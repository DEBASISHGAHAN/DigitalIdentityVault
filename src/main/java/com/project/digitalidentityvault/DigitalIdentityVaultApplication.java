package com.project.digitalidentityvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableScheduling
@EnableWebSecurity
public class DigitalIdentityVaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalIdentityVaultApplication.class, args);
    }

}
