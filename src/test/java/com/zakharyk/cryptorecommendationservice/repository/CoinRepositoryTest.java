package com.zakharyk.cryptorecommendationservice.repository;

import com.zakharyk.cryptorecommendationservice.model.CryptoCoin;
import com.zakharyk.cryptorecommendationservice.provider.CoinDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoinRepositoryTest {
    @Mock
    private CoinDataProvider mockCoinDataProvider1;

    @Mock
    private CoinDataProvider mockCoinDataProvider2;

    private CoinRepository coinRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        var dataProviders = List.of(mockCoinDataProvider1, mockCoinDataProvider2);
        coinRepository = new CoinRepository(dataProviders);
    }

    @Test
    void getCryptoCoin() {
        var price1 = new CryptoCoin.Price(LocalDateTime.now().minusHours(1), BigDecimal.valueOf(5000.0));
        var price2 = new CryptoCoin.Price(LocalDateTime.now(), BigDecimal.valueOf(5500.0));
        var bitcoin = new CryptoCoin("BTC", List.of(price1, price2));
        when(mockCoinDataProvider1.getCryptoCoins()).thenReturn(List.of(bitcoin));
        coinRepository.initialize();

        var optionalCryptoCoin = coinRepository.getCryptoCoin("BTC");

        assertTrue(optionalCryptoCoin.isPresent());
        assertEquals(bitcoin, optionalCryptoCoin.get());
    }

    @Test
    void getCryptoCoin_empty() {
        var optionalCryptoCoin = coinRepository.getCryptoCoin("XYZ");
        assertFalse(optionalCryptoCoin.isPresent());
    }

    @Test
    void getAllAvailableCoins() {
        var price1 = new CryptoCoin.Price(LocalDateTime.now().minusHours(1), BigDecimal.valueOf(5000.0));
        var price2 = new CryptoCoin.Price(LocalDateTime.now(), BigDecimal.valueOf(5500.0));
        var bitcoin = new CryptoCoin("BTC", Arrays.asList(price1, price2));

        var price3 = new CryptoCoin.Price(LocalDateTime.now().minusHours(1), BigDecimal.valueOf(200.0));
        var price4 = new CryptoCoin.Price(LocalDateTime.now(), BigDecimal.valueOf(300.0));
        var ethereum = new CryptoCoin("ETH", Arrays.asList(price3, price4));

        when(mockCoinDataProvider1.getCryptoCoins()).thenReturn(List.of(bitcoin));
        when(mockCoinDataProvider2.getCryptoCoins()).thenReturn(List.of(ethereum));

        coinRepository.initialize();

        var allAvailableCoins = coinRepository.getAllAvailableCoins();
        assertEquals(2, allAvailableCoins.size());
        assertTrue(allAvailableCoins.contains(bitcoin));
        assertTrue(allAvailableCoins.contains(ethereum));
    }

    @Test
    void testGetAllAvailableCoinsReturnsEmptyCollectionIfNoCoinsAvailable() {
        when(mockCoinDataProvider1.getCryptoCoins()).thenReturn(List.of());
        when(mockCoinDataProvider2.getCryptoCoins()).thenReturn(List.of());

        coinRepository.initialize();

        var allAvailableCoins = coinRepository.getAllAvailableCoins();
        assertEquals(0, allAvailableCoins.size());
    }

}