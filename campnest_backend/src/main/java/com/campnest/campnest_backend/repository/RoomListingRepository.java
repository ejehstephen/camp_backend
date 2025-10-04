package com.campnest.campnest_backend.repository;

import com.campnest.campnest_backend.model.RoomListing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface RoomListingRepository extends JpaRepository<RoomListing, UUID> {

    // Dynamic filtering (optional params will be handled in service/controller)
    Page<RoomListing> findByLocationContainingIgnoreCaseAndPriceBetweenAndGenderPreferenceIgnoreCase(
            String location, Double minPrice, Double maxPrice, String genderPreference, Pageable pageable
    );

}
