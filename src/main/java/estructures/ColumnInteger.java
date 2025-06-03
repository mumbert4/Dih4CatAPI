package estructures;


public class ColumnInteger extends Column<Integer>{
    int valueInt;
    public ColumnInteger(int i){
        valueInt = i;
    }

    public Integer getValue(){
        return valueInt;
    }

    @Override
    public void setValue(Integer integer) {
        valueInt=integer;
    }

//    public double difference(Column column, List<Column> allValues) {
//        Pair<Double, Double> p = meanStd(allValues);
//        double mean = p.getFirst();
//        double standardDev = p.getSecond();
//        double module = Math.abs(((ColumnInteger) column).getValue() - this.getValue());
//        return Math.abs((module - mean)/standardDev);
//    }
}
