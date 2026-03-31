package com.quickfare.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for fare estimation.
 * Matches the exact JSON contract expected by the frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FareEstimateResponse {

    private boolean success;
    private FareData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FareData {

        private List<FarePriceDto> prices;

        @JsonProperty("traffic_condition")
        private String trafficCondition;

        @JsonProperty("time_of_day")
        private String timeOfDay;
    }
}
