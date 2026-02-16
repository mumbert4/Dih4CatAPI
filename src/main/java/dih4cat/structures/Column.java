package dih4cat.structures;


public abstract class Column<T> {
    //public abstract double difference(Column column,List<Column> allValues);

    public abstract T getValue();

    public abstract void setValue(T t);
//    public static Pair<Double, Double> meanStd(List<Column> allValues) {
//
//        double mean = 0;
//
//        for (Column a : allValues) {
//            if (a instanceof ColumnDouble) {
//                mean += ((ColumnDouble) a).valueD;
//            } else if (a instanceof ColumnInteger) {
//                mean += ((ColumnInteger) a).valueInt;
//            } else if (a instanceof  ColumnBool){
//                if (((ColumnBool) a).valueB) ++mean;
//            }
//        }
//
//        mean = mean / allValues.size();
//
//        double sum = 0;
//        double actV;
//
//        for (Column a : allValues) {
//            if (a instanceof ColumnDouble) {
//                actV = ((ColumnDouble) a).valueD;
//                sum += (actV - mean) * (actV - mean);
//            } else if (a instanceof ColumnInteger) {
//                actV = ((ColumnInteger) a).valueInt;
//                sum += (actV - mean) * (actV - mean);
//            } else if (a instanceof  ColumnBool){
//                if (((ColumnBool) a).valueB) actV = 1;
//                else actV = 0;
//                sum += (actV - mean) * (actV - mean);
//            }
//        }
//
//        double standardDev = Math.sqrt(sum / allValues.size());
//        return new Pair<>(mean, standardDev);
//    }
}
