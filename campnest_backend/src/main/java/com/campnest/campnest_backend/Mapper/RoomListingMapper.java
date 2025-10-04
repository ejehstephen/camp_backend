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

        // ✅ Build WhatsApp link if owner has a phone number
        User owner = entity.getOwner();
        if (owner.getPhoneNumber() != null) {
            String whatsappLink = "https://wa.me/" + owner.getPhoneNumber()
                    + "?text=Hi%20I%20saw%20your%20room%20listing%20on%20CampNest";
            dto.setWhatsappLink(whatsappLink);
        }

        return dto;
    }
}

