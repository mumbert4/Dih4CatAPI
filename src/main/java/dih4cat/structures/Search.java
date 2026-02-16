package dih4cat.structures;

import java.util.Set;

public class Search {

    public Set<String> tags;
    public String modality;
    Integer duration;
    Set<String> organizers;

    public Search(Set<String> tags, String modality, Integer duration, Set<String> organizers){
        this.tags=tags;
        this.modality=modality;
        this.duration=duration;
        this.organizers=organizers;
    }
}
