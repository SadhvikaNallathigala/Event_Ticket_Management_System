package com.example.eventticketmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EventTicketManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                EventTicketManagementApplication.class,
                args
        );
    }
}