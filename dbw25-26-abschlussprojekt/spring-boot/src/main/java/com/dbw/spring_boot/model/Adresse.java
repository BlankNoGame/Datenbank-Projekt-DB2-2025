package com.dbw.spring_boot.model;

public record Adresse(
        Integer adresseId,
        boolean aktiv,
        String strasse,
        String hausnummer,
        int plz,
        String ort,
        String land
) {
}
