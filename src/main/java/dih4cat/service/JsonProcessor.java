package dih4cat.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dih4cat.algorithm.GibertDistance;
import dih4cat.domain.CtrlDomain;
import dih4cat.estructures.QueryConfig;
import dih4cat.graph.Graph;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.Gson;

@Service
public class JsonProcessor {

    public static String data = "";
    public static String onto = "";
    public static boolean getOnto,getData;
    private static CtrlDomain CDomain;

    public static void initialize(){
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
    }

    public Object procesarArchivo(String filename) throws Exception {
        String ruta = "examples/" + filename;
        System.out.println("Ruta: " + ruta);
        System.out.println(System.getProperty("user.dir"));

        URL url = getClass().getClassLoader().getResource("examples/1.json");
        System.out.println("URL del recurso: " + url);

        InputStream input = getClass().getClassLoader().getResourceAsStream("examples/1.json");
        System.out.println("¿InputStream nulo? " + (input == null));
        if (input == null) {
            System.out.println("No se encontro el archivo " + ruta);
            throw new IllegalArgumentException("Archivo no encontrado: " + ruta);
        }
        Gson gson = new Gson();
            try {
                String content = new Scanner(input, StandardCharsets.UTF_8).useDelimiter("\\A").next();
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
                        config.strongTags
                );

                // Mostrar información de salida
                System.out.println("Tags útiles: " + usefullTags);
                System.out.println("Tags no encontrados: " + unusedTags);
                GibertDistance.getInstance().saveRecommendationsAsJson("settings/output/"+input+".json");
                GibertDistance.getInstance().saveRecommendationsAsPDF("settings/output/"+input+".pdf");
                try (FileReader reader = new FileReader("settings/output/"+filename, StandardCharsets.UTF_8)) {
                    return gson.fromJson(reader, Object.class);
                }
            } catch (IOException e) {
                System.err.println("Error al leer el archivo: " + e.getMessage());
            }


        return null;
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