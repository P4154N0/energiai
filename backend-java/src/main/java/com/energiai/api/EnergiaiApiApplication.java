package com.energiai.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;

// Excluimos temporalmente DataSource y Flyway para probar el servidor Web
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		FlywayAutoConfiguration.class
})
public class EnergiaiApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnergiaiApiApplication.class, args);
	}
}