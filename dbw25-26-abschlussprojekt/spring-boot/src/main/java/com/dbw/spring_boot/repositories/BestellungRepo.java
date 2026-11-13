package com.dbw.spring_boot.repositories;

import com.dbw.spring_boot.model.Bestellung;
import com.dbw.spring_boot.model.BestellungCreate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public class BestellungRepo {

    private final JdbcTemplate jdbc;

    public BestellungRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> findAllRaw() {
        return jdbc.queryForList("""
            SELECT
              b.bestellung_id,
              b.kunde_id,
              b.mitarbeiterzuweis,
              b.datum,
              b.status,

              bp.position_id,
              bp.menge,

              p.sku,
              p.name,
              p.preis,
              p.lagerbestand,
              p.angelegt_von
            FROM bestellung b
            LEFT JOIN bestellposition bp ON b.bestellung_id = bp.bestellung_id
            LEFT JOIN produkt p ON bp.sku = p.sku
            ORDER BY b.bestellung_id
        """);
    }

    public void create(BestellungCreate b) {
        jdbc.update("""
            INSERT INTO bestellung (kunde_id, mitarbeiterzuweis, datum, status)
            VALUES (?, ?, ?, ?)
        """, b.kundeId(), b.personalnr(), Timestamp.from(b.datum()), b.status()
        );
    }

    public void delete(int id) {
        jdbc.update(
                "DELETE FROM bestellung WHERE bestellung_id = ?", id
        );
    }

}
