package com.example.currency.repository;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CurrencyRateCache {

    private final Map<String, BigDecimal> ratesCache = new ConcurrentHashMap<>();
    private volatile Instant lastRefreshed;
    private volatile String baseCurrency = "USD";

    public void updateRates(String baseCurrency, Map<String, BigDecimal> newRates) {
        this.baseCurrency = baseCurrency;
        this.ratesCache.clear();
        this.ratesCache.putAll(newRates);
        this.lastRefreshed = Instant.now();
    }

    public Optional<BigDecimal> getRate(String targetCurrency) {
        return Optional.ofNullable(ratesCache.get(targetCurrency.toUpperCase()));
    }

    public Map<String, BigDecimal> getAllRates() {
        return Collections.unmodifiableMap(ratesCache);
    }

    public Instant getLastRefreshed() {
        return lastRefreshed;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }
}
