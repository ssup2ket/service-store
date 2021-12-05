package com.ssup2ket.store.config;

import com.ssup2ket.store.pkg.auth.AccessTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired private AccessTokenFilter accessTokenFilter;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();

    http.addFilterBefore(accessTokenFilter, BasicAuthenticationFilter.class);

    http.authorizeRequests()
        .antMatchers(HttpMethod.GET, "/v1/stores/**/products/**")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/v1/stores/**/products")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/v1/stores/**")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/v1/stores")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/healthz")
        .permitAll()
        .anyRequest()
        .authenticated();
  }
}
