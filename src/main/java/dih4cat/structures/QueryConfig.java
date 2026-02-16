package dih4cat.structures;

import java.util.Set;

/**
 * DTO (Data Transfer Object) que contiene los parámetros de una consulta de recomendación.
 * Mapea directamente desde el JSON enviado por el cliente (REST API).
 */
public class QueryConfig {
    public Set<String> tags;
    public Set<String> organizers;
    public Integer minDuration;
    public Integer maxDuration;
    public Integer numCourses;
    public String modality;
    public String time;
    public String userStatus;
    public String fromTo;
    public String untilTo;
    public boolean format;
    public boolean organizer;
    public boolean duration;
    public boolean status;
    public boolean strongTags;

    @Override
    public String toString() {
        return "QueryConfig{" +
                "tags=" + tags +
                ", organizers=" + organizers +
                ", minDuration=" + minDuration +
                ", maxDuration=" + maxDuration +
                ", numCourses=" + numCourses +
                ", modality='" + modality + '\'' +
                ", time='" + time + '\'' +
                ", userStatus='" + userStatus + '\'' +
                ", fromTo='" + fromTo + '\'' +
                ", untilTo='" + untilTo + '\'' +
                ", format=" + format +
                ", organizer=" + organizer +
                ", duration=" + duration +
                ", status=" + status +
                ", strongTags=" + strongTags +
                '}';
    }
}
