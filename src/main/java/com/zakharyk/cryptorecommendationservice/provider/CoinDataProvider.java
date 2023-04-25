package com.zakharyk.cryptorecommendationservice.provider;

import com.zakharyk.cryptorecommendationservice.model.CryptoCoin;

import java.util.List;

public interface CoinDataProvider {
    List<CryptoCoin> getCryptoCoins();
}
