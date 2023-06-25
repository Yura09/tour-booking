package com.touramigo.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableDiscoveryClient
@SpringBootApplication
@EnableSwagger2
@EnableFeignClients
public class UsersServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsersServiceApplication.class, args);
    }

    @Bean
    public Docket awesomeApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.awesomeApiInfo())
                .select()
                .paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("com.touramigo.service.controller"))
                .build();
    }


    private ApiInfo awesomeApiInfo() {
        return new ApiInfoBuilder()
                .title("amigo-users-service")
                .description("Users services. To call endpoints through Gateway API Service use '/api/auth' prefix if path. Example: gateway-api-service-url:8090/api/auth/users/.")
                .version("0.1")
                .build();
    }
}
