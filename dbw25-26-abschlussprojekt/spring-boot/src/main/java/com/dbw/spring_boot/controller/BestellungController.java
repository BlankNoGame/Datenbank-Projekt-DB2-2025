package com.dbw.spring_boot.controller;

import com.dbw.spring_boot.model.BestellPosDetails;
import com.dbw.spring_boot.model.Bestellung;
import com.dbw.spring_boot.model.BestellungCreate;
import com.dbw.spring_boot.model.Produkt;
import com.dbw.spring_boot.repositories.BestellungRepo;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bestellungen")
public class BestellungController {

    private final BestellungRepo repo;

    public BestellungController(BestellungRepo repo){
        this.repo = repo;
    }

    @GetMapping
    public List<Bestellung> get(
            @RequestParam(required = false) Integer id) {

        List<Map<String, Object>> rows = repo.findAllRaw();
        Map<Integer, Bestellung> bestellungen = new LinkedHashMap<>();

        for (Map<String, Object> r : rows) {

            Integer bid = ((Number) r.get("bestellung_id")).intValue();

            bestellungen.putIfAbsent(bid, new Bestellung(
                    bid,
                    ((Number) r.get("kunde_id")).intValue(),
                    ((Number) r.get("mitarbeiterzuweis")).intValue(),
                    ((Timestamp) r.get("datum")).toInstant(),
                    (String) r.get("status"),
                    new ArrayList<>()
            ));

            if (r.get("position_id") != null) {
                Produkt produkt = new Produkt(
                        (String) r.get("sku"),
                        (String) r.get("name"),
                        (BigDecimal) r.get("preis"),
                        ((Number) r.get("lagerbestand")).intValue(),
                        ((Number) r.get("angelegt_von")).intValue()
                );

                bestellungen.get(bid).positionen().add(
                        new BestellPosDetails(
                                ((Number) r.get("position_id")).intValue(),
                                bid,
                                produkt,
                                ((Number) r.get("menge")).intValue(),
                                ((BigDecimal) r.get("preis"))
                                        .multiply(BigDecimal.valueOf(((Number) r.get("menge")).intValue()))
                        )
                );
            }
        }

        if (id != null) {
            return bestellungen.containsKey(id)
                    ? List.of(bestellungen.get(id))
                    : List.of();
        }

        return new ArrayList<>(bestellungen.values());
    }

    @PostMapping
    public void create(@RequestBody BestellungCreate b) {
        repo.create(b);
    }

    @DeleteMapping
    public void delete(@RequestParam Integer id) {
        repo.delete(id);
    }


}
