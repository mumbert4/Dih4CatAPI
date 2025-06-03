package main;

import domain.CtrlDomain;
import estructures.QueryConfig;
import graph.Graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
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
                    // Puedes pasar `config` a CtrlDomain u otros módulos aquí

                } catch (IOException e) {
                    System.err.println("Error al leer el archivo: " + e.getMessage());
                }
            } else {
                System.err.println("Archivo no encontrado: " + examplePath);
            }
        }
    }


    public static void getPaths(){
        String jsonFilePath = "settings/paths.json"; // Ruta del fichero JSON
        String pathOnt = null;
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