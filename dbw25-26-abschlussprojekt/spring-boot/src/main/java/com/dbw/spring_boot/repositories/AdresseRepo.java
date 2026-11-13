package com.dbw.spring_boot.repositories;

import com.dbw.spring_boot.model.Adresse;
import com.dbw.spring_boot.model.AdresseCreate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdresseRepo {

    private final JdbcTemplate jdbc;

    public AdresseRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Adresse> mapper = (rs, i) ->
            new Adresse(
                    rs.getInt("adresse_id"),
                    rs.getBoolean("aktiv"),
                    rs.getString("strasse"),
                    rs.getString("hausnummer"),
                    rs.getInt("plz"),
                    rs.getString("ort"),
                    rs.getString("land")
            );

    public List<Adresse> findAll() {
        return jdbc.query("""
            SELECT adresse_id, aktiv, strasse, hausnummer, plz, ort, land
            FROM adresse
        """, mapper);
    }

    public Adresse findById(int id) {
        return jdbc.queryForObject("""
            SELECT adresse_id, aktiv, strasse, hausnummer, plz, ort, land
            FROM adresse
            WHERE adresse_id = ?
        """, mapper, id);
    }

    public void create(AdresseCreate a) {
        jdbc.update("""
            INSERT INTO adresse (aktiv, strasse, hausnummer, plz, ort, land)
            VALUES (?, ?, ?, ?, ?, ?)
        """,
                a.aktiv(),
                a.strasse(),
                a.hausnummer(),
                Integer.parseInt(a.plz()),
                a.ort(),
                a.land()
        );
    }

    public void update(int id, AdresseCreate a) {
        jdbc.update("""
            UPDATE adresse
            SET aktiv=?, strasse=?, hausnummer=?, plz=?, ort=?, land=?
            WHERE adresse_id=?
        """,
                a.aktiv(),
                a.strasse(),
                a.hausnummer(),
                Integer.parseInt(a.plz()),
                a.ort(),
                a.land(),
                id
        );
    }
}
