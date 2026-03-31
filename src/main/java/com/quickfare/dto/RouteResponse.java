package com.quickfare.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for route calculation.
 * Contains route info + fare breakdown for all services.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponse {

    private boolean success;
    private String pickup;
    private String drop;
    private String rideType;
    private String distance;
    private String time;

    @JsonProperty("distance_value")
    private double distanceValue; // distance in km (numeric)

    private List<FarePriceDto> prices;

    @JsonProperty("traffic_condition")
    private String trafficCondition;

    @JsonProperty("time_of_day")
    private String timeOfDay;
}
