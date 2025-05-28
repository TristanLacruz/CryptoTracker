package com.tracker.backend;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class CryptoTrackerApplication {

    @Autowired
    private MongoTemplate mongoTemplate;

    public static void main(String[] args) {
        SpringApplication.run(CryptoTrackerApplication.class, args);
    }

    // @PostConstruct
    // public void testMongo() {
    //     System.out.println("📁 Colecciones en Atlas:");
    //     mongoTemplate.getDb().listCollectionNames().forEach(System.out::println);
    // }
}
