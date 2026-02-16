package dih4cat.structures;


public class ColumnDouble extends Column<Double>{
    double valueD;

    public ColumnDouble(double d){
        valueD = d;
    }

    public Double getValue(){
        return valueD;
    }

    @Override
    public void setValue(Double aDouble) {
        valueD = aDouble;
    }

//    public double difference(Column column, List<Column> allValues) {
//        Pair<Double, Double> p = meanStd(allValues);
//        double mean = p.getFirst();
//        double standardDev = p.getSecond();
//        double module = Math.abs(((ColumnDouble) column).getValue() - this.getValue());
//        return Math.abs((module - mean)/standardDev);
//    }
}
