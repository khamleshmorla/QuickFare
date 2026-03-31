package com.quickfare.service;

import com.quickfare.dto.FareEstimateResponse;
import com.quickfare.dto.FarePriceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Orchestration service for fare estimation.
 * Coordinates traffic data fetching, fare calculation across multiple
 * services and vehicle types, and builds the response.
 */
@Slf4j
@Service
public class FareEstimationOrchestrator {

    private final TrafficService trafficService;
    private final FareCalculationService fareCalculationService;

    // ---- Service → Vehicle Type Mapping ----
    private static final Map<String, List<String>> SERVICE_VEHICLE_TYPES = new LinkedHashMap<>();

    static {
        SERVICE_VEHICLE_TYPES.put("Ola", List.of("bike", "auto", "mini", "sedan"));
        SERVICE_VEHICLE_TYPES.put("Uber", List.of("bike", "auto", "go", "premier"));
        SERVICE_VEHICLE_TYPES.put("Rapido", List.of("bike", "auto", "car"));
    }

    public FareEstimationOrchestrator(TrafficService trafficService,
                                      FareCalculationService fareCalculationService) {
        this.trafficService = trafficService;
        this.fareCalculationService = fareCalculationService;
    }

    /**
     * Estimate fares across all services and vehicle types.
     *
     * @param startLat Start latitude
     * @param startLng Start longitude
     * @param endLat   End latitude
     * @param endLng   End longitude
     * @return Complete fare estimate response
     */
    public FareEstimateResponse estimateFares(double startLat, double startLng,
                                              double endLat, double endLng) {
        log.info("Estimating fares from ({},{}) to ({},{})", startLat, startLng, endLat, endLng);

        // Step 1: Get traffic conditions
        TrafficService.TrafficData trafficData =
                trafficService.getTrafficConditions(startLat, startLng, endLat, endLng);

        // Step 2: Determine city (default to Mumbai — can be enhanced with reverse geocoding)
        String city = "mumbai";

        // Step 3: Calculate fares for all services and vehicle types
        List<FarePriceDto> prices = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : SERVICE_VEHICLE_TYPES.entrySet()) {
            String serviceName = entry.getKey();
            List<String> vehicleTypes = entry.getValue();

            for (String vehicleType : vehicleTypes) {
                try {
                    int fare = fareCalculationService.calculateRideFare(
                            city, vehicleType,
                            trafficData.distanceKm(),
                            trafficData.durationMin(),
                            true // Include tolls
                    );

                    double surge = fareCalculationService.estimateSurgeMultiplier(city, vehicleType);

                    prices.add(FarePriceDto.builder()
                            .service(serviceName)
                            .vehicleType(vehicleType)
                            .displayName(capitalize(serviceName) + " " + capitalize(vehicleType))
                            .distance(trafficData.distanceKm())
                            .duration(trafficData.durationMin())
                            .fare(fare)
                            .surgeMultiplier(surge)
                            .build());

                } catch (Exception ex) {
                    log.warn("Failed to calculate fare for {} {}: {}",
                            serviceName, vehicleType, ex.getMessage());
                    // Skip this combination but don't fail the entire request
                }
            }
        }

        // Step 4: Build response
        String trafficCondition = trafficService.getTrafficConditionLabel(trafficData.trafficMultiplier());
        String timeOfDay = fareCalculationService.getTimeOfDay();

        return FareEstimateResponse.builder()
                .success(true)
                .data(FareEstimateResponse.FareData.builder()
                        .prices(prices)
                        .trafficCondition(trafficCondition)
                        .timeOfDay(timeOfDay)
                        .build())
                .build();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
