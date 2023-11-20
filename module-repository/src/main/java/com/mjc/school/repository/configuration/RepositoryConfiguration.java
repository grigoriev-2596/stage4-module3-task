package com.mjc.school.repository.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaAuditing
@EnableTransactionManagement
@EntityScan(basePackages = "com.mjc.school.repository.entity")
@EnableAutoConfiguration
@Configuration
public class RepositoryConfiguration {
}
