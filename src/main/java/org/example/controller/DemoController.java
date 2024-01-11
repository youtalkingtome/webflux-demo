package org.example.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
public class DemoController {

        @GetMapping("/hello")
        public Mono<String> hello() {
            return Mono.just("Hello, Spring WebFlux!");
        }

}
