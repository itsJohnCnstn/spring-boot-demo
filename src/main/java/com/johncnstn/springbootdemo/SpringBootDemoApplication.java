package com.johncnstn.springbootdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * ✅ Marks this class as the entry point for the Spring Boot application.
 *
 * Combines three annotations:
 *   - @Configuration → allows defining beans with @Bean methods
 *   - @EnableAutoConfiguration → enables Spring Boot’s auto-config (e.g. DataSource, Web MVC)
 *   - @ComponentScan → scans this package and subpackages for @Component, @Service, @Controller, etc.
 *
 * This annotation bootstraps almost everything automatically,
 * minimizing boilerplate and reducing the need for manual XML config.
 */
@SpringBootApplication
public class SpringBootDemoApplication {

    /*
     * ✅ Main method — entry point for any Java application.
     *
     * SpringApplication.run():
     *   - Bootstraps the Spring context
     *   - Starts the embedded web server (Tomcat by default)
     *   - Initializes beans, scans for components, loads application.properties
     *
     * This is where the magic of auto-configuration begins.
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoApplication.class, args);
    }

}
