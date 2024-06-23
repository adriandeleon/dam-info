package com.grokthecode.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${app.name}")
    private String appName;
    @Value("${app.version}")
    private String appVersion;
    @Value("${app.description}")
    private String appDescription;
    @Value("${app.maintainer}")
    private String appMaintainer;
    @Value("${app.github.url}")
    private String gitHubUrl;
    @Value("${app.license.url}")
    private String licenseUrl;

        @Bean
        public OpenAPI usersMicroserviceOpenAPI() {
            return new OpenAPI()
                    .info(new Info().title(appName)
                            .description(appDescription)
                            .version(appVersion));
        }
    }
