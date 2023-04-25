package com.zakharyk.cryptorecommendationservice.repository;

import com.zakharyk.cryptorecommendationservice.model.CryptoCoin;
import com.zakharyk.cryptorecommendationservice.provider.CoinDataProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CoinRepository {
    private final List<CoinDataProvider> dataProviders;
    private final Map<String, CryptoCoin> cryptoCoinMap = new HashMap<>();

    @PostConstruct
    public void initialize() {
        var cryptoCoins = dataProviders
                .stream()
                .map(CoinDataProvider::getCryptoCoins)
                .flatMap(Collection::stream)
                .toList();

        cryptoCoins
                .forEach(cryptoCoin -> cryptoCoinMap.put(cryptoCoin.getSymbol(), cryptoCoin));

    }

   public Optional<CryptoCoin> getCryptoCoin(String symbol){
        return Optional.ofNullable(cryptoCoinMap.get(symbol));
   }

   public Collection<CryptoCoin> getAllAvailableCoins(){
        return cryptoCoinMap.values();
   }
}
