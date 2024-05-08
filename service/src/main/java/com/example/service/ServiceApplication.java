package com.example.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Controller
@ResponseBody
@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    private final AtomicInteger port = new AtomicInteger(-1);

    @EventListener
    void serverReady(WebServerInitializedEvent event) {
        port.set(event.getWebServer().getPort());
    }

    @GetMapping("/oops")
    ResponseEntity<?> oops() {
        return Math.random() > .5 ?
                ResponseEntity.ok(Map.of("message", "Hello from port " + this.port.get())) :
                ResponseEntity.notFound().build();
    }

}
