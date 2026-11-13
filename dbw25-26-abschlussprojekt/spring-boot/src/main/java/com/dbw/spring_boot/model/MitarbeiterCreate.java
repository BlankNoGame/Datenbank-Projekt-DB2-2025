package com.dbw.spring_boot.model;

public record MitarbeiterCreate(String passwort, String email,
                                String vorname, String nachname) {
}
