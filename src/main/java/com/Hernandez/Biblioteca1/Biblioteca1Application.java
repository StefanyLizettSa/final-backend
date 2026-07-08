package com.Hernandez.Biblioteca1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Biblioteca1Application {

	public static void main(String[] args) {
	SpringApplication.run(Biblioteca1Application.class, args);
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📚 BIBLIOTECA API INICIADA CORRECTAMENTE");
        System.out.println("=".repeat(60));
        System.out.println("🔗 API Base:      http://localhost:8080");
        System.out.println("📖 Swagger UI:    http://localhost:8080/swagger-ui.html");
        System.out.println("📄 OpenAPI JSON:  http://localhost:8080/v3/api-docs");
        System.out.println("=".repeat(60) + "\n");
	}
}
