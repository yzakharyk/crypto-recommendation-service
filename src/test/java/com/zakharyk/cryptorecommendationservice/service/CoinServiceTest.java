package com.zakharyk.cryptorecommendationservice.service;

import com.zakharyk.cryptorecommendationservice.model.CryptoCoin;
import com.zakharyk.cryptorecommendationservice.model.CryptoCoinDto;
import com.zakharyk.cryptorecommendationservice.model.Filter;
import com.zakharyk.cryptorecommendationservice.repository.CoinRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoinServiceTest {
    @Mock
    private CoinRepository coinRepository;
    @InjectMocks
    private CoinService coinService;

    @Test
    void testCalculateNormalizedRange() {
        var coin1 = new CryptoCoin("BTC", Arrays.asList(
                new CryptoCoin.Price(LocalDateTime.of(2022, 4, 1, 0, 0), BigDecimal.valueOf(50000)),
                new CryptoCoin.Price(LocalDateTime.of(2022, 4, 2, 0, 0), BigDecimal.valueOf(55000)),
                new CryptoCoin.Price(LocalDateTime.of(2022, 4, 3, 0, 0), BigDecimal.valueOf(60000))));
        var coin2 = new CryptoCoin("ETH", Arrays.asList(
                new CryptoCoin.Price(LocalDateTime.of(2022, 4, 1, 0, 0), BigDecimal.valueOf(2100)),
                new CryptoCoin.Price(LocalDateTime.of(2022, 4, 2, 0, 0), BigDecimal.valueOf(2200)),
                new CryptoCoin.Price(LocalDateTime.of(2022, 4, 3, 0, 0), BigDecimal.valueOf(2400))));

        when(coinRepository.getAllAvailableCoins()).thenReturn(Arrays.asList(coin1, coin2));

        var result = coinService.calculateNormalizedRange();

        assertEquals(2,result.size());
        assertEquals("BTC",result.get(0).symbol());
        assertEquals(new BigDecimal("0.20"), result.get(0).value());
        assertEquals("ETH", result.get(1).symbol());
        assertEquals(new BigDecimal("0.14"), result.get(1).value());
    }

    @Test
    void getCoin_maxFilter() {
        var price1 = new CryptoCoin.Price(LocalDateTime.of(2022, 4, 1, 0, 0), BigDecimal.valueOf(50000));
        var price2 = new CryptoCoin.Price(LocalDateTime.of(2022, 4, 2, 0, 0), BigDecimal.valueOf(51000));
        when(coinRepository.getCryptoCoin("BTC")).thenReturn(Optional.of(new CryptoCoin("BTC", List.of(price1, price2))));

        var result = coinService.getCoin("BTC", Filter.MAX);

        assertEquals("BTC", result.symbol());
        assertEquals(price2.getValue(), result.price());
        assertEquals(price2.getTimestamp(), result.timestamp());
    }

    @Test
    void getCoin_minFilter() {
        var price1 = new CryptoCoin.Price(LocalDateTime.of(2022, 4, 1, 0, 0), BigDecimal.valueOf(50000));
        var price2 = new CryptoCoin.Price(LocalDateTime.of(2022, 4, 2, 0, 0), BigDecimal.valueOf(51000));
        when(coinRepository.getCryptoCoin("BTC")).thenReturn(Optional.of(new CryptoCoin("BTC", List.of(price1, price2))));

        var result = coinService.getCoin("BTC", Filter.MIN);

        assertEquals("BTC", result.symbol());
        assertEquals(price1.getValue(), result.price());
        assertEquals(price1.getTimestamp(), result.timestamp());
    }
    @Test
    void getCoin_newestFilter() {
        var price1 = new CryptoCoin.Price(LocalDateTime.of(2022, 4, 1, 0, 0), BigDecimal.valueOf(50000));
        var price2 = new CryptoCoin.Price(LocalDateTime.of(2022, 4, 2, 0, 0), BigDecimal.valueOf(51000));
        when(coinRepository.getCryptoCoin("BTC")).thenReturn(Optional.of(new CryptoCoin("BTC", List.of(price1, price2))));

        var result = coinService.getCoin("BTC", Filter.NEWEST);

        assertEquals("BTC", result.symbol());
        assertEquals(price2.getValue(), result.price());
        assertEquals(price2.getTimestamp(), result.timestamp());
    }
    @Test
    void getCoin_oldestFilter() {
        var price1 = new CryptoCoin.Price(LocalDateTime.of(2022, 4, 1, 0, 0), BigDecimal.valueOf(50000));
        var price2 = new CryptoCoin.Price(LocalDateTime.of(2022, 4, 2, 0, 0), BigDecimal.valueOf(51000));
        when(coinRepository.getCryptoCoin("BTC")).thenReturn(Optional.of(new CryptoCoin("BTC", List.of(price1, price2))));

        var result = coinService.getCoin("BTC", Filter.OLDEST);

        assertEquals("BTC", result.symbol());
        assertEquals(price1.getValue(), result.price());
        assertEquals(price1.getTimestamp(), result.timestamp());
    }


    @Test
     void getCoin_notFound() {
        when(coinRepository.getCryptoCoin("COIN1")).thenReturn(Optional.empty());

        var exception = assertThrows(ResponseStatusException.class, () -> coinService.getCoin("COIN1", Filter.MAX));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No coin found", exception.getReason());
    }

    @Test
    void getCoin_withEmptyPriceList() {
        var symbol = "BTC";
        var filter = Filter.NEWEST;
        var expected = new CryptoCoinDto(symbol, null, null);
        var coin = new CryptoCoin(symbol, new ArrayList<>());
        when(coinRepository.getCryptoCoin(symbol)).thenReturn(Optional.of(coin));

        var actual = coinService.getCoin(symbol, filter);

        assertEquals(expected, actual);
    }

    @Test
     void getHighestNormalizedForDate() {
        var testDate = LocalDate.of(2022, 1, 1);
        var allAvailableCoins = new ArrayList<CryptoCoin>();
        var coin1 = new CryptoCoin("BTC", Arrays.asList(
                new CryptoCoin.Price(LocalDateTime.of(2021, 12, 31, 0, 0), BigDecimal.valueOf(50000)),
                new CryptoCoin.Price(LocalDateTime.of(2022, 1, 1, 0, 0), BigDecimal.valueOf(60000)),
                new CryptoCoin.Price(LocalDateTime.of(2022, 1, 2, 0, 0), BigDecimal.valueOf(55000))
        ));
        var coin2 = new CryptoCoin("ETH", Arrays.asList(
                new CryptoCoin.Price(LocalDateTime.of(2021, 12, 31, 0, 0), BigDecimal.valueOf(2000)),
                new CryptoCoin.Price(LocalDateTime.of(2022, 1, 1, 0, 0), BigDecimal.valueOf(2200)),
                new CryptoCoin.Price(LocalDateTime.of(2022, 1, 2, 0, 0), BigDecimal.valueOf(2100))
        ));
        allAvailableCoins.add(coin1);
        allAvailableCoins.add(coin2);

        when(coinRepository.getAllAvailableCoins()).thenReturn(allAvailableCoins);

        var result = coinService.getHighestNormalizedForDate(testDate);

        assertEquals("BTC", result.symbol());
        assertEquals(new BigDecimal("1.00"), result.value());
    }

    @Test
    void getHighestNormalizedForDate_noDataFound() {
        var specifiedDate = LocalDate.of(2023, 4, 30);
        when(coinRepository.getAllAvailableCoins()).thenReturn(Collections.emptyList());

        var exception = assertThrows(ResponseStatusException.class,
                () -> coinService.getHighestNormalizedForDate(specifiedDate));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

}
