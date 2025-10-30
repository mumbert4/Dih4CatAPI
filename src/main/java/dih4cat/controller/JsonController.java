package dih4cat.controller;

import com.google.gson.JsonElement;

import dih4cat.estructures.QueryConfig;
import dih4cat.service.JsonProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
public class JsonController {

    private final JsonProcessor jsonProcessor;

    public JsonController(JsonProcessor jsonProcessor) {
        this.jsonProcessor = jsonProcessor;
    }

    @PostMapping("/procesar")
    public ResponseEntity<Object> procesar(@RequestBody QueryConfig config) {
        try {
            String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now());
            String outputBase = "settings/output/request-" + timestamp; // sin extensión
            Object result = jsonProcessor.procesarConfig(config,outputBase); // ver abajo
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno al procesar el JSON");
        }
    }
}