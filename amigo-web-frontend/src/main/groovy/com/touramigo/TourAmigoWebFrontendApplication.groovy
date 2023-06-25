package com.touramigo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.feign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableScheduling
@EnableConfigurationProperties
class TourAmigoWebFrontendApplication {

    static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(TourAmigoWebFrontendApplication)
                .run(args)
    }

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }
}
