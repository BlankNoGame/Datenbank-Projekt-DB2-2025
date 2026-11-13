package com.dbw.spring_boot.repositories;

import com.dbw.spring_boot.model.Mitarbeiter;
import com.dbw.spring_boot.model.MitarbeiterCreate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MitarbeiterRepo {

    private final JdbcTemplate jdbc;

    public MitarbeiterRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Mitarbeiter> mapper = (rs, i) -> new Mitarbeiter(
            rs.getInt("personal_nr"),
            rs.getString("passwort"),
            rs.getString("email"),
            rs.getString("vorname"),
            rs.getString("nachname")
    );

    public List<Mitarbeiter> findAll() {
        return jdbc.query("""
            SELECT personal_nr, passwort, email, vorname, nachname
            FROM mitarbeiter
        """, mapper);
    }

    public Mitarbeiter findById(int id) {
        return jdbc.queryForObject("""
            SELECT personal_nr, passwort, email, vorname, nachname
            FROM mitarbeiter
            WHERE personal_nr = ?
        """, mapper, id);
    }

    public void create(MitarbeiterCreate m) {
        jdbc.update("""
            INSERT INTO mitarbeiter (passwort, email, vorname, nachname)
            VALUES (?, ?, ?, ?)
        """,
                m.passwort(),
                m.email(),
                m.vorname(),
                m.nachname()
        );
    }

    public void delete(int id) {
        jdbc.update(
                "DELETE FROM mitarbeiter WHERE personal_nr = ?",
                id
        );
    }

    public Mitarbeiter login(int personalNr, String passwort) {
        List<Mitarbeiter> list = jdbc.query("""
            SELECT personal_nr, passwort, email, vorname, nachname
            FROM mitarbeiter
            WHERE personal_nr = ? AND passwort = ?
        """, mapper, personalNr, passwort);

        return list.isEmpty() ? null : list.get(0);
    }

}
