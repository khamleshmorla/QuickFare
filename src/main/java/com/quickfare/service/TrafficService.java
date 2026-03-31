package com.quickfare.service;

import com.quickfare.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Service for fetching real-time traffic data from Google Maps Distance Matrix API.
 */
@Slf4j
@Service
public class TrafficService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String distanceMatrixUrl;

    public TrafficService(RestTemplate restTemplate,
                          @Value("${quickfare.api.google-maps.key}") String apiKey,
                          @Value("${quickfare.api.google-maps.distance-matrix-url}") String distanceMatrixUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.distanceMatrixUrl = distanceMatrixUrl;
    }

    /**
     * Fetch traffic conditions between two coordinates.
     *
     * @return TrafficData containing distance, duration, and traffic multiplier
     */
    public TrafficData getTrafficConditions(double startLat, double startLng,
                                            double endLat, double endLng) {
        String url = String.format(
                "%s?origins=%s,%s&destinations=%s,%s&departure_time=now&traffic_model=best_guess&key=%s",
                distanceMatrixUrl, startLat, startLng, endLat, endLng, apiKey
        );

        log.debug("Fetching traffic data from Google Maps: origins={},{} destinations={},{}",
                startLat, startLng, endLat, endLng);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                throw new ExternalApiException("Empty response from Google Maps Distance Matrix API");
            }

            return parseTrafficResponse(response);

        } catch (RestClientException ex) {
            log.error("Failed to fetch traffic data from Google Maps", ex);
            // Fallback: calculate approximate values from coordinates
            return calculateFallbackTrafficData(startLat, startLng, endLat, endLng);
        }
    }

    /**
     * Parse the Google Maps Distance Matrix response.
     */
    @SuppressWarnings("unchecked")
    private TrafficData parseTrafficResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> rows = (List<Map<String, Object>>) response.get("rows");
            List<Map<String, Object>> elements = (List<Map<String, Object>>) rows.get(0).get("elements");
            Map<String, Object> result = elements.get(0);

            // Distance in km
            Map<String, Object> distanceMap = (Map<String, Object>) result.get("distance");
            double distanceKm = ((Number) distanceMap.get("value")).doubleValue() / 1000.0;

            // Duration in traffic (minutes)
            double durationMin;
            double trafficMultiplier = 1.0;

            Map<String, Object> durationInTraffic = (Map<String, Object>) result.get("duration_in_traffic");
            Map<String, Object> normalDuration = (Map<String, Object>) result.get("duration");

            if (durationInTraffic != null && normalDuration != null) {
                double trafficSeconds = ((Number) durationInTraffic.get("value")).doubleValue();
                double normalSeconds = ((Number) normalDuration.get("value")).doubleValue();
                durationMin = trafficSeconds / 60.0;

                double trafficRatio = trafficSeconds / normalSeconds;
                if (trafficRatio > 1.5) {
                    trafficMultiplier = 1.3; // Heavy traffic
                } else if (trafficRatio > 1.2) {
                    trafficMultiplier = 1.15; // Moderate traffic
                }
            } else {
                double normalSeconds = ((Number) normalDuration.get("value")).doubleValue();
                durationMin = normalSeconds / 60.0;
            }

            log.debug("Traffic data parsed: distance={}km, duration={}min, multiplier={}",
                    distanceKm, durationMin, trafficMultiplier);

            return new TrafficData(distanceKm, durationMin, trafficMultiplier);

        } catch (Exception ex) {
            log.error("Failed to parse Google Maps response", ex);
            throw new ExternalApiException("Failed to parse traffic data", ex);
        }
    }

    /**
     * Fallback: Calculate approximate distance using Haversine formula.
     */
    private TrafficData calculateFallbackTrafficData(double startLat, double startLng,
                                                     double endLat, double endLng) {
        log.warn("Using fallback distance calculation (Haversine formula)");

        double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(endLat - startLat);
        double dLng = Math.toRadians(endLng - startLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceKm = earthRadiusKm * c;

        // Estimate duration: ~30 km/h average in city
        double durationMin = (distanceKm / 30.0) * 60.0;

        return new TrafficData(distanceKm, durationMin, 1.0);
    }

    /**
     * Determine traffic condition label from multiplier.
     */
    public String getTrafficConditionLabel(double trafficMultiplier) {
        if (trafficMultiplier > 1.2) return "Heavy";
        if (trafficMultiplier > 1.1) return "Moderate";
        return "Light";
    }

    // ---- Data Transfer Object ----

    public record TrafficData(double distanceKm, double durationMin, double trafficMultiplier) {
    }
}
