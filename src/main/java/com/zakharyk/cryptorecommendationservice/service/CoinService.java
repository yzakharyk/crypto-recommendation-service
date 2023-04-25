package com.zakharyk.cryptorecommendationservice.service;

import com.zakharyk.cryptorecommendationservice.model.CryptoCoin;
import com.zakharyk.cryptorecommendationservice.model.CryptoCoinDto;
import com.zakharyk.cryptorecommendationservice.model.Filter;
import com.zakharyk.cryptorecommendationservice.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class CoinService {
    private final CoinRepository coinRepository;

    public CryptoCoinDto getCoinData(String symbol, Filter filter) {
        var cryptoCoin = coinRepository.getCryptoCoin(symbol)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (CollectionUtils.isEmpty(cryptoCoin.getPrices())) {
            return new CryptoCoinDto(symbol, null, null);
        }

        var priceStream = cryptoCoin.getPrices().stream();

        CryptoCoin.Price coinPrice;
        switch (filter) {
            case OLDEST -> {
                coinPrice = priceStream.min(Comparator.comparing(CryptoCoin.Price::getTimestamp)).get();
            }
            case NEWEST -> {
                coinPrice = priceStream.max(Comparator.comparing(CryptoCoin.Price::getTimestamp)).get();
            }
            case MIN -> {
                coinPrice = priceStream.min(Comparator.comparing(CryptoCoin.Price::getValue)).get();
            }
            case MAX -> {
                coinPrice = priceStream.max(Comparator.comparing(CryptoCoin.Price::getValue)).get();
            }
            default -> throw new IllegalStateException("Unexpected filter value: " + filter);
        }
        return new CryptoCoinDto(symbol, coinPrice.getValue(), coinPrice.getTimestamp());
    }
}
