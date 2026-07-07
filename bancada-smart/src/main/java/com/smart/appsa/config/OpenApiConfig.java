package com.smart.appsa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bancada Smart 4.0 API")
                        .description("API de controle da bancada industrial de montagem automatizada. "
                                + "Antes de usar endpoints de escrita (POST/PATCH/PUT), inicie a comunicação com os CLPs via POST /api/smart/start.")
                        .version("1.0.0"));
    }
}
