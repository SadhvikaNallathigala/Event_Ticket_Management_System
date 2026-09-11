package com.example.eventticketmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class IndianTimeConfig {

    @Bean
    public Clock indianClock() {
        return Clock.system(ZoneId.of("Asia/Kolkata"));
    }
}