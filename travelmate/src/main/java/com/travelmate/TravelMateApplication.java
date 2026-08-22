package com.travelmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TravelMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelMateApplication.class, args);
        System.out.println("=================================================");
        System.out.println(" TravelMate Day-Visit Planner is running!");
        System.out.println(" Tourist Portal: http://localhost:8080");
        System.out.println(" Admin Portal:   http://localhost:8080/admin.html");
        System.out.println(" REST API:       http://localhost:8080/api/attractions");
        System.out.println("=================================================");
    }
}
