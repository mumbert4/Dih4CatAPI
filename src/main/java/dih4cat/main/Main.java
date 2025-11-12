package dih4cat.main;

import dih4cat.algorithm.GibertDistance;
import dih4cat.domain.CtrlDomain;
import dih4cat.estructures.QueryConfig;
import dih4cat.graph.Graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.*;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static String data = "";
    public static String onto = "";
    public static boolean getOnto,getData;
    private static CtrlDomain CDomain;
    public static void main(String[] args) {
        getOnto = false;
        getData = false;
        getPaths();
        CDomain = CtrlDomain.getInstance();
        CDomain.setGibert();
        if(getOnto){
            Graph.getInstance().importFile(new File(onto));
            CDomain.completeMatrix();
        }
        if(getData){
            CDomain.initializeData(true, data);
        }

        Scanner scanner = new Scanner(System.in);
        Gson gson = new Gson();

        while (true) {
            System.out.println("Introduce un número (1, 2, 3...) o 'd' para salir:");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("d")) {
                System.out.println("Saliendo del programa...");
                break;
            }

            String examplePath = "settings/examples/" + input + ".json";
            File exampleFile = new File(examplePath);
            if (exampleFile.exists()) {
                try {
                    String content = new String(Files.readAllBytes(exampleFile.toPath()));
                    QueryConfig config = gson.fromJson(content, QueryConfig.class);
                    System.out.println("Configuración cargada:");
                    System.out.println(config);

                    // Crear conjuntos auxiliares
                    Set<String> usefullTags = new HashSet<>();
                    Set<String> unusedTags = new HashSet<>();

                    // Llamar al método del dominio
                    CDomain.courseDistances(
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
                            config.numCourses
                    );

                    // Mostrar información de salida
                    System.out.println("Tags útiles: " + usefullTags);
                    System.out.println("Tags no encontrados: " + unusedTags);
                    String outputPath = "settings/output/"+input;
                    System.out.println(outputPath);
                    System.out.println("Iniciando el programa...");
                    System.out.println("Path de salida: " + outputPath);
                    GibertDistance.getInstance().saveRecommendationsAsJson(outputPath+".json");
                    GibertDistance.getInstance().saveRecommendationsAsPDF(outputPath+input+".pdf");
                } catch (IOException e) {
                    System.err.println("Error al leer el archivo: " + e.getMessage());
                }
            }
            else {
                System.err.println("Archivo no encontrado: " + examplePath);
            }
        }
    }


    public static void getPaths(){
        String jsonFilePath = "settings/paths.json"; // Ruta del fichero JSON
        String pathOnt = null;
        System.out.println(System.getProperty("user.dir"));
        String pathData = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if line contains "pathOnt" and extract the value
                if (line.contains("\"pathOnt\"")) {
                    pathOnt = extractPath(line);
                }
                // Check if line contains "pathData" and extract the value
                else if (line.contains("\"pathData\"")) {
                    pathData = extractPath(line);
                }
            }

            // Set extracted paths to variables (assuming `data` and `onto` are accessible)
            System.out.printf("pathOnt: %s\npathData: %s\n",pathOnt,pathData);
            data = pathData;
            onto = pathOnt;
            System.out.println(data);
            System.out.println(onto);
            // Check if files exist at the specified paths
            getData = pathData != null && Files.exists(Paths.get(pathData)) && !pathData.isBlank();
            getOnto = pathOnt != null && Files.exists(Paths.get(pathOnt)) && !pathOnt.isBlank();
            System.out.println("MAIN GETPATHS: "+ getData + " " + getOnto);

        } catch (IOException e) {
            System.out.println("NOT FOUND");
            e.printStackTrace();
        }
    }

    private static String extractPath(String line) {
        // Find start and end of the path value within the quotes
        int start = line.indexOf(":") + 3; // Position after colon and opening quote
        int end = line.lastIndexOf("\"");  // Position of closing quote
        return line.substring(start, end);
    }
}