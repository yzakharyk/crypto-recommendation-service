package com.zakharyk.cryptorecommendationservice.model;

import java.math.BigDecimal;


public record CoinValueDto(String symbol, BigDecimal value) {
}
