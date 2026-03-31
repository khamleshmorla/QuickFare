package com.quickfare.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for fare estimation.
 * Accepts pickup and drop coordinates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FareEstimateRequest {

    @NotNull(message = "Start latitude is required")
    private Double start_latitude;

    @NotNull(message = "Start longitude is required")
    private Double start_longitude;

    @NotNull(message = "End latitude is required")
    private Double end_latitude;

    @NotNull(message = "End longitude is required")
    private Double end_longitude;
}
