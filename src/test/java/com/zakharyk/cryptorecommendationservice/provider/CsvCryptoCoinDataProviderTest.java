package com.zakharyk.cryptorecommendationservice.provider;

import com.zakharyk.cryptorecommendationservice.model.CryptoCoin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


class CsvCryptoCoinDataProviderTest {
    public static final String CSV_PRICES_FOLDER = "src/test/resources/csv/prices";
    public static final String CSV_PRICES_FOLDER_BTC_VALUES = "src/test/resources/csv/prices/BTC_values.csv";
    private CsvCryptoCoinDataProvider csvCryptoCoinDataProvider;

    @BeforeEach
    void setUp() {
        csvCryptoCoinDataProvider = new CsvCryptoCoinDataProvider();
        ReflectionTestUtils.setField(csvCryptoCoinDataProvider,"coinsCsvFolderLocation", CSV_PRICES_FOLDER);
    }

    @Test
    void testGetCryptoCoins() {
        var cryptoCoins = csvCryptoCoinDataProvider.getCryptoCoins();
        assertEquals(1, cryptoCoins.size());

        var bitcoin = cryptoCoins.get(0);
        assertEquals("BTC", bitcoin.getSymbol());
        assertEquals(100, bitcoin.getPrices().size());
        assertEquals(new BigDecimal("46813.21"), bitcoin.getPrices().get(0).getValue());
    }

    @Test
    void testGetFileInputStreams() {
        var inputStreams = csvCryptoCoinDataProvider.getFileInputStreams();
        assertEquals(1, inputStreams.size());
        var btcInputStream = inputStreams.get("BTC");
        assertNotNull(btcInputStream);
    }

    @Test
    void testGetFileInputStreamsInvalidFolder() {
        ReflectionTestUtils.setField(csvCryptoCoinDataProvider,"coinsCsvFolderLocation","invalid/folder");
        assertThrows(IllegalArgumentException.class, () -> csvCryptoCoinDataProvider.getFileInputStreams());
    }

    @Test
    void testGetCryptoCoinFromInputStream() throws FileNotFoundException {
        var inputStream = new FileInputStream(CSV_PRICES_FOLDER_BTC_VALUES);
        var bitcoin = csvCryptoCoinDataProvider.getCryptoCoinFromInputStream("BTC", inputStream);
        assertEquals("BTC", bitcoin.getSymbol());
        assertEquals(100, bitcoin.getPrices().size());
        assertEquals(new BigDecimal("46813.21"), bitcoin.getPrices().get(0).getValue());
    }

}