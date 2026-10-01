package com.example.currency.controller;

import com.example.currency.repository.CurrencyRateCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rates")
public class CurrencyController {

    private final CurrencyRateCache rateCache;

    public CurrencyController(CurrencyRateCache rateCache) {
        this.rateCache = rateCache;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRates() {
        return ResponseEntity.ok(Map.of(
                "base", rateCache.getBaseCurrency(),
                "lastRefreshed", rateCache.getLastRefreshed() != null
                        ? rateCache.getLastRefreshed() : "PENDING",
                "rates", rateCache.getAllRates()
        ));
    }

    @GetMapping("/{currency}")
    public ResponseEntity<?> getSpecificRate(@PathVariable String currency) {
        return rateCache.getRate(currency)
                .map(rate -> ResponseEntity.ok(Map.of(
                        "base", rateCache.getBaseCurrency(),
                        "target", currency.toUpperCase(),
                        "rate", rate,
                        "timestamp", rateCache.getLastRefreshed()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
