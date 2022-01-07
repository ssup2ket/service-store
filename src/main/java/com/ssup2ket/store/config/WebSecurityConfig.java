package com.ssup2ket.store.config;

import com.ssup2ket.store.pkg.auth.AccessTokenFilter;
import com.ssup2ket.store.server.http.error.HttpAuthExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired private AccessTokenFilter accessTokenFilter;
  @Autowired private HttpAuthExceptionHandler accessTokenExceptionHandler;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    //
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    // Disable CSRF
    http.csrf().disable();

    // Set access token filter and exception handling
    http.addFilterBefore(accessTokenFilter, BasicAuthenticationFilter.class)
        .exceptionHandling()
        .authenticationEntryPoint(accessTokenExceptionHandler);

    // Set authorization by path
    http.authorizeRequests()
        .antMatchers(HttpMethod.GET, "/v1/stores/**/products/**")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/v1/stores/**")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/v1/swagger/**") // Swagger
        .permitAll()
        .antMatchers(HttpMethod.GET, "/v1/swagger-ui/**") // Swagger
        .permitAll()
        .antMatchers(HttpMethod.GET, "/v1") // Swagger
        .permitAll()
        .antMatchers(HttpMethod.GET, "/healthz")
        .permitAll()
        .anyRequest()
        .authenticated();
  }
}
