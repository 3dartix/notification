package ru.geekbrains.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "ru.geekbrains")
@ComponentScan(basePackages = {"ru.geekbrains"})
@EntityScan(basePackages = {"ru.geekbrains"})
@EnableJpaRepositories(basePackages = {"ru.geekbrains"})
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
