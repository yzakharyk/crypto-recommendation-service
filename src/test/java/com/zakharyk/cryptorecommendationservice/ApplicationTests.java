package com.zakharyk.cryptorecommendationservice;

import com.zakharyk.cryptorecommendationservice.controller.CoinController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = {"app.coins.csv.folder = csv/prices"})
class ApplicationTests {
    @Autowired
    private CoinController coinController;
    @Test
    void contextLoads() {
        assertNotNull(coinController);
    }

}
