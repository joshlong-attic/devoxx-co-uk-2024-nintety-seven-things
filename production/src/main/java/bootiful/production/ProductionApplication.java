package bootiful.production;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class ProductionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductionApplication.class, args);
    }

    @Bean
    RestClient http() {
        return RestClient.builder().build();
    }
}

@Controller
@ResponseBody
class ReliableController {

    private final CircuitBreaker cb;

    private final RestClient http;

    ReliableController(RestClient http, CircuitBreakerFactory<?,?> circuitBreakerFactory) {
        this.cb = circuitBreakerFactory.create("try");
        this.http = http;
    }

    @GetMapping("/try")
    ResponseEntity<?> call() {
        return this.cb.run(() -> this.http
                        .get()
                        .uri("https://httpbingo.org/unstable")
                        .retrieve()
                        .toEntity(new ParameterizedTypeReference<>() {
                        }),
                throwable -> ResponseEntity.notFound().build());
    }

}