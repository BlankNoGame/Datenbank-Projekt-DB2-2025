package com.dbw.spring_boot.model;

import java.math.BigDecimal;

public record BestellPos(
        Integer positionsId,
        Integer bestellungId,
        String produktSku,
        Integer menge,
        BigDecimal gesamtpreis
) {
}
