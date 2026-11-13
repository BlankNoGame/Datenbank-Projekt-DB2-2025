package com.dbw.spring_boot.controller;

import com.dbw.spring_boot.model.Adresse;
import com.dbw.spring_boot.model.Kunde;
import com.dbw.spring_boot.model.KundeAdresse;
import com.dbw.spring_boot.model.KundeCreate;
import com.dbw.spring_boot.repositories.KundenRepo;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kunden")
public class KundenController {

    private final KundenRepo repo;

    public KundenController(KundenRepo repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Kunde> get(@RequestParam(required = false) Integer id,
                           @RequestParam(required = false) String email) {
        List<Map<String, Object>> rows = repo.findAllRaw();
        Map<Integer, Kunde> kunden = new LinkedHashMap<>();

        for (Map<String, Object> r: rows) {
            Integer kID = ((Number) r.get("kunde_id")).intValue();

            kunden.putIfAbsent(kID, new Kunde(kID, (String) r.get("email"), (String) r.get("vorname"),
                    (String) r.get("nachname"), (String) r.get("passwort"), new ArrayList<>()));

            if (r.get("adresse_id") != null) {
                Adresse adresse = new Adresse(((Number) r.get("adresse_id")).intValue(), (Boolean) r.get("aktiv"),
                        (String) r.get("strasse"), (String) r.get("hausnummer"), ((Number) r.get("plz")).intValue(),
                        (String) r.get("ort"), (String) r.get("land"));

                kunden.get(kID).adressen().add(new KundeAdresse(adresse, (String) r.get("typ")));
            }
        }

        return kunden.values().stream()
                .filter(k -> id == null || k.kundeId().equals(id))
                .filter(k -> email == null || k.email().equalsIgnoreCase(email))
                .toList();
    }

    @PostMapping
    public void create(@RequestBody KundeCreate k) {
        repo.create(k);
    }

    @PutMapping
    public void update(@RequestParam Integer id, @RequestBody KundeCreate k) {

        Kunde kunde = repo.findById(id);
        if(kunde == null) {
            throw new IllegalArgumentException("Kunde nicht gefunden");
        }

        repo.update(id,k);

    }

}
