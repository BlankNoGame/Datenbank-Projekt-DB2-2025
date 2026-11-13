CREATE TABLE adresse (
    adresse_id SERIAL PRIMARY KEY,
    aktiv BOOLEAN NOT NULL,
    strasse VARCHAR(60) NOT NULL,
    hausnummer VARCHAR(10) NOT NULL,
    plz BIGINT NOT NULL,
    ort VARCHAR(100) NOT NULL,
    land VARCHAR(100) NOT NULL,

    CHECK(strasse ~ '^[A-ZÄÖÜ][a-zäöüß]+$'),
    CHECK(hausnummer ~ '[0-9]+[a-z]?$'),
    CHECK(plz >= 0 AND plz < 100000000000)
);

CREATE TABLE kunde (
    kunde_id SERIAL PRIMARY KEY,
    email VARCHAR(256) UNIQUE NOT NULL,
    vorname VARCHAR(32) NOT NULL,
    nachname VARCHAR(32) NOT NULL,
    passwort VARCHAR(20) NOT NULL,

    CHECK(email ~ '^[^@]+@[^@]+\.[^@]+$'),
    CHECK(vorname ~ '^[A-ZÄÖÜ][a-zäöü]+$'),
    CHECK(nachname ~ '^[A-ZÄÖÜ][a-zäöü]+$'),
    CHECK(passwort ~ '^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{5,20}$')
);

CREATE TABLE mitarbeiter (
    personal_nr SERIAL PRIMARY KEY,
    vorname VARCHAR(32) NOT NULL,
    nachname VARCHAR(32) NOT NULL,
    email VARCHAR(256) NOT NULL,
    passwort VARCHAR(20) NOT NULL,

    CHECK(passwort ~ '^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{5,20}$')
);

CREATE TABLE kunde_hat_adressen (
    kunde_id INT NOT NULL,
    adresse_id INT NOT NULL,
    typ VARCHAR(20) NOT NULL,
    PRIMARY KEY (kunde_id, adresse_id),
    FOREIGN KEY (kunde_id) REFERENCES kunde(kunde_id) ON DELETE CASCADE,
    FOREIGN KEY (adresse_id) REFERENCES adresse(adresse_id) ON DELETE CASCADE,
    CHECK (typ IN  ('Lieferadresse' , 'Rechnungsadresse'))
);

CREATE TABLE bestellung (
    bestellung_id SERIAL PRIMARY KEY,
    kunde_id INT NOT NULL,
    mitarbeiterzuweis INT NOT NULL,
    datum TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (kunde_id) REFERENCES kunde(kunde_id) ON DELETE CASCADE,
    FOREIGN KEY (mitarbeiterzuweis) REFERENCES mitarbeiter(personal_nr) ON DELETE CASCADE,
    CHECK(status IN ('neu', 'bezahlt', 'versendet', 'abgeschlossen', 'storniert'))
);

CREATE TABLE produkt (
    sku VARCHAR(200) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    preis DECIMAL(10,2) NOT NULL,
    lagerbestand BIGINT NOT NULL,
    angelegt_von INT NOT NULL,
    FOREIGN KEY (angelegt_von) REFERENCES mitarbeiter(personal_nr) ON DELETE CASCADE,
    CHECK(lagerbestand >= 0),
    CHECK(sku ~ '^[A-Z0-9-]+$')
);

CREATE TABLE bestellposition(
    position_id SERIAL PRIMARY KEY,
    sku VARCHAR(200) NOT NULL,
    bestellung_id INT NOT NULL,
    menge BIGINT NOT NULL,
    FOREIGN KEY (sku) REFERENCES produkt(sku) ON DELETE CASCADE,
    FOREIGN KEY (bestellung_id) REFERENCES bestellung(bestellung_id) ON DELETE CASCADE,
    CHECK(menge > 0)
);

CREATE VIEW v_kunde_summe_anzahl_bestellungen AS
    SELECT k.kunde_id AS "kundeId",
           k.email AS email,
           COUNT(DISTINCT b.bestellung_id) AS "anzahlBestellungen",
           COALESCE(SUM(p.preis * bp.menge),0) AS gesamtsumme
    FROM kunde k
        LEFT JOIN bestellung b
            ON k.kunde_id = b.kunde_id
        LEFT JOIN bestellposition bp
            ON b.bestellung_id = bp.bestellung_id
        LEFT JOIN produkt p
                  ON bp.sku = p.sku
        GROUP BY k.kunde_id, k.email
        ORDER BY k.kunde_id;

CREATE VIEW v_produkt_verkaufszahlen AS
    SELECT p.sku AS sku,
           p.name AS name,

           COALESCE(SUM(
                CASE
                    WHEN b.status NOT IN ('neu', 'storniert')
                    THEN bp.menge
                    ELSE 0
                END), 0) AS "gesamtVerkaufteMenge",

           COALESCE(SUM(
                CASE
                    WHEN b.status NOT IN ('neu', 'storniert')
                    THEN bp.menge * p.preis
                    ELSE 0
                END), 0) AS umsatz,

            COUNT(DISTINCT
                CASE
                    WHEN b.status NOT IN ('neu', 'storniert')
                    THEN b.bestellung_id
                END) AS "anzahlBestellungen"

    FROM produkt p
        LEFT JOIN bestellposition bp ON p.sku = bp.sku
        LEFT JOIN bestellung b ON bp.bestellung_id = b.bestellung_id
        GROUP BY p.sku, p.name
        ORDER BY "gesamtVerkaufteMenge" DESC;


CREATE VIEW v_mitarbeiter_uebersicht AS
    SELECT m.personal_nr AS "personalNr",
           COUNT(DISTINCT b.bestellung_id) AS "anzahlVerwalteterBestellungen",
           COUNT(DISTINCT p.sku) AS "anzahlAngelegterProdukte"
    FROM mitarbeiter m
        LEFT JOIN bestellung b ON m.personal_nr = b.mitarbeiterzuweis
        LEFT JOIN produkt p ON m.personal_nr = p.angelegt_von
        GROUP BY m.personal_nr
        ORDER BY m.personal_nr;


CREATE VIEW v_mitarbeiter_bestellstatus_uebersicht AS
    SELECT m.personal_nr AS "personalNr",
           s.status AS status,
           COUNT(b.bestellung_id) AS "anzahlBestellungen"
    FROM mitarbeiter m
        CROSS JOIN (SELECT UNNEST(ARRAY['neu','bezahlt','versendet','abgeschlossen','storniert']) AS status) s
        LEFT JOIN bestellung b ON b.mitarbeiterzuweis = m.personal_nr AND b.status = s.status
        GROUP BY m.personal_nr, s.status
        ORDER BY m.personal_nr, s.status;



CREATE OR REPLACE FUNCTION check_status()
    RETURNS TRIGGER AS $$

DECLARE
       pos RECORD;
BEGIN

    IF NEW.status = 'storniert' AND OLD.status NOT IN ('neu', 'bezahlt') THEN
       RAISE EXCEPTION 'Status kann nur von neu oder bezahlt auf storniert wechseln';
    END IF;

    IF NEW.status = 'storniert' THEN
        FOR pos IN SELECT sku, menge FROM bestellposition WHERE bestellung_id = NEW.bestellung_id LOOP
            UPDATE produkt SET lagerbestand = lagerbestand + pos.menge WHERE sku = pos.sku;
        END LOOP;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION trg_bestellpos_lager()
RETURNS TRIGGER AS $$

DECLARE
    produkt_lager BIGINT;
    bestell_status TEXT;
    diff BIGINT;
BEGIN

    SELECT status INTO bestell_status FROM bestellung WHERE bestellung_id = COALESCE(NEW.bestellung_id, OLD.bestellung_id);

    IF bestell_status = 'storniert' THEN
        RETURN COALESCE(NEW, OLD);
    END IF;

    IF TG_OP = 'INSERT' THEN
        diff := NEW.menge;

    ELSIF TG_OP = 'UPDATE' THEN
        diff := NEW.menge - OLD.menge;
        IF diff = 0 THEN
           RETURN NEW;
        END IF;
    END IF;

    SELECT lagerbestand INTO produkt_lager FROM produkt WHERE sku = COALESCE(NEW.sku, OLD.sku) FOR UPDATE;

    IF diff > 0 AND diff > produkt_lager THEN
        RAISE EXCEPTION 'Nicht genügend Lagerbestand für Produkt %. Benötigt: %, Verfügbar: %',
           COALESCE(NEW.sku, OLD.sku), diff, produkt_lager;
    END IF;

    UPDATE produkt SET lagerbestand = lagerbestand - diff WHERE sku = COALESCE(NEW.sku, OLD.sku);

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION bestellpos_delete_check()
       RETURNS TRIGGER AS $$
DECLARE
       bestell_status TEXT;
BEGIN
    SELECT status INTO bestell_status FROM bestellung WHERE bestellung_id = OLD.bestellung_id;

    IF bestell_status = 'storniert' THEN
       RETURN OLD;
    END IF;

    UPDATE produkt SET lagerbestand = lagerbestand + OLD.menge WHERE sku = OLD.sku;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER check_storniert
    BEFORE UPDATE OF status ON bestellung
FOR EACH ROW
EXECUTE FUNCTION check_status();


CREATE TRIGGER trg_bestellpos_lager_insert_update
BEFORE INSERT OR UPDATE ON bestellposition
    FOR EACH ROW
EXECUTE FUNCTION trg_bestellpos_lager();


CREATE TRIGGER trg_bestellpos_delete
BEFORE DELETE ON bestellposition
    FOR EACH ROW
EXECUTE FUNCTION bestellpos_delete_check();