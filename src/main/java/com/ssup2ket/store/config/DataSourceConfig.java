package com.ssup2ket.store.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(order = Ordered.HIGHEST_PRECEDENCE)
public class DataSourceConfig {}
