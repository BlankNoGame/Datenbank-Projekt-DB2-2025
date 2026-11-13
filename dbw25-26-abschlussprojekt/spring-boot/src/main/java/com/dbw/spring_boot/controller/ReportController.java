package com.dbw.spring_boot.controller;


import com.dbw.spring_boot.model.KundeSumBestellung;
import com.dbw.spring_boot.repositories.ReportRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportRepo repo;

    public ReportController(ReportRepo repo) {
        this.repo = repo;
    }

    @GetMapping("/kunde/summe-anzahl-bestellungen")
    public List<KundeSumBestellung> getKundenReport() {
        return repo.getKundenReport();
    }

    @GetMapping("/produkt/verkaufszahlen")
    public List<Map<String, Object>> get() {
        return repo.findVerkaufszahlen();
    }

    @GetMapping("/mitarbeiter/uebersicht")
    public List<Map<String, Object>> getUebersicht() {
        return repo.findUebersicht();
    }

    @GetMapping("/mitarbeiter/bestellstatus-uebersicht")
    public List<Map<String, Object>> getBestllstatusUebersicht() {
        return repo.findStatusUebersicht();
    }
}
