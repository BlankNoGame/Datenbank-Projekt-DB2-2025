package com.dbw.spring_boot.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class BestellposRepo {

    private final JdbcTemplate jdbc;

    public BestellposRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> findAllRaw() {
        return jdbc.queryForList("""
            SELECT
              bp.position_id,
              bp.bestellung_id,
              bp.sku,
              bp.menge,
              p.preis
            FROM bestellposition bp
            JOIN produkt p ON bp.sku = p.sku
        """);
    }

    public void create(int bestellungId, String sku, int menge) {
        jdbc.update("""
            INSERT INTO bestellposition (bestellung_id, sku, menge)
            VALUES (?, ?, ?)
        """, bestellungId, sku, menge);
    }

    public void delete(int id) {
        jdbc.update(
                "DELETE FROM bestellposition WHERE position_id = ?",
                id
        );
    }

}
