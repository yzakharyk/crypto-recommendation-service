package com.zakharyk.cryptorecommendationservice.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class CryptoCoin {
   private String symbol;
   private List<Price> prices;

    public CryptoCoin(String symbol, List<Price> prices) {
        this.symbol = symbol;
        this.prices = prices;
    }

    @Data
    public static class Price {
        private LocalDateTime timestamp;
        private BigDecimal value;

        public Price(LocalDateTime timestamp, BigDecimal value) {
            this.timestamp = timestamp;
            this.value = value;
        }
    }
}
