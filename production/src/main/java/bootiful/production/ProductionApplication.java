package bootiful.production;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;

import java.util.Map;

@SpringBootApplication
public class ProductionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductionApplication.class, args);
    }

    @Bean
    RetryTemplate retryTemplate (){
        return new RetryTemplate();
    }

    @Bean
    @LoadBalanced
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
}

@Controller
@ResponseBody
class ReliableController {

    private final CircuitBreaker circuitBreaker;

    private final RestClient http;

    //private final RetryTemplate retryTemplate;

    ReliableController(
            RestClient http,
            CircuitBreakerFactory<?, ?> circuitBreakerFactory
            // , RetryTemplate retryTemplate
    ) {
        this.circuitBreaker = circuitBreakerFactory.create("try");
        this.http = http;
        //this.retryTemplate = retryTemplate;
    }

    @GetMapping("/try")
    ResponseEntity<?> call() {
        var ptr = new ParameterizedTypeReference<Map<String, String>>() { };
        return // retryTemplate.execute(context ->
                this.circuitBreaker
                .run(
                        () -> http.get().uri("http://service/oops").retrieve().toEntity(ptr),
                        throwable -> ResponseEntity.ok().body(Map.of("message", "oops!"))
                );
        //);
    }
}