package com.quickfare.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Individual fare price item within the comparison result.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarePriceDto {

    private String service;

    @JsonProperty("vehicle_type")
    private String vehicleType;

    @JsonProperty("display_name")
    private String displayName;

    private Double distance;

    private Double duration;

    private Integer fare;

    @JsonProperty("surge_multiplier")
    private Double surgeMultiplier;

    private String eta;
}
