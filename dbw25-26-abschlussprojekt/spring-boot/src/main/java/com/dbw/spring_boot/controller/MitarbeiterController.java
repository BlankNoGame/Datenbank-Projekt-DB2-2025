package com.dbw.spring_boot.controller;

import com.dbw.spring_boot.model.Mitarbeiter;
import com.dbw.spring_boot.model.MitarbeiterCreate;
import com.dbw.spring_boot.repositories.MitarbeiterRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mitarbeiter")
public class MitarbeiterController {

    private final MitarbeiterRepo repo;

    public MitarbeiterController(MitarbeiterRepo repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Mitarbeiter> getMitarbeiter(@RequestParam(required = false) Integer id) {

        if (id != null) {
            Mitarbeiter m = repo.findById(id);
            return m != null ? List.of(m) : List.of();
        }

        return repo.findAll();

    }

    @PostMapping
    public void create(@RequestBody MitarbeiterCreate m) {
        repo.create(m);
    }

    @DeleteMapping
    public void delete(@RequestParam Integer id) {
        repo.delete(id);
    }

}
