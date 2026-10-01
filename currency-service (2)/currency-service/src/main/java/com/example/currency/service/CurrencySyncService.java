package com.example.currency.service;

import com.example.currency.dto.ExchangeRateResponse;
import com.example.currency.repository.CurrencyRateCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Service
public class CurrencySyncService {

    private static final Logger log = LoggerFactory.getLogger(CurrencySyncService.class);

    private final RestClient restClient;
    private final CurrencyRateCache rateCache;
    private final String baseCurrency;

    public CurrencySyncService(
            RestClient.Builder restClientBuilder,
            CurrencyRateCache rateCache,
            @Value("${currency.api.base-url}") String baseUrl,
            @Value("${currency.api.base-currency}") String baseCurrency) {
        this.rateCache = rateCache;
        this.baseCurrency = baseCurrency;

        // Resilient connect/read timeouts
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Scheduled(
            fixedRateString = "${currency.schedule.fixed-rate-ms}",
            initialDelayString = "${currency.schedule.initial-delay-ms}"
    )
    public void fetchLatestRates() {
        log.info("Triggering scheduled sync for base currency: {}", baseCurrency);
        try {
            ExchangeRateResponse response = restClient.get()
                    .uri("/{base}", baseCurrency)
                    .retrieve()
                    .body(ExchangeRateResponse.class);

            if (response != null && "success".equalsIgnoreCase(response.result())
                    && response.rates() != null) {
                rateCache.updateRates(response.baseCode(), response.rates());
                log.info("Successfully refreshed {} currency rates. Last upstream update: {}",
                        response.rates().size(), response.lastUpdated());
            } else {
                log.warn("Received empty or unsuccessful payload from external rate provider.");
            }
        } catch (Exception ex) {
            // Guard scheduler thread from dying
            log.error("Failed to sync exchange rates from external API: {}", ex.getMessage(), ex);
        }
    }
}
