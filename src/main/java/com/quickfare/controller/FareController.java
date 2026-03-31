package com.quickfare.controller;

import com.quickfare.dto.FareEstimateRequest;
import com.quickfare.dto.FareEstimateResponse;
import com.quickfare.service.FareEstimationOrchestrator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for fare estimation.
 * Thin controller — all business logic delegated to services.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class FareController {

    private final FareEstimationOrchestrator fareEstimationOrchestrator;

    public FareController(FareEstimationOrchestrator fareEstimationOrchestrator) {
        this.fareEstimationOrchestrator = fareEstimationOrchestrator;
    }

    /**
     * POST /api/fare-estimate
     * Accepts pickup/drop coordinates and returns fare comparison
     * across multiple ride-hailing services.
     */
    @PostMapping("/fare-estimate")
    public ResponseEntity<FareEstimateResponse> estimateFare(
            @Valid @RequestBody FareEstimateRequest request) {

        log.info("Fare estimate request: ({},{}) → ({},{})",
                request.getStart_latitude(), request.getStart_longitude(),
                request.getEnd_latitude(), request.getEnd_longitude());

        FareEstimateResponse response = fareEstimationOrchestrator.estimateFares(
                request.getStart_latitude(),
                request.getStart_longitude(),
                request.getEnd_latitude(),
                request.getEnd_longitude()
        );

        return ResponseEntity.ok(response);
    }
}
