package com.ssup2ket.store.config;

import com.ssup2ket.store.pkg.auth.AccessToken;
import com.ssup2ket.store.pkg.auth.AccessTokenProvider;
import com.ssup2ket.store.proto.ProductGrpc;
import com.ssup2ket.store.proto.StoreGrpc;
import java.util.ArrayList;
import java.util.List;
import net.devh.boot.grpc.server.security.authentication.BearerAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.check.AccessPredicate;
import net.devh.boot.grpc.server.security.check.AccessPredicateVoter;
import net.devh.boot.grpc.server.security.check.GrpcSecurityMetadataSource;
import net.devh.boot.grpc.server.security.check.ManualGrpcSecurityMetadataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;

@Configuration
public class GrpcSecurityConfig {
  @Bean
  GrpcSecurityMetadataSource grpcSecurityMetadataSource() {
    final ManualGrpcSecurityMetadataSource source = new ManualGrpcSecurityMetadataSource();
    source.set(StoreGrpc.getListStoreMethod(), AccessPredicate.permitAll());
    source.set(StoreGrpc.getGetStoreMethod(), AccessPredicate.permitAll());
    source.set(ProductGrpc.getListProductMethod(), AccessPredicate.permitAll());
    source.set(ProductGrpc.getGetProductMethod(), AccessPredicate.permitAll());
    source.setDefault(AccessPredicate.authenticated());
    return source;
  }

  @Bean
  AccessDecisionManager grpcAccessDecisionManager() {
    final List<AccessDecisionVoter<?>> voters = new ArrayList<>();
    voters.add(new AccessPredicateVoter());
    return new UnanimousBased(voters);
  }

  @Bean
  AuthenticationManager grpcAuthenticationManager() {
    final List<AuthenticationProvider> providers = new ArrayList<>();
    providers.add(new AccessTokenProvider());
    return new ProviderManager(providers);
  }

  @Bean
  GrpcAuthenticationReader grpcAuthenticationReader() {
    return new BearerAuthenticationReader(accessToken -> new AccessToken(accessToken, null));
  }
}
