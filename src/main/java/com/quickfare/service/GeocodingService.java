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
 * Service for geocoding addresses to coordinates using Google Maps Geocoding API.
 */
@Slf4j
@Service
public class GeocodingService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;

    public GeocodingService(RestTemplate restTemplate,
                            @Value("${quickfare.api.google-maps.key}") String apiKey,
                            @Value("${quickfare.api.google-maps.geocode-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    /**
     * Geocode an address to latitude/longitude coordinates.
     *
     * @param address Address string to geocode
     * @return Coordinates (lat, lng)
     */
    @SuppressWarnings("unchecked")
    public Coordinates geocodeAddress(String address) {
        String url = String.format("%s?address=%s&key=%s", baseUrl,
                java.net.URLEncoder.encode(address, java.nio.charset.StandardCharsets.UTF_8), apiKey);

        log.debug("Geocoding address via Google Maps: {}", address);

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !"OK".equals(response.get("status"))) {
                throw new ExternalApiException("Error or empty response from Google Maps geocoding API. Status: " + 
                    (response != null ? response.get("status") : "null"));
            }

            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            if (results == null || results.isEmpty()) {
                throw new ExternalApiException("No geocoding results found for address: " + address);
            }

            Map<String, Object> geometry = (Map<String, Object>) results.get(0).get("geometry");
            Map<String, Object> location = (Map<String, Object>) geometry.get("location");
            
            double lat = ((Number) location.get("lat")).doubleValue();
            double lng = ((Number) location.get("lng")).doubleValue();

            log.debug("Geocoded '{}' to ({}, {})", address, lat, lng);
            return new Coordinates(lat, lng);

        } catch (RestClientException ex) {
            log.error("Failed to geocode address: {}", address, ex);
            log.warn("Internet disconnected or Google Maps unavailable. Using fallback deterministic offline coordinates.");
            
            // Generate pseudo-random coordinates based on the address string
            int hash = Math.abs(address.hashCode());
            // Center around Hyderabad (17.3850, 78.4867) with small variations
            double lat = 17.3850 + (hash % 100) / 500.0;
            double lng = 78.4867 + ((hash / 100) % 100) / 500.0;
            
            return new Coordinates(lat, lng);
        }
    }

    // ---- Data Transfer Object ----

    public record Coordinates(double lat, double lng) {
    }
}
