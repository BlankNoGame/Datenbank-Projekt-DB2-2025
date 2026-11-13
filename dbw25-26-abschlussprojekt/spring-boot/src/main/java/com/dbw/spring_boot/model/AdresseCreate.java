package com.dbw.spring_boot.model;

public record AdresseCreate (boolean aktiv,
                             String strasse,
                             String hausnummer,
                             String plz,
                             String ort,
                             String land) {
}
