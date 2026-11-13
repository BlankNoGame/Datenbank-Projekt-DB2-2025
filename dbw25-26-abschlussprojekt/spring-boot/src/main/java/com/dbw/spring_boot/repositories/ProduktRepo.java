package com.dbw.spring_boot.repositories;

import com.dbw.spring_boot.model.Produkt;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProduktRepo {

    private final JdbcTemplate jdbc;

    public ProduktRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Produkt> mapper = (rs, i) ->
            new Produkt(
                    rs.getString("sku"),
                    rs.getString("name"),
                    rs.getBigDecimal("preis"),
                    rs.getInt("lagerbestand"),
                    rs.getInt("angelegt_von")
            );

    public List<Produkt> findAll() {
        return jdbc.query("SELECT * FROM produkt", mapper);
    }

    public Produkt findBySku(String sku) {
        return jdbc.queryForObject(
                "SELECT * FROM produkt WHERE sku = ?",
                mapper,
                sku
        );
    }

    public void create(Produkt p) {
        jdbc.update("""
            INSERT INTO produkt (sku, name, preis, lagerbestand, angelegt_von)
            VALUES (?, ?, ?, ?, ?)
        """,
                p.sku(),
                p.name(),
                p.preis(),
                p.lagerbestand(),
                p.angelegtVon()
        );
    }

    public void updateLagerbestand(String sku, int bestand) {
        jdbc.update(
                "UPDATE produkt SET lagerbestand = ? WHERE sku = ?",
                bestand, sku
        );
    }

    public void delete(String sku) {
        jdbc.update("DELETE FROM produkt WHERE sku = ?", sku);
    }

}
