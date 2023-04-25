package com.zakharyk.cryptorecommendationservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CryptoCoinDto (String symbol, BigDecimal price, LocalDateTime timestamp) {
}
