package com.healthcare.platform.service;

import com.healthcare.platform.model.Doctor;
import com.healthcare.platform.model.Hospital;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoServiceTest {

    private final GeoService geoService = new GeoService();

    @Test
    void resolvesStateFromKnownAndUnknownCities() {
        assertEquals("West Bengal", GeoService.resolveStateFromCity(null));
        assertEquals("Maharashtra", GeoService.resolveStateFromCity(" Navi Mumbai "));
        assertEquals("ALL", GeoService.resolveStateFromCity("Jaipur"));
    }

    @Test
    void resolvesExactPartialAndFallbackCoordinates() {
        double[] kolkata = GeoService.resolveCityCoordinates("Kolkata");
        double[] partial = GeoService.resolveCityCoordinates("Kolkata, West Bengal");
        double[] fallback = GeoService.resolveCityCoordinates("Unknown city");

        assertEquals(22.5726, kolkata[0]);
        assertEquals(22.5726, partial[0]);
        assertSame(GeoService.CITY_COORDINATES.get("Budge Budge"), fallback);
    }

    @Test
    void calculatesDistanceAndEnforcesMinimumTransitTime() {
        assertEquals(0.0, geoService.calculateDistance(22.5726, 88.3639, 22.5726, 88.3639));
        assertTrue(geoService.calculateDistance(20.2961, 85.8245, 20.2444, 85.7766) > 0);
        assertEquals(3, geoService.estimateTransitMinutes(0.1, false));
        assertTrue(geoService.estimateTransitMinutes(45, true) < geoService.estimateTransitMinutes(45, false));
    }

    @Test
    void sortsHospitalsByDistanceAndHandlesMissingCoordinates() {
        Hospital nearby = new Hospital();
        nearby.setName("Nearby");
        nearby.setLatitude(22.5726);
        nearby.setLongitude(88.3639);

        Hospital unknown = new Hospital();
        unknown.setName("Unknown");

        List<Hospital> hospitals = geoService.sortByProximityHospitals(
                Arrays.asList(unknown, nearby), 22.5726, 88.3639);

        assertEquals("Nearby", hospitals.get(0).getName());
        assertEquals(999.0, hospitals.get(1).getDistanceKm());
        assertEquals(0.0, hospitals.get(0).getDistanceKm());
    }

    @Test
    void returnsEmptyListForNullInputsAndCopiesUnmodifiableLists() {
        assertNotNull(geoService.sortByProximityHospitals(null, 0, 0));
        assertTrue(geoService.sortByProximityDoctors(Collections.emptyList(), 0, 0).isEmpty());

        Hospital hospital = new Hospital();
        hospital.setLatitude(22.5726);
        hospital.setLongitude(88.3639);
        List<Hospital> sorted = geoService.sortByProximityHospitals(
                Collections.unmodifiableList(List.of(hospital)), 22.5726, 88.3639);

        assertEquals(1, sorted.size());
        assertEquals(0.0, sorted.get(0).getDistanceKm());
    }
}
