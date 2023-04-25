package com.zakharyk.cryptorecommendationservice.controller;

import com.zakharyk.cryptorecommendationservice.model.CryptoCoinDto;
import com.zakharyk.cryptorecommendationservice.model.Filter;
import com.zakharyk.cryptorecommendationservice.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coins")
@RequiredArgsConstructor
public class CoinController {
    private final CoinService coinService;

    @GetMapping("/{symbol}")
    public CryptoCoinDto getCoin(@PathVariable String symbol, @RequestParam(defaultValue = "NEWEST") Filter filter) {
        return coinService.getCoinData(symbol, filter);
    }
}
