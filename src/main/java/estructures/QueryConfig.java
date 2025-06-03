package estructures;


import java.util.Set;

public class QueryConfig {
    public Set<String> tags;
    public Set<String> organizers;
    public Integer minDuration;
    public Integer maxDuration;
    public Integer numCourses;
    public String modality;
    public String time;
    public String userStatus;
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
                ", format=" + format +
                ", organizer=" + organizer +
                ", duration=" + duration +
                ", status=" + status +
                ", strongTags=" + strongTags +
                '}';
    }
}