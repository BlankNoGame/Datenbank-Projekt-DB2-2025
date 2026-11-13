package com.dbw.spring_boot.model;

public record KundeSumBestellung(int kundeId, String email, int anzahlBestellungen, double gesamtsumme) {
}
