package com.example.boatrental.boats.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.boatrental.boats.repository")
@EntityScan(basePackages = "com.example.boatrental.boats.domain")
public class BoatsTestConfiguration {
}
