package com.example.demo;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class Application {
    private EmitterProcessor<Message<String>> processor = EmitterProcessor.create(false);

    @Bean
    public Supplier<Flux<Message<String>>> supplier() {
        return () -> processor;
    }

    @Bean
    public Consumer<Flux<String>> consumer() {
        return msg -> msg
                .map(m -> MessageBuilder
                        .withPayload(m)
                        .setHeader("spring.cloud.stream.sendto.destination", "topic-out")
                        .build())
                .doOnNext(processor::onNext)
                .subscribe();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
