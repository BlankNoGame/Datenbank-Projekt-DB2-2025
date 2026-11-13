package com.dbw.spring_boot.model;

import java.time.Instant;
import java.util.List;

public record Bestellung(
        Integer bestellungId,
        Integer kundeId,
        Integer personalnr,
        Instant datum,
        String status,
        List<BestellPosDetails> positionen
) {
}
