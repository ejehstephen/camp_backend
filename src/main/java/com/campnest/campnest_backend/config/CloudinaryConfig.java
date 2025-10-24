package com.campnest.campnest_backend.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dkz95najt",
                "api_key", "538688576271882",
                "api_secret", "D_o6KNGridJzbJw1wQFgUqaLw1s"
        ));
    }
}
