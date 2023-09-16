package ru.practicum.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.practicum"})
public class EwmServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ru.practicum.main.EwmServiceApplication.class, args);
    }
}
