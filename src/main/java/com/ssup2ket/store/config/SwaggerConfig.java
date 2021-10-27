package com.ssup2ket.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
  @Bean
  public Docket postsApi() {
    return new Docket(DocumentationType.OAS_30)
        .apiInfo(this.apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.ssup2ket.store.server.http.controller"))
        .paths(PathSelectors.any())
        .build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("ssup2ket store service")
        .description("ssup2ket store service")
        .version("1.0")
        .build();
  }
}
