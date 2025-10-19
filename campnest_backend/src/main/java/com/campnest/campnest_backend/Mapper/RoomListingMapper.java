package com.campnest.campnest_backend.Mapper;


import com.campnest.campnest_backend.dto.RoomListingDTO;
import com.campnest.campnest_backend.model.RoomListing;
import com.campnest.campnest_backend.model.User;

public class RoomListingMapper {

    public static RoomListingDTO toDTO(RoomListing entity) {
        RoomListingDTO dto = new RoomListingDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setLocation(entity.getLocation());
        dto.setImages(entity.getImages());
        dto.setAmenities(entity.getAmenities());
        dto.setRules(entity.getRules());
        dto.setGenderPreference(entity.getGenderPreference());
        dto.setAvailableFrom(entity.getAvailableFrom());
        dto.setIsActive(entity.getIsActive());
        dto.setOwnerId(entity.getOwner().getId()); // only ID
        dto.setOwnerPhone(entity.getOwnerPhone());
        dto.setWhatsappLink(entity.getWhatsappLink());

// ✅ Build WhatsApp link using the listing's ownerPhone (not User model)
        if (entity.getOwnerPhone() != null && !entity.getOwnerPhone().isEmpty()) {
            String sanitizedPhone = entity.getOwnerPhone().replaceAll("[^0-9+]", "");
            String whatsappLink = "https://wa.me/" + sanitizedPhone
                    + "?text=Hi%20I%20saw%20your%20room%20listing%20on%20CampNest";
            dto.setWhatsappLink(whatsappLink);
        }


        return dto;
    }
}

