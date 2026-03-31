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

    /**
     * GET /api/route?pickup=...&drop=...&rideType=...
     * Calculate route and fare from pickup to drop address.
     */
    @GetMapping("/route")
    public ResponseEntity<RouteResponse> calculateRoute(
            @RequestParam String pickup,
            @RequestParam String drop,
            @RequestParam String rideType) {

        log.info("Route request: pickup='{}', drop='{}', rideType='{}'", pickup, drop, rideType);

        RouteResponse response = routeService.calculateRoute(pickup, drop, rideType);
        return ResponseEntity.ok(response);
    }
}
