package com.dbw.spring_boot.controller;


import com.dbw.spring_boot.model.Kunde;
import com.dbw.spring_boot.model.KundeLoginReq;
import com.dbw.spring_boot.model.Mitarbeiter;
import com.dbw.spring_boot.model.MitarbeiterLoginReq;
import com.dbw.spring_boot.repositories.KundenRepo;
import com.dbw.spring_boot.repositories.MitarbeiterRepo;
import jakarta.validation.constraints.Null;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final MitarbeiterRepo mitarbeiterRepo;
    private final KundenRepo kundenRepo;

    public LoginController(MitarbeiterRepo mitarbeiterRepo, KundenRepo kundenRepo) {
        this.mitarbeiterRepo = mitarbeiterRepo;
        this.kundenRepo = kundenRepo;
    }

    @PostMapping("/mitarbeiter")
    public ResponseEntity<Mitarbeiter> loginMitarbeiter(@RequestBody MitarbeiterLoginReq req) {
        if (req.personalNr() == null || req.passwort() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mitarbeiter());
        }

        Mitarbeiter m = mitarbeiterRepo.login(req.personalNr(), req.passwort());
        if (m != null) {
            m.setPasswort(null);
            return ResponseEntity.ok(m);
        }

        Mitarbeiter fail = new Mitarbeiter(null,null,null,null,null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(fail);
    }

    @PostMapping("/kunde")
    public ResponseEntity<Kunde> loginKunde(@RequestBody KundeLoginReq req) {
        if (req.email() == null || req.passwort() == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Kunde(null, null, null, null, null, List.of()));
        }

        Kunde k = kundenRepo.login(req.email(), req.passwort());

        if(k != null) {
            return ResponseEntity.ok(new Kunde(
                    k.kundeId(),
                    k.email(),
                    k.vorname(),
                    k.nachname(),
                    null,
                    k.adressen()
            ));
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new Kunde(null, null, null, null, null, List.of()));

    }
}
