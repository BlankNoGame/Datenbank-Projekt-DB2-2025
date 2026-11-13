package com.dbw.spring_boot.model;

import java.util.List;

public record KundeCreate(String email,
                          String vorname,
                          String nachname,
                          String passwort ){
}
