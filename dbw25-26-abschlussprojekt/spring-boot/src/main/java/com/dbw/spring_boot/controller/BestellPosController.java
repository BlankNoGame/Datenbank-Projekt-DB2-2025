package com.dbw.spring_boot.controller;

import com.dbw.spring_boot.model.BestellPosCreate;
import com.dbw.spring_boot.model.BestellPos;
import com.dbw.spring_boot.repositories.BestellposRepo;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bestellpositionen")
public class BestellPosController {

    private final BestellposRepo repo;

    public BestellPosController(BestellposRepo repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<BestellPos> get(@RequestParam(required = false) Integer id) {

        List<Map<String, Object>> rows = repo.findAllRaw();
        List<BestellPos> result = new ArrayList<>();

        for (Map<String, Object> r : rows) {
            BestellPos bp = new BestellPos(((Number) r.get("position_id")).intValue(),
                    ((Number) r.get("bestellung_id")).intValue(), (String) r.get("sku"),
                    ((Number) r.get("menge")).intValue(), ((BigDecimal) r.get("preis"))
                            .multiply(BigDecimal.valueOf(((Number) r.get("menge")).intValue())));

            if (id == null || bp.positionsId().equals(id)) {
                result.add(bp);
            }
        }
        return result;
    }

    @PostMapping
    public void create(@RequestBody BestellPosCreate dto) {
        repo.create(dto.bestellungId(), dto.sku(), dto.menge());
    }

    @DeleteMapping
    public void delete(@RequestParam Integer id) {
        repo.delete(id);
    }



}
