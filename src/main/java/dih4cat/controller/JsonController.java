package dih4cat.controller;

import com.google.gson.JsonElement;

import dih4cat.service.JsonProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class JsonController {

    private final JsonProcessor jsonProcessor;

    public JsonController(JsonProcessor jsonProcessor) {
        this.jsonProcessor = jsonProcessor;
    }

    @GetMapping("/procesar")
    public ResponseEntity<Object> procesar(@RequestParam String archivo) {
        try {
            System.out.println("archivo: " + archivo);
            Object result = jsonProcessor.procesarArchivo(archivo);
            System.out.println("Resultado: ");
            System.out.println(result);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}