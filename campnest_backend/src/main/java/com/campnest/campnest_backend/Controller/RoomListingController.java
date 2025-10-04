package com.campnest.campnest_backend.Controller;

import com.campnest.campnest_backend.Mapper.RoomListingMapper;
import com.campnest.campnest_backend.dto.RoomListingDTO;
import com.campnest.campnest_backend.model.RoomListing;
import com.campnest.campnest_backend.model.User;
import com.campnest.campnest_backend.repository.RoomListingRepository;
import com.campnest.campnest_backend.repository.UserRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.campnest.campnest_backend.Mapper.RoomListingMapper.toDTO;

@RestController
@RequestMapping("/api/listings")
public class RoomListingController {

    private final RoomListingRepository roomListingRepository;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;

    public RoomListingController(RoomListingRepository roomListingRepository, UserRepository userRepository, Cloudinary cloudinary) {
        this.roomListingRepository = roomListingRepository;
        this.userRepository = userRepository;
        this.cloudinary = cloudinary;
    }

    // 🔒 helper: extract email from JWT
    private String getCurrentUserEmail() {
        return (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // Create new listing (owner is always current logged-in user)
    @PostMapping
    public ResponseEntity<?> createListing(
            @RequestPart("listing") RoomListingDTO dto,
            @RequestPart(value = "files", required = false) MultipartFile[] files
    ) {
        String email = getCurrentUserEmail();
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> mediaUrls = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                try {
                    Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                            ObjectUtils.asMap("resource_type", "auto"));
                    mediaUrls.add((String) uploadResult.get("secure_url"));
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.internalServerError().body("❌ Upload failed");
                }
            }
        }
        RoomListing room = new RoomListing();
        room.setTitle(dto.getTitle());
        room.setDescription(dto.getDescription());
        room.setPrice(dto.getPrice());
        room.setLocation(dto.getLocation());
        room.setImages(dto.getImages());
        room.setAmenities(dto.getAmenities());
        room.setRules(dto.getRules());
        room.setGenderPreference(dto.getGenderPreference());
        room.setAvailableFrom(dto.getAvailableFrom());
        room.setIsActive(dto.getIsActive());
        room.setOwner(owner);

        RoomListing saved = roomListingRepository.save(room);
        return ResponseEntity.ok(toDTO(saved));
    }

    // Get all listings (open to everyone authenticated)
    @GetMapping
    public List<RoomListingDTO> getAllListings() {
        return roomListingRepository.findAll()
                .stream()
                .map(RoomListingMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Get single listing
    @GetMapping("/{id}")
    public RoomListingDTO getListingById(@PathVariable UUID id) {
        RoomListing room = roomListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        return toDTO(room);
    }

    // Update listing (only if current user is the owner)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateListing(@PathVariable UUID id, @RequestBody RoomListingDTO dto) {
        String email = getCurrentUserEmail();

        return roomListingRepository.findById(id)
                .map(existing -> {
                    if (!existing.getOwner().getEmail().equals(email)) {
                        return ResponseEntity.status(403).body("You can only update your own listings");
                    }

                    existing.setTitle(dto.getTitle());
                    existing.setDescription(dto.getDescription());
                    existing.setPrice(dto.getPrice());
                    existing.setLocation(dto.getLocation());
                    existing.setImages(dto.getImages());
                    existing.setAmenities(dto.getAmenities());
                    existing.setRules(dto.getRules());
                    existing.setGenderPreference(dto.getGenderPreference());
                    existing.setAvailableFrom(dto.getAvailableFrom());
                    existing.setIsActive(dto.getIsActive());

                    RoomListing updated = roomListingRepository.save(existing);
                    return ResponseEntity.ok(toDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete listing (only if current user is the owner)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable UUID id) {
        String email = getCurrentUserEmail();

        return roomListingRepository.findById(id)
                .map(existing -> {
                    if (!existing.getOwner().getEmail().equals(email)) {
                        return ResponseEntity.status(403).body("You can only delete your own listings");
                    }
                    roomListingRepository.delete(existing);
                    return ResponseEntity.ok("Listing deleted successfully");
                })
                .orElse(ResponseEntity.status(404).body("Listing not found"));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchListings(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String genderPreference,
            @RequestParam(defaultValue = "0") int page,           // page number starts from 0
            @RequestParam(defaultValue = "10") int size,          // items per page
            @RequestParam(defaultValue = "createdAt") String sort,// sort field
            @RequestParam(defaultValue = "desc") String order     // asc or desc
    ) {
        String loc = (location != null) ? location : "";
        Double min = (minPrice != null) ? minPrice : 0.0;
        Double max = (maxPrice != null) ? maxPrice : Double.MAX_VALUE;
        String gender = (genderPreference != null) ? genderPreference : "";

        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        Page<RoomListing> results = roomListingRepository
                .findByLocationContainingIgnoreCaseAndPriceBetweenAndGenderPreferenceIgnoreCase(
                        loc, min, max, gender, pageable
                );

        List<RoomListingDTO> listings = results
                .stream()
                .map(RoomListingMapper::toDTO)
                .toList();

        return ResponseEntity.ok(listings);
    }
}
