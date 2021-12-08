package com.ssup2ket.store.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
  @Bean
  public OpenAPI postsApi() {
    return new OpenAPI().components(new Components()).info(getInfo());
  }

  private Info getInfo() {
    return new Info()
        .title("ssup2ket store service")
        .description("ssup2ket store service")
        .version("1.0.0");
  }
}
