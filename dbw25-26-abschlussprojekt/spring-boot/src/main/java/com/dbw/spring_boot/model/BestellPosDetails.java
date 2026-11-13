package com.dbw.spring_boot.model;

import java.math.BigDecimal;

public record BestellPosDetails (Integer positionsId,
                                 Integer bestellungId,
                                 Produkt produkt,
                                 Integer menge,
                                 BigDecimal gesamtpreis) {
}
