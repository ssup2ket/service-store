package com.ssup2ket.store.server.http.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class SwaggerController {
  @RequestMapping("/v1")
  public String redirectToSwagger() {
    return "redirect:/v1/swagger/swagger-ui/";
  }
}
