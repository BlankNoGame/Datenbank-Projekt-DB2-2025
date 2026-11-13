package com.dbw.spring_boot.model;

import java.time.Instant;

public record BestellungCreate (Integer kundeId,
                               Integer personalnr,
                               Instant datum,
                               String status) {
}
