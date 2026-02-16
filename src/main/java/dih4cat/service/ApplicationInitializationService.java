package dih4cat.service;

import dih4cat.algorithm.GibertDistance;
import dih4cat.config.ApplicationPaths;
import dih4cat.config.ApplicationConfiguration;
import dih4cat.domain.CtrlDomain;
import dih4cat.graph.Graph;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Servicio centralizado para la inicialización de la aplicación.
 * Encapsula la lógica de carga de configuración, ontología y datos.
 * Utilizado tanto por la API REST como por la CLI.
 */
@Service
public class ApplicationInitializationService {
    
    private String dataPath = "";
    private String ontologyPath = "";
    private boolean hasOntology = false;
    private boolean hasData = false;
    private final CtrlDomain ctrlDomain;
    private final Graph graph;
    private final GibertDistance gibertDistance;
    private final ApplicationConfiguration appConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Constructor con inyección de dependencias.
     * @param ctrlDomain Servicio de control del dominio
     * @param graph Servicio del grafo de ontología
     * @param gibertDistance Servicio del algoritmo de distancia
     */
    public ApplicationInitializationService(CtrlDomain ctrlDomain, Graph graph, GibertDistance gibertDistance, ApplicationConfiguration appConfig) {
        this.ctrlDomain = ctrlDomain;
        this.graph = graph;
        this.gibertDistance = gibertDistance;
        this.appConfig = appConfig;
    }
    
    /**
     * Inicializa la aplicación cargando:
     * 1. Rutas desde settings/paths.json con Jackson
     * 2. Dominio (CtrlDomain)
     * 3. Algoritmo de distancia (GibertDistance)
     * 4. Grafo de ontología si existe
     * 5. Datos si existen
     */
    public void initialize() {
        loadPaths();
        initializeDomain();
        loadOntology();
        loadData();
    }
    
    /**
     * Carga las rutas desde settings/paths.json usando Jackson ObjectMapper
     * Reemplaza el parsing manual anterior con deserialización robusta.
     */
    private void loadPaths() {
        String jsonFilePath = appConfig != null ? appConfig.getConfig() : "settings/paths.json";
        System.out.println("Cargando configuración desde: " + jsonFilePath);
        System.out.println("Directorio de trabajo: " + System.getProperty("user.dir"));
        
        try {
            // Deserializar JSON usando Jackson
            ApplicationPaths config = objectMapper.readValue(
                new File(jsonFilePath), 
                ApplicationPaths.class
            );
            
            // Extraer y validar rutas
            dataPath = config.getDataPath() != null ? config.getDataPath() : "";
            ontologyPath = config.getOntologyPath() != null ? config.getOntologyPath() : "";
            
            validatePaths();
            logPathStatus();
            
        } catch (IOException e) {
            System.err.println("Error al cargar configuración desde " + jsonFilePath);
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("Se procederá sin rutas configuradas (ontología y datos deshabilitados)");
            dataPath = "";
            ontologyPath = "";
            hasOntology = false;
            hasData = false;
        }
    }
    
    /**
     * Valida la existencia de los archivos de ruta
     */
    private void validatePaths() {
        hasOntology = ontologyPath != null && Files.exists(Paths.get(ontologyPath)) && !ontologyPath.isBlank();
        hasData = dataPath != null && Files.exists(Paths.get(dataPath)) && !dataPath.isBlank();
    }
    
    /**
     * Registra el estado de carga de las rutas
     */
    private void logPathStatus() {
        System.out.printf("Ruta Ontología: %s%n", ontologyPath);
        System.out.printf("Ruta Datos: %s%n", dataPath);
        System.out.printf("Ontología encontrada: %s%n", hasOntology);
        System.out.printf("Datos encontrados: %s%n", hasData);
    }
    
    /**
     * Inicializa el dominio y su algoritmo de distancia
     */
    private void initializeDomain() {
        // Establecer dependencias bidireccionales para evitar ciclos
        // CtrlDomain ↔ GibertDistance
        ctrlDomain.setGibertDistance(gibertDistance);
        gibertDistance.setCtrlDomain(ctrlDomain);
        
        // Graph ↔ GibertDistance
        graph.setGibertDistance(gibertDistance);
    }
    
    /**
     * Carga la ontología en el grafo si existe
     */
    private void loadOntology() {
        if (hasOntology) {
            System.out.println("Cargando ontología desde: " + ontologyPath);
            graph.importFile(new File(ontologyPath));
            ctrlDomain.completeMatrix();
            System.out.println("Ontología cargada correctamente");
        } else {
            System.out.println("Advertencia: Ontología no disponible");
        }
    }
    
    /**
     * Carga los datos si existen
     */
    private void loadData() {
        if (hasData) {
            System.out.println("Cargando datos desde: " + dataPath);
            ctrlDomain.initializeData(true, dataPath);
            System.out.println("Datos cargados correctamente");
        } else {
            System.out.println("Advertencia: Datos no disponibles");
        }
    }
    
    // Getters
    public String getDataPath() {
        return dataPath;
    }
    
    public String getOntologyPath() {
        return ontologyPath;
    }
    
    public boolean hasOntology() {
        return hasOntology;
    }
    
    public boolean hasData() {
        return hasData;
    }
    
    public CtrlDomain getCtrlDomain() {
        return ctrlDomain;
    }
}
