package com.quickfare.controller;

import com.quickfare.dto.RouteResponse;
import com.quickfare.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for route calculation.
 * Accepts address strings and returns route with fare estimates.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @org.springframework.beans.factory.annotation.Value("${quickfare.api.google-maps.key}")
    private String googleMapsKey;

    @GetMapping(value = "/config.js", produces = "application/javascript")
    public String getConfigJs() {
        return "const GOMAPS_API_KEY = '" + googleMapsKey + "';\n" +
               "document.write('<script src=\"https://maps.googleapis.com/maps/api/js?key=' + GOMAPS_API_KEY + '&libraries=places,directions\"><\\/script>');";
    }

    /**
     * GET /api/route?pickup=...&drop=...&rideType=...
     * Calculate route and fare from pickup to drop address.
     */
    @GetMapping("/route")
    public ResponseEntity<RouteResponse> calculateRoute(
            @RequestParam String pickup,
            @RequestParam String drop,
            @RequestParam String rideType,
            @RequestParam(required = false) Double distanceKm,
            @RequestParam(required = false) Double durationMin) {

        log.info("Route request: pickup='{}', drop='{}', rideType='{}', distance={}, duration={}", 
                pickup, drop, rideType, distanceKm, durationMin);

        RouteResponse response = routeService.calculateRoute(pickup, drop, rideType, distanceKm, durationMin);
        return ResponseEntity.ok(response);
    }
}
