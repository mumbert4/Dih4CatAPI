package dih4cat.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuración de rutas de la aplicación.
 * Deserializa desde settings/paths.json usando Jackson.
 * 
 * Ejemplo JSON:
 * {
 *   "pathData": "settings/data/formacions3.csv",
 *   "pathOnt": "settings/ontologies/Dih4Cat.ont"
 * }
 */
public class ApplicationPaths {

    @JsonProperty("pathData")
    private String dataPath;

    @JsonProperty("pathOnt")
    private String ontologyPath;

    // Constructor sin argumentos para Jackson
    public ApplicationPaths() {
    }

    /**
     * Constructor con todos los parámetros.
     * @param dataPath Ruta al archivo de datos
     * @param ontologyPath Ruta al archivo de ontología
     */
    public ApplicationPaths(String dataPath, String ontologyPath) {
        this.dataPath = dataPath;
        this.ontologyPath = ontologyPath;
    }

    // Getters y Setters
    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getOntologyPath() {
        return ontologyPath;
    }

    public void setOntologyPath(String ontologyPath) {
        this.ontologyPath = ontologyPath;
    }

    /**
     * Valida que ambas rutas estén configuradas.
     * @return true si ambas rutas no son nulas ni vacías
     */
    public boolean isValid() {
        return dataPath != null && !dataPath.isBlank() &&
               ontologyPath != null && !ontologyPath.isBlank();
    }

    @Override
    public String toString() {
        return "ApplicationPaths{" +
                "dataPath='" + dataPath + '\'' +
                ", ontologyPath='" + ontologyPath + '\'' +
                '}';
    }
}
