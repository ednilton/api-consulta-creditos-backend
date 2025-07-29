package com.creditos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

//@EnableKafka
@SpringBootApplication
public class ConsultaCreditosApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsultaCreditosApiApplication.class, args);
    }
}
