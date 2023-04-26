package com.zakharyk.cryptorecommendationservice.controller;

import com.zakharyk.cryptorecommendationservice.annotation.ApplyRateLimit;
import com.zakharyk.cryptorecommendationservice.model.CoinValueDto;
import com.zakharyk.cryptorecommendationservice.model.CryptoCoinDto;
import com.zakharyk.cryptorecommendationservice.model.Filter;
import com.zakharyk.cryptorecommendationservice.service.CoinService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/coins")
@RequiredArgsConstructor
public class CoinController {
    private final CoinService coinService;

    @GetMapping("/{symbol}")
    @ApplyRateLimit(callsPerMinuteAllowed = 10)
    public CryptoCoinDto getCoin(@PathVariable String symbol, @RequestParam(defaultValue = "NEWEST") Filter filter) {
        return coinService.getCoin(symbol, filter);
    }

    @GetMapping("/normalized")
    @ApplyRateLimit(callsPerMinuteAllowed = 3)
    public List<CoinValueDto> coinsByNormalizedRange() {
        return coinService.calculateNormalizedRange();
    }

    @GetMapping("/normalized/highest")
    @ApplyRateLimit(callsPerMinuteAllowed = 3)
    public CoinValueDto getHighestNormalizedForDate(@Parameter(description = "Date in yyyy-MM-dd format")
                                                    @RequestParam LocalDate specifiedDate) {
        return coinService.getHighestNormalizedForDate(specifiedDate);
    }
}
