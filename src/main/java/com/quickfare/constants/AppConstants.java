package com.quickfare.constants;

/**
 * Application-wide constants.
 * Centralizes magic strings and values to avoid hardcoding throughout the codebase.
 */
public final class AppConstants {

    private AppConstants() {
        // Utility class — prevent instantiation
    }

    // ---- Default City ----
    public static final String DEFAULT_CITY = "mumbai";

    // ---- Route Adjustment ----
    public static final double ROUTE_ADJUSTMENT_FACTOR = 1.3;
    public static final double FARE_MULTIPLIER = 1.05;

    // ---- Traffic Thresholds ----
    public static final double HEAVY_TRAFFIC_THRESHOLD = 1.5;
    public static final double MODERATE_TRAFFIC_THRESHOLD = 1.2;
    public static final double HEAVY_TRAFFIC_MULTIPLIER = 1.3;
    public static final double MODERATE_TRAFFIC_MULTIPLIER = 1.15;

    // ---- Surge Pricing ----
    public static final double SEDAN_PEAK_SURGE = 1.05;
    public static final double DEFAULT_PEAK_SURGE = 1.03;
    public static final double WEEKEND_SURGE_MULTIPLIER = 1.03;

    // ---- Minimum Fare Multipliers ----
    public static final double MIN_FARE_SHORT = 1.1;   // <= 2 km
    public static final double MIN_FARE_MEDIUM = 1.3;  // <= 5 km
    public static final double MIN_FARE_LONG = 1.5;    // > 5 km

    // ---- Average City Speed (km/h) for fallback ----
    public static final double AVERAGE_CITY_SPEED_KMH = 30.0;

    // ---- API Response Messages ----
    public static final String MSG_UNSUPPORTED_CITY = "Unsupported city or vehicle type";
    public static final String MSG_PICKUP_REQUIRED = "Pickup location is required";
    public static final String MSG_DROP_REQUIRED = "Drop location is required";
    public static final String MSG_RIDE_TYPE_REQUIRED = "Ride type is required";
    public static final String MSG_EXTERNAL_API_UNAVAILABLE = "External service unavailable";
    public static final String MSG_EXTERNAL_API_TIMEOUT = "External service timed out";
    public static final String MSG_UNEXPECTED_ERROR = "An unexpected error occurred";
}
