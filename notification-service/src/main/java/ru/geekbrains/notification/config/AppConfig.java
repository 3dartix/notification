package ru.geekbrains.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

@Configuration
public class AppConfig {
    @Bean
    public TelegramBotsApi TelegramBotsApi(){
        ApiContextInitializer.init();
        return new TelegramBotsApi();
    }

    @Bean
    public RestTemplate createRestTemplate(){
        return new RestTemplate();
    }
}
