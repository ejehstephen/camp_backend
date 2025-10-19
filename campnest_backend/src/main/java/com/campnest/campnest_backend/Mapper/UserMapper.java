package com.campnest.campnest_backend.Mapper;


import com.campnest.campnest_backend.dto.UserDTO;
import com.campnest.campnest_backend.model.User;

public class UserMapper {

    public static UserDTO toDTO (User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setSchool(user.getSchool());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setProfileImage(user.getProfileImage());
        return dto;
    }
}
