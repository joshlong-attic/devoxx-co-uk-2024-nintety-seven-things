package bootiful.production;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RouterFunctions.route;
import static org.springframework.web.servlet.function.ServerResponse.*;

@SpringBootApplication
public class ProductionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductionApplication.class, args);
    }

    @Bean
    @LoadBalanced
    RestClient.Builder builder() {
        return RestClient.builder();
    }



    @Bean
    RouterFunction<ServerResponse> routes(RestClient.Builder builder) {
        var restClient = builder.build() ;
        return route()
                .GET("/{seconds}", request -> {
                    var body = restClient
                            .get()
                            .uri("http://service/hello")
                            .retrieve()
                            .body(String.class);

                    return ok().body(body);
                })
                .build();

    }
}

