package com.ssup2ket.store.config;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.ssup2ket.store.domain.repository",
    entityManagerFactoryRef = "secondaryEntityManagerFactory",
    transactionManagerRef = "secondaryTransactionManager",
    includeFilters =
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = {".*Secondary.*"}))
public class DataSourceSecondaryConfig {
  static String test = "test";

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.secondary")
  public DataSource secondaryDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
      EntityManagerFactoryBuilder builder) {
    return builder
        .dataSource(secondaryDataSource())
        .packages("com.ssup2ket.store.domain.model")
        .persistenceUnit("secondary")
        .build();
  }

  @Bean
  PlatformTransactionManager secondaryTransactionManager(EntityManagerFactoryBuilder builder) {
    return new JpaTransactionManager(secondaryEntityManagerFactory(builder).getObject());
  }
}
