package estructures;


public class ColumnBool extends Column<Boolean>{
    boolean valueB;
    public ColumnBool(boolean b){
        valueB = b;
    }
    public Boolean getValue(){
        return valueB;
    }

    @Override
    public void setValue(Boolean aBoolean) {
        valueB = aBoolean;
    }


    //public double difference(Column column, List<Column> allValues) {
//        Pair<Double, Double> p = meanStd(allValues);
//        double mean = p.getFirst();
//        double standardDev = p.getSecond();
//        double module;
//        if (((ColumnBool) column).getValue() == this.getValue()) module = 0;
//        else module = 1;
//        return Math.abs((module - mean)/standardDev);
//    }



}
