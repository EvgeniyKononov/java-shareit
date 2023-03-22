package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItServer {

    public static void main(String[] args) {
        System.setProperty("server.port", "9090");
        SpringApplication.run(ShareItServer.class, args);
    }
}
