package dih4cat.cli;

import dih4cat.algorithm.GibertDistance;
import dih4cat.config.ApplicationConfiguration;
import dih4cat.domain.CtrlDomain;
import dih4cat.structures.QueryConfig;
import dih4cat.service.ApplicationInitializationService;
import dih4cat.graph.Graph;
import dih4cat.item.ItemManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.Gson;

/**
 * Aplicación CLI (Command Line Interface) para el sistema de recomendación.
 * Permite consultar ejemplos y procesar recomendaciones de forma interactiva.
 * 
 * NOTE: Esta clase es una aplicación CLI legacy. La entrada principal de
 * Spring Boot está en dih4cat.App. Para usar esta CLI, ejecutar como aplicación
 * standalone sin Spring.
 */
public class CommandLineApplication {
    
    private static ApplicationInitializationService initializationService;
    private static CtrlDomain ctrlDomain;
    private static GibertDistance gibertDistance;
    private static Gson gson;
    private static ApplicationConfiguration appConfig;

    public static void main(String[] args) {
        // Inicializar dependencias manualmente para ejecución standalone
        ItemManager itemManager = new ItemManager();
        Graph graph = new Graph();

        // Cargar configuración desde variables de entorno para ejecución standalone
        appConfig = new ApplicationConfiguration();
        String cfg = System.getenv("CONFIG_PATH");
        if (cfg != null && !cfg.isBlank()) appConfig.setConfig(cfg);
        String ex = System.getenv("EXAMPLES_PATH");
        if (ex != null && !ex.isBlank()) appConfig.setExamples(ex);
        String out = System.getenv("OUTPUT_PATH");
        if (out != null && !out.isBlank()) appConfig.setOutput(out);
        String mat = System.getenv("MATRIX_PATH");
        if (mat != null && !mat.isBlank()) appConfig.setMatrix(mat);

        gibertDistance = new GibertDistance(itemManager, appConfig);
        ctrlDomain = new CtrlDomain(itemManager, graph);
        
        // Establecer dependencias bidireccionales
        ctrlDomain.setGibertDistance(gibertDistance);
        gibertDistance.setCtrlDomain(ctrlDomain);
        graph.setGibertDistance(gibertDistance);
        
        // Inicializar aplicación
        initializationService = new ApplicationInitializationService(ctrlDomain, graph, gibertDistance, appConfig);
        initializationService.initialize();
        gson = new Gson();

        // Iniciar CLI interactivo
        runCLI();
    }

    /**
     * Ejecuta el loop interactivo de la CLI
     * Permite al usuario cargar ejemplos y procesar recomendaciones
     */
    private static void runCLI() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Sistema de Recomendación de Cursos (CLI) ===");
        System.out.println("Introduce el número de ejemplo (1, 2, 3...) o 'q' para salir");
        System.out.println();

        while (true) {
            System.out.print("Ejemplo: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) {
                System.out.println("¡Hasta luego!");
                break;
            }

            processExample(input);
        }

        scanner.close();
    }

    /**
     * Procesa un ejemplo de consulta
     * 
     * @param exampleNumber Número del ejemplo a procesar
     */
    private static void processExample(String exampleNumber) {
        String examplesBase = (appConfig != null && appConfig.getExamples() != null) ? appConfig.getExamples() : "settings/examples";
        String examplePath = examplesBase + "/" + exampleNumber + ".json";
        File exampleFile = new File(examplePath);

        if (!exampleFile.exists()) {
            System.err.println("❌ Archivo no encontrado: " + examplePath);
            return;
        }

        try {
            // Cargar configuración desde el archivo
            String content = new String(Files.readAllBytes(exampleFile.toPath()));
            QueryConfig config = gson.fromJson(content, QueryConfig.class);
            
            System.out.println("\n✓ Configuración cargada:");
            System.out.println(config);

            // Procesar consulta
            processQuery(config, exampleNumber);

        } catch (IOException e) {
            System.err.println("❌ Error al leer archivo: " + e.getMessage());
        }
    }

    /**
     * Procesa una consulta y genera recomendaciones
     */
    private static void processQuery(QueryConfig config, String exampleNumber) {
        // Crear conjuntos auxiliares para tags
        Set<String> usefullTags = new HashSet<>();
        Set<String> unusedTags = new HashSet<>();

        // Ejecutar algoritmo de distancia
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

        // Mostrar resultados
        System.out.println("\n✓ Resultados:");
        System.out.println("  Tags útiles: " + usefullTags);
        System.out.println("  Tags no encontrados: " + unusedTags);

        // Guardar salida
        String outputBase = (appConfig != null && appConfig.getOutput() != null) ? appConfig.getOutput() : "settings/output";
        String outputPath = outputBase + "/" + exampleNumber;
        gibertDistance.saveRecommendationsAsJson(outputPath + ".json");
        gibertDistance.saveRecommendationsAsPDF(outputPath + ".pdf");
        
        System.out.println("  📄 Recomendaciones guardadas en: " + outputPath + ".{json,pdf}");
        System.out.println();
    }
}
