package com.example.currency.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExchangeRateResponse(
        String result,
        @JsonProperty("base_code") String baseCode,
        @JsonProperty("time_last_update_utc") String lastUpdated,
        @JsonProperty("rates") Map<String, BigDecimal> rates
) {}
