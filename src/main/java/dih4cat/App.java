package dih4cat;

import dih4cat.service.JsonProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Punto de entrada de la aplicación Spring Boot (API REST)
 */
@SpringBootApplication
public class App {
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    /**
     * Bean que se ejecuta al iniciar Spring Boot
     * Inicializa la aplicación (carga ontología, datos, etc.)
     */
    @Bean
    public CommandLineRunner initializeApplication(JsonProcessor jsonProcessor) {
        return args -> {
            System.out.println("Inicializando aplicación...");
            jsonProcessor.initialize();
            System.out.println("Aplicación inicializada correctamente");
        };
    }
}