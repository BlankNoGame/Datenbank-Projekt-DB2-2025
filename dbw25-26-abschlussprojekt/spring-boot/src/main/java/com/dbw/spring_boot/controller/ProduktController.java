package com.dbw.spring_boot.controller;

import com.dbw.spring_boot.model.Produkt;
import com.dbw.spring_boot.repositories.ProduktRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produkte")
public class ProduktController {

    private final ProduktRepo repo;

    public ProduktController(ProduktRepo repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Produkt> get(@RequestParam(required = false) String sku) {
        if (sku != null) {
            Produkt produkt = repo.findBySku(sku);
            return produkt != null ? List.of(produkt) : List.of();
        }
        return repo.findAll();
    }

    @PostMapping
    public void create(@RequestBody Produkt p) {
        repo.create(p);
    }

    @PatchMapping("/{sku}")
    public void update(@PathVariable String sku,
                       @RequestParam int lagerbestand) {
        repo.updateLagerbestand(sku, lagerbestand);
    }

    @DeleteMapping
    public void delete(@RequestParam String sku) {
        repo.delete(sku);
    }


}
