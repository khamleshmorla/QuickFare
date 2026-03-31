package com.quickfare.service;

import com.quickfare.dto.FarePriceDto;
import com.quickfare.dto.RouteResponse;
import com.quickfare.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for route calculation using geocoding + fare estimation.
 * Returns complete route info with fare comparison across all services.
 */
@Slf4j
@Service
public class RouteService {

    private final GeocodingService geocodingService;
    private final TrafficService trafficService;
    private final FareCalculationService fareCalculationService;

    public RouteService(GeocodingService geocodingService,
                        TrafficService trafficService,
                        FareCalculationService fareCalculationService) {
        this.geocodingService = geocodingService;
        this.trafficService = trafficService;
        this.fareCalculationService = fareCalculationService;
    }

    /**
     * Calculate route information and fare breakdown for all services.
     *
     * @param pickup   Pickup address string
     * @param drop     Drop address string
     * @param rideType Vehicle category (bike, auto, car)
     * @return Route response with distance, time, and all service fares
     */
    public RouteResponse calculateRoute(String pickup, String drop, String rideType, Double providedDistanceKm, Double providedDurationMin) {
        if (pickup == null || pickup.isBlank()) {
            throw new InvalidInputException("Pickup location is required");
        }
        if (drop == null || drop.isBlank()) {
            throw new InvalidInputException("Drop location is required");
        }
        if (rideType == null || rideType.isBlank()) {
            throw new InvalidInputException("Ride type is required");
        }

        log.info("Calculating route: pickup='{}', drop='{}', rideType='{}'", pickup, drop, rideType);

        double distanceKm;
        double durationMin;
        double trafficMultiplier = 1.0;

        if (providedDistanceKm != null && providedDurationMin != null && providedDistanceKm > 0) {
            distanceKm = providedDistanceKm;
            durationMin = providedDurationMin;
            log.info("Using client-provided measurements: {} km, {} min", distanceKm, durationMin);
        } else {
            // Geocode addresses to coordinates
            GeocodingService.Coordinates pickupCoords = geocodingService.geocodeAddress(pickup);
            GeocodingService.Coordinates dropCoords = geocodingService.geocodeAddress(drop);

            // Get traffic-aware distance and time
            TrafficService.TrafficData trafficData = trafficService.getTrafficConditions(
                    pickupCoords.lat(), pickupCoords.lng(),
                    dropCoords.lat(), dropCoords.lng()
            );

            distanceKm = trafficData.distanceKm();
            durationMin = trafficData.durationMin();
            trafficMultiplier = trafficData.trafficMultiplier();
        }

        // Build fare comparison list based on ride type
        List<FarePriceDto> prices = buildFareComparison(rideType, distanceKm, durationMin);

        // Get traffic condition and time of day
        String trafficCondition = trafficService.getTrafficConditionLabel(trafficMultiplier);
        String timeOfDay = fareCalculationService.getTimeOfDay();

        return RouteResponse.builder()
                .success(true)
                .pickup(pickup)
                .drop(drop)
                .rideType(rideType)
                .distance(String.format("%.1f km", distanceKm))
                .time(String.format("%d min", Math.round(durationMin)))
                .distanceValue(distanceKm)
                .prices(prices)
                .trafficCondition(trafficCondition)
                .timeOfDay(timeOfDay)
                .build();
    }

    /**
     * Build fare comparison across Ola, Uber, Rapido for the given ride type.
     */
    private List<FarePriceDto> buildFareComparison(String rideType, double distanceKm, double durationMin) {
        List<FarePriceDto> prices = new ArrayList<>();

        switch (rideType.toLowerCase()) {
            case "bike" -> {
                prices.add(buildFare("Rapido", "bike", "🏍️ Rapido Bike", distanceKm, durationMin, "10 min"));
                prices.add(buildFare("Ola", "bike", "🚴 Ola Bike", distanceKm, durationMin, "12 min"));
                prices.add(buildFare("Uber", "bike", "🏍️ Uber Moto", distanceKm, durationMin, "11 min"));
            }
            case "auto" -> {
                prices.add(buildFare("Rapido", "auto", "🛺 Rapido Auto", distanceKm, durationMin, "15 min"));
                prices.add(buildFare("Ola", "auto", "🛺 Ola Auto", distanceKm, durationMin, "15 min"));
                prices.add(buildFare("Uber", "auto", "🚜 Uber Auto", distanceKm, durationMin, "14 min"));
            }
            case "car" -> {
                prices.add(buildFare("Ola", "mini", "🚗 Ola Mini", distanceKm, durationMin, "18 min"));
                prices.add(buildFare("Ola", "sedan", "🚙 Ola Sedan", distanceKm, durationMin, "20 min"));
                prices.add(buildFare("Uber", "go", "🚖 UberGo", distanceKm, durationMin, "17 min"));
                prices.add(buildFare("Uber", "premier", "🚖 Uber Premier", distanceKm, durationMin, "19 min"));
                prices.add(buildFare("Rapido", "car", "🚗 Rapido Car", distanceKm, durationMin, "20 min"));
            }
            default -> {
                // Default to bike
                prices.add(buildFare("Rapido", "bike", "🏍️ Rapido Bike", distanceKm, durationMin, "10 min"));
                prices.add(buildFare("Ola", "bike", "🚴 Ola Bike", distanceKm, durationMin, "12 min"));
                prices.add(buildFare("Uber", "bike", "🏍️ Uber Moto", distanceKm, durationMin, "11 min"));
            }
        }

        return prices;
    }

    /**
     * Build a single fare price DTO.
     */
    private FarePriceDto buildFare(String service, String vehicleType, String displayName,
                                    double distanceKm, double durationMin, String eta) {
        int fare = fareCalculationService.calculateRideFare(
                service, vehicleType, distanceKm, durationMin, true);

        double surge = fareCalculationService.estimateSurgeMultiplier(service, vehicleType);

        return FarePriceDto.builder()
                .service(service)
                .vehicleType(vehicleType)
                .displayName(displayName)
                .distance(distanceKm)
                .duration(durationMin)
                .fare(fare)
                .surgeMultiplier(surge)
                .eta(eta)
                .build();
    }
}
