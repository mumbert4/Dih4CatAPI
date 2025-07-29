package dih4cat;

import dih4cat.service.JsonProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    public static void main(String[] args) {

        JsonProcessor.initialize();
        SpringApplication.run(App.class, args);
    }
}