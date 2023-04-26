package com.zakharyk.cryptorecommendationservice.service;

import com.zakharyk.cryptorecommendationservice.model.CoinValueDto;
import com.zakharyk.cryptorecommendationservice.model.CryptoCoin;
import com.zakharyk.cryptorecommendationservice.model.CryptoCoinDto;
import com.zakharyk.cryptorecommendationservice.model.Filter;
import com.zakharyk.cryptorecommendationservice.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoinService {
    private static final String NO_DATA_FOUND_MESSAGE = "No data at specified date found";
    private final CoinRepository coinRepository;


    public List<CoinValueDto> calculateNormalizedRange() {
        var result = new ArrayList<CoinValueDto>();
        var allAvailableCoins = coinRepository.getAllAvailableCoins();

        allAvailableCoins.forEach(coin -> {
            var maxPrice = coin.getPrices().stream().max(Comparator.comparing(CryptoCoin.Price::getValue)).get();
            var minPrice = coin.getPrices().stream().min(Comparator.comparing(CryptoCoin.Price::getValue)).get();
            var normalizedRange = maxPrice.getValue().subtract(minPrice.getValue()).divide(minPrice.getValue(), 2, RoundingMode.HALF_UP);
            result.add(new CoinValueDto(coin.getSymbol(), normalizedRange));
        });

        result.sort((e1, e2) -> e2.value().compareTo(e1.value()));
        return result;
    }

    public CryptoCoinDto getCoin(String symbol, Filter filter) {
        var cryptoCoin = coinRepository.getCryptoCoin(symbol)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No coin found"));

        if (CollectionUtils.isEmpty(cryptoCoin.getPrices())) {
            return new CryptoCoinDto(symbol, null, null);
        }

        var priceStream = cryptoCoin.getPrices().stream();

        CryptoCoin.Price coinPrice;
        switch (filter) {
            case OLDEST -> coinPrice = priceStream.min(Comparator.comparing(CryptoCoin.Price::getTimestamp)).get();
            case NEWEST -> coinPrice = priceStream.max(Comparator.comparing(CryptoCoin.Price::getTimestamp)).get();
            case MIN -> coinPrice = priceStream.min(Comparator.comparing(CryptoCoin.Price::getValue)).get();
            case MAX -> coinPrice = priceStream.max(Comparator.comparing(CryptoCoin.Price::getValue)).get();
            default -> throw new IllegalStateException("Unexpected filter value: " + filter);
        }
        return new CryptoCoinDto(symbol, coinPrice.getValue(), coinPrice.getTimestamp());
    }

    public CoinValueDto getHighestNormalizedForDate(LocalDate specifiedDate) {
        var allAvailableCoins = coinRepository.getAllAvailableCoins();

        var coinValues = new ArrayList<CoinValueDto>();

        for (CryptoCoin coin : allAvailableCoins) {
            var specifiedDatePrice = coin.getPrices().stream()
                    .filter(price -> price.getTimestamp().toLocalDate().isEqual(specifiedDate))
                    .findFirst();

            if (specifiedDatePrice.isEmpty()) {
                continue;
            }

            var maxPrice = coin.getPrices().stream()
                    .filter(price -> price.getTimestamp().toLocalDate().isEqual(specifiedDate) || price.getTimestamp()
                            .toLocalDate().isBefore(specifiedDate))
                    .max(Comparator.comparing(CryptoCoin.Price::getValue))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NO_DATA_FOUND_MESSAGE));

            var minPrice = coin.getPrices().stream()
                    .filter(price -> price.getTimestamp().toLocalDate().isEqual(specifiedDate) || price.getTimestamp()
                            .toLocalDate().isBefore(specifiedDate))
                    .min(Comparator.comparing(CryptoCoin.Price::getValue))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NO_DATA_FOUND_MESSAGE));

            var normalizedRange = specifiedDatePrice.get().getValue().subtract(minPrice.getValue())
                    .divide(maxPrice.getValue().subtract(minPrice.getValue()), 2, RoundingMode.HALF_UP);

            coinValues.add(new CoinValueDto(coin.getSymbol(), normalizedRange));
        }

        return coinValues.stream()
                .max(Comparator.comparing(CoinValueDto::value))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NO_DATA_FOUND_MESSAGE));
    }
}
