package com.cdac.QBD;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * MAIN APPLICATION CLASS
 *
 * PURPOSE:
 * --------
 * Entry point of the Spring Boot application.
 *
 * NOTE:
 * -----
 * - No security is enabled at this stage
 * - APIs are open for development and testing
 * - JWT & security will be added later
 */
@SpringBootApplication
public class QbdApplication {

    public static void main(String[] args) {
        SpringApplication.run(QbdApplication.class, args);
    }
}
