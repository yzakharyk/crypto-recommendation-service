package com.zakharyk.cryptorecommendationservice.provider;

import com.opencsv.CSVReader;
import com.zakharyk.cryptorecommendationservice.model.CryptoCoin;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CsvCryptoCoinDataProvider implements CoinDataProvider {
    @Value("${app.coins.csv.folder}")
    private String coinsCsvFolderLocation;


    @Override
    public List<CryptoCoin> getCryptoCoins() {
        var inputStreamMap = getFileInputStreams();

        return inputStreamMap.entrySet().stream()
                .map(entry -> getCryptoCoinFromInputStream(entry.getKey(), entry.getValue()))
                .toList();
    }

    @SneakyThrows
    private Map<String, InputStream> getFileInputStreams() {
        var csvFolder = new File(coinsCsvFolderLocation);
        Assert.isTrue(csvFolder.exists(),"csv folder not found");

        return Arrays.stream(Objects.requireNonNull(csvFolder.listFiles()))
                .collect(Collectors.toMap(file -> file.getName().substring(0, file.getName().indexOf("_")),
                        file -> {
                            try {
                                return new FileInputStream(file);
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }));
    }

    @SneakyThrows
    private CryptoCoin getCryptoCoinFromInputStream(String symbol, InputStream inputStream) {
        var cryptoCoin = new CryptoCoin(symbol, new ArrayList<>());

        try (var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            var csvReader = new CSVReader(bufferedReader);
            csvReader.skip(1);
            csvReader.readAll().stream()
                    .map(this::mapCsvLineToCryptoCoinPrice)
                    .forEach(cryptoCoinPrice -> cryptoCoin.getPrices().add(cryptoCoinPrice));
        }

        return cryptoCoin;
    }

    private CryptoCoin.Price mapCsvLineToCryptoCoinPrice(String[] csvLine) {
        var timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(csvLine[0])),
                TimeZone.getDefault().toZoneId());
        var price = new BigDecimal(csvLine[2]);
        return new CryptoCoin.Price(timestamp, price);
    }

}
