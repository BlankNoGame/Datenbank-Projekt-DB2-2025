package com.dbw.spring_boot.controller;

import com.dbw.spring_boot.model.Adresse;
import com.dbw.spring_boot.model.AdresseCreate;
import com.dbw.spring_boot.repositories.AdresseRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adressen")
public class AdresseController {

    private final AdresseRepo repo;

    public AdresseController(AdresseRepo repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Adresse> get(@RequestParam(required = false) Integer id) {
        if (id != null) {
            Adresse adr = repo.findById(id);
            return adr != null ? List.of(adr) : List.of();
        }
        return repo.findAll();
    }

    @PostMapping
    public void create(@RequestBody AdresseCreate adr) {
        repo.create(adr);
    }

    @PutMapping
    public void update(@RequestParam Integer id, @RequestBody AdresseCreate adr) {
        repo.update(id, adr);
    }

}
