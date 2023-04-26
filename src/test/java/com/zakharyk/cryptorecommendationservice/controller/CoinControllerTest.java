package com.zakharyk.cryptorecommendationservice.controller;

import com.zakharyk.cryptorecommendationservice.model.Filter;
import com.zakharyk.cryptorecommendationservice.service.CoinService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CoinControllerTest {
    @Mock
    private CoinService coinService;
    @InjectMocks
    private CoinController coinController;

    @Test
    void getCoin() {
        coinController.getCoin("BTC", Filter.MAX);
        verify(coinService).getCoin("BTC", Filter.MAX);
    }

    @Test
    void coinsByNormalizedRange() {
        coinController.coinsByNormalizedRange();
        verify(coinService).calculateNormalizedRange();
    }

    @Test
    void getHighestNormalizedForDate() {
        var now = LocalDate.now();
        coinController.getHighestNormalizedForDate(now);
        verify(coinService).getHighestNormalizedForDate(now);
    }
}