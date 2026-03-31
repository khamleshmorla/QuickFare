package com.quickfare.controller;

import com.quickfare.domain.entity.Fare;
import com.quickfare.exception.ResourceNotFoundException;
import com.quickfare.repository.FareRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for fare contributions (CRUD).
 * Allows users to submit actual fare data for analytics and community comparison.
 */
@Slf4j
@RestController
@RequestMapping("/api/fares")
public class FareContributionController {

    private final FareRepository fareRepository;

    public FareContributionController(FareRepository fareRepository) {
        this.fareRepository = fareRepository;
    }

    /**
     * POST /api/fares — Submit a fare contribution
     */
    @PostMapping
    public ResponseEntity<Fare> createFare(@Valid @RequestBody Fare fare) {
        log.info("New fare contribution: service={}, rideType={}, fare={}",
                fare.getService(), fare.getRideType(), fare.getActualFare());
        Fare saved = fareRepository.save(fare);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * GET /api/fares — List all fare contributions
     */
    @GetMapping
    public ResponseEntity<List<Fare>> getAllFares() {
        List<Fare> fares = fareRepository.findAll();
        return ResponseEntity.ok(fares);
    }

    /**
     * GET /api/fares/{id} — Get a specific fare by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Fare> getFareById(@PathVariable Long id) {
        Fare fare = fareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fare not found with id: " + id));
        return ResponseEntity.ok(fare);
    }

    /**
     * DELETE /api/fares/{id} — Delete a fare contribution
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFare(@PathVariable Long id) {
        if (!fareRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fare not found with id: " + id);
        }
        fareRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
