package com.quickfare.repository;

import com.quickfare.domain.entity.Fare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Fare entity — provides CRUD + custom query methods.
 */
@Repository
public interface FareRepository extends JpaRepository<Fare, Long> {

    List<Fare> findByUserId(String userId);

    List<Fare> findByService(Fare.ServiceProvider service);

    List<Fare> findByRideType(Fare.RideType rideType);
}
