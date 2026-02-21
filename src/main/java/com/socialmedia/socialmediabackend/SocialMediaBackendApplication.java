package com.socialmedia.socialmediabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SocialMediaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialMediaBackendApplication.class, args);
    }
}
