package com.dbw.spring_boot.repositories;

import com.dbw.spring_boot.model.KundeSumBestellung;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ReportRepo {

    private final JdbcTemplate jdbc;

    public ReportRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<KundeSumBestellung> getKundenReport() {
        return jdbc.query("""
            SELECT
                "kundeId",
                email,
                "anzahlBestellungen",
                gesamtsumme
            FROM v_kunde_summe_anzahl_bestellungen
        """, (rs, i) -> new KundeSumBestellung(
                rs.getInt("kundeId"),
                rs.getString("email"),
                rs.getInt("anzahlBestellungen"),
                rs.getDouble("gesamtsumme")
        ));
    }

    public List<Map<String, Object>> findVerkaufszahlen() {
        return jdbc.queryForList( "SELECT * FROM v_produkt_verkaufszahlen");
    }

    public List<Map<String, Object>> findUebersicht() {
        return jdbc.queryForList("SELECT * FROM v_mitarbeiter_uebersicht");
    }

    public List<Map<String, Object>> findStatusUebersicht() {
        return jdbc.queryForList("SELECT * FROM v_mitarbeiter_bestellstatus_uebersicht");
    }

}
