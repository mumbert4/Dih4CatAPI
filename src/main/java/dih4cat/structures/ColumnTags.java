package dih4cat.structures;

import java.util.HashSet;
import java.util.Set;

public class ColumnTags extends Column<Set<String>>{

   Set<String> tags;

    public ColumnTags(String s){//arribara una cadena de paraules separades per comes(,) aqui les separarem en paraules individuals i les afegirem a tags
        tags = new HashSet<>();
        String aux ="";
        for(int i = 0; i < s.length();++i){
            if((i == s.length() - 1 ||  s.charAt(i) == ',' ) && !aux.isEmpty()){
                if(s.charAt(i) != ',')  aux+=s.charAt(i);
                tags.add(aux.strip());
                aux="";
            }
            else aux+=s.charAt(i);
        }

    }
    @Override
    public Set<String> getValue() {
        return tags;
    }

    @Override
    public void setValue(Set<String> hashSet) {
        tags = hashSet;
    }
}
