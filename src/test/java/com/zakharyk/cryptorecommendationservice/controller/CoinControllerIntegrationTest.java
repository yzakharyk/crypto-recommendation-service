package com.zakharyk.cryptorecommendationservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = {"app.coins.csv.folder = csv/prices"})
@AutoConfigureMockMvc
class CoinControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getCoin() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/coins/BTC"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getHighestNormalizedForDate() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/coins/normalized/highest").param("specifiedDate", "2022-01-01"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void coinsByNormalizedRange() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/coins/normalized").param("specifiedDate", "2022-01-01"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
