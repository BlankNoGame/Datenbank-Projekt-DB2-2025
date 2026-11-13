package com.dbw.spring_boot.repositories;

import com.dbw.spring_boot.model.Kunde;
import com.dbw.spring_boot.model.KundeCreate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class KundenRepo {

    private final JdbcTemplate jdbc;

    public KundenRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> findAllRaw() {
        return jdbc.queryForList("""
            SELECT
              k.kunde_id,
              k.email,
              k.vorname,
              k.nachname,
              k.passwort,
              a.adresse_id,
              a.aktiv,
              a.strasse,
              a.hausnummer,
              a.plz,
              a.ort,
              a.land,
              ka.typ
            FROM kunde k
            LEFT JOIN kunde_hat_adressen ka ON k.kunde_id = ka.kunde_id
            LEFT JOIN adresse a ON ka.adresse_id = a.adresse_id
            ORDER BY k.kunde_id
        """);
    }

    public Kunde findById(int id) {
        List<Kunde> list = jdbc.query("""
        SELECT k.kunde_id, k.email, k.vorname, k.nachname, k.passwort
        FROM kunde k
        WHERE k.kunde_id = ?
    """,
                (rs, rowNum) -> new Kunde(
                        rs.getInt("kunde_id"),
                        rs.getString("email"),
                        rs.getString("vorname"),
                        rs.getString("nachname"),
                        rs.getString("passwort"),
                        new ArrayList<>() // Adressen hier ggf. separat laden, wenn nötig
                ), id);

        return list.isEmpty() ? null : list.get(0);
    }

    public void create(KundeCreate k) {
        jdbc.update("""
            INSERT INTO kunde (email, vorname, nachname, passwort)
            VALUES (?, ?, ?, ?)
        """,
                k.email(),
                k.vorname(),
                k.nachname(),
                k.passwort()
        );
    }

    public void update(int id, KundeCreate k) {
        jdbc.update("""
            UPDATE kunde
            SET email = ?, vorname = ?, nachname = ?, passwort = ?
            WHERE kunde_id = ?
        """,
                k.email(),
                k.vorname(),
                k.nachname(),
                k.passwort(),
                id
        );
    }

    // Login Kunde
    public Kunde login(String email, String passwort) {
        List<Kunde> list = jdbc.query("""
            SELECT kunde_id, email, vorname, nachname, passwort
            FROM kunde
            WHERE email = ? AND passwort = ?
        """,
                (rs, i) -> new Kunde(
                        rs.getInt("kunde_id"),
                        rs.getString("email"),
                        rs.getString("vorname"),
                        rs.getString("nachname"),
                        rs.getString("passwort"),
                        new ArrayList<>()
                ),
                email, passwort
        );

        return list.isEmpty() ? null : list.get(0);
    }



}
