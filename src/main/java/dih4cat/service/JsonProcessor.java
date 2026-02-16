package dih4cat.service;

import dih4cat.algorithm.GibertDistance;
import dih4cat.domain.CtrlDomain;
import dih4cat.structures.QueryConfig;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;

@Service
public class JsonProcessor {

    private final Gson gson = new Gson();
    private final ApplicationInitializationService initializationService;
    private final GibertDistance gibertDistance;
    private CtrlDomain ctrlDomain;

    /**
     * Constructor con inyección de dependencias.
     * @param initializationService Servicio de inicialización
     * @param gibertDistance Servicio de algoritmo de distancia
     */
    public JsonProcessor(ApplicationInitializationService initializationService, GibertDistance gibertDistance) {
        this.initializationService = initializationService;
        this.gibertDistance = gibertDistance;
    }

    /**
     * Inicializa la aplicación usando el servicio de inicialización centralizado
     */
    public void initialize() {
        initializationService.initialize();
        this.ctrlDomain = initializationService.getCtrlDomain();
    }

    /**
     * Procesa la configuración de consulta y genera recomendaciones
     * Guarda resultados en JSON y PDF
     */
    public Object procesarConfig(QueryConfig config, String outputBasePathNoExt) throws Exception {
        // Validar que el dominio esté inicializado
        if (ctrlDomain == null) {
            throw new IllegalStateException("Aplicación no inicializada. Llama a initialize() primero.");
        }
        
        // Conjuntos auxiliares
        Set<String> usefullTags = new HashSet<>();
        Set<String> unusedTags = new HashSet<>();

        // Procesar las distancias de cursos
        ctrlDomain.courseDistances(
                config.tags,
                usefullTags,
                unusedTags,
                config.modality,
                config.userStatus,
                config.minDuration,
                config.maxDuration,
                config.organizers,
                config.format,
                config.duration,
                config.organizer,
                config.status,
                config.strongTags,
                config.numCourses,
                config.fromTo,
                config.untilTo);

        // Mostrar tags útiles y no encontrados
        System.out.println("Tags útiles: " + usefullTags);
        System.out.println("Tags no encontrados: " + unusedTags);

        // Guardar resultados en JSON y PDF usando la instancia inyectada
        gibertDistance.saveRecommendationsAsJson(outputBasePathNoExt + ".json");
        gibertDistance.saveRecommendationsAsPDF(outputBasePathNoExt + ".pdf");

        // Leer y retornar el JSON generado
        try (FileReader reader = new FileReader(outputBasePathNoExt + ".json", StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, Object.class);
        }
    }

    /**
     * Actualiza la base de datos recargando los datos desde el archivo
     */
    public boolean updateDB() {
        if (ctrlDomain == null) {
            throw new IllegalStateException("Aplicación no inicializada. Llama a initialize() primero.");
        }
        
        System.out.println("Actualizando base de datos de cursos");
        ctrlDomain.initializeData(true, initializationService.getDataPath());
        return true;
    }

    /**
     * Obtiene todos los nodos de la ontología (excepto la raíz)
     */
    public Set<String> getOntologyNodes() {
        if (ctrlDomain == null) {
            throw new IllegalStateException("Aplicación no inicializada. Llama a initialize() primero.");
        }
        
        Set<String> allNodes = ctrlDomain.getTags();
        Set<String> filteredNodes = new HashSet<>(allNodes);
        filteredNodes.remove("arrel"); // Remover raíz
        return filteredNodes;
    }
}