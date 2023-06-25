package com.touramigo.service.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
@EnableAutoConfiguration
public class SwaggerAggregatorController implements SwaggerResourcesProvider {

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources= new ArrayList<>();
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName("amigo-users-service");
        swaggerResource.setLocation("/api/auth/v2/api-docs");
        swaggerResource.setSwaggerVersion("2.0");
        resources.add(swaggerResource);

        SwaggerResource swaggerResource3 = new SwaggerResource();
        swaggerResource3.setName("amigo-gateway-api-service");
        swaggerResource3.setLocation("/v2/api-docs");
        swaggerResource3.setSwaggerVersion("2.0");
        resources.add(swaggerResource3);

        return resources;
    }
}
