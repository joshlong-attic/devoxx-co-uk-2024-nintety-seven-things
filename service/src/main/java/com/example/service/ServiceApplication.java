package com.example.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.web.servlet.function.RouterFunctions.route;
import static org.springframework.web.servlet.function.ServerResponse.ok;

@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    private final AtomicInteger port = new AtomicInteger(-1);

    @EventListener
    void serverReady(WebServerInitializedEvent serverInitializedEvent) {
        this.port.set(serverInitializedEvent.getWebServer().getPort());
    }

    @Bean
    RouterFunction<ServerResponse> get() {
        return route()
                .GET("/hello", request -> ok().body(Map.of("message", "Hello from port " + port.get())))
                .build();
    }
}
