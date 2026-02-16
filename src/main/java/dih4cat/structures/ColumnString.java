package dih4cat.structures;
import java.util.HashSet;
import java.util.Set;

public class ColumnString extends Column<String>{
    String valueS;
    public ColumnString(String s){
        valueS = s;
    }

    public String getValue(){
        return valueS;
    }

    @Override
    public void setValue(String s) {
        valueS = s;
    }

    public double module() {
        return valueS.length() / 2.0;
    }

//    public double difference(Column column, List<Column> allValues) {
//
//        ColumnString col = (ColumnString) column;
//        String s1 = this.getValue();
//        String s2 = col.getValue();
//        return distanciaJaccard(s1,s2);
//    }

    /**
     * Mètode que calcula la similitud entre dos conjunts de caràcters, strings
     * @param s1 Set de caràcters finits que volem comparar
     * @param s2 Set de caràcters finits que volem comparar
     * @return Retorna la distància entre els dos strings. Si distància = 0 significa que els dos
     * strings són iguals. Altrament, significa que el dos strings són diferents i tenen alguna desemblança.
     */
    private double distanciaJaccard(String s1, String s2) { // 0 <= distJ(s1,s2) <= 1
        Set<String> unio = new HashSet<String>();
        Set<String> interseccio = new HashSet<String>();
        if (s1.length() == 0 && s2.length() == 0) return 0;
        if (s1.length() == 0 || s2.length() == 0) return 1;
        boolean posat = false;
        for (int i = 0; i < s1.length(); i++) {
            unio.add(String.valueOf(s1.charAt(i)));
            for (int j = 0; j < s2.length(); j++) {
                if (!posat) unio.add(String.valueOf(s2.charAt(j)));
                if (s1.charAt(i) == s2.charAt(j)) interseccio.add(String.valueOf(s1.charAt(i)));
            }
            posat = true;
        }
        //(#cardinalitat interseccio) /(cardinalitat unio dels sample sets) * 100
        double indexSimilitudJaccard = Math.round((double) interseccio.size() / (double) unio.size() * 100);
        var dist = 1 - (indexSimilitudJaccard / (double) 100);
        return dist;
    }
}
