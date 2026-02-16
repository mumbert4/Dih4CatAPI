package dih4cat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades de configuración de la aplicación.
 * Lee propiedades desde application.yml con el prefijo "app.paths".
 * 
 * Ejemplo en application.yml:
 * app:
 *   paths:
 *     config: "settings/paths.json"
 *     examples: "settings/examples"
 *     output: "settings/output"
 *     matrix: "settings/matrix.csv"
 * 
 * Puede sobrescribirse con variables de entorno:
 * - CONFIG_PATH=ruta/config.json java -jar app.jar
 * - EXAMPLES_PATH=ruta/ejemplos java -jar app.jar
 * - OUTPUT_PATH=ruta/salida java -jar app.jar
 * - MATRIX_PATH=ruta/matriz.csv java -jar app.jar
 */
@Component
@ConfigurationProperties(prefix = "app.paths")
public class ApplicationConfiguration {

    /**
     * Ruta al archivo de configuración (paths.json)
     * Variable de entorno: CONFIG_PATH
     * Por defecto: settings/paths.json
     */
    private String config = "settings/paths.json";

    /**
     * Ruta al directorio de ejemplos
     * Variable de entorno: EXAMPLES_PATH
     * Por defecto: settings/examples
     */
    private String examples = "settings/examples";

    /**
     * Ruta al directorio de salida
     * Variable de entorno: OUTPUT_PATH
     * Por defecto: settings/output
     */
    private String output = "settings/output";

    /**
     * Ruta al archivo de matriz de distancias (CSV)
     * Variable de entorno: MATRIX_PATH
     * Por defecto: settings/matrix.csv
     */
    private String matrix = "settings/matrix.csv";

    // Getters y Setters

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getExamples() {
        return examples;
    }

    public void setExamples(String examples) {
        this.examples = examples;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    @Override
    public String toString() {
        return "ApplicationConfiguration{" +
                "config='" + config + '\'' +
                ", examples='" + examples + '\'' +
                ", output='" + output + '\'' +
                ", matrix='" + matrix + '\'' +
                '}';
    }
}
