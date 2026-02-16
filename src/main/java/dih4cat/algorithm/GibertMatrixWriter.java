package dih4cat.algorithm;

import dih4cat.domain.CtrlDomain;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

/**
 * Helper to write the distances matrix CSV extracted from GibertDistance.
 */
public class GibertMatrixWriter {

    public static void writeMatrix(HashMap<Integer, HashMap<Integer, Double>> distancesTags, CtrlDomain ctrlDomain, String matrixPath) {
        if (distancesTags == null || ctrlDomain == null) return;

        try (FileWriter writer = new FileWriter(matrixPath)) {
            writer.append("       ,");
            for (Integer col : distancesTags.keySet()) {
                writer.append(ctrlDomain.idToString(col)).append(",");
            }
            writer.append("\n");

            for (Integer row : distancesTags.keySet()) {
                writer.append(ctrlDomain.idToString(row)).append(",");

                for (Integer col : distancesTags.keySet()) {
                    Double distance = distancesTags.getOrDefault(row, new HashMap<>()).get(col);
                    if (distance != null) {
                        writer.append(String.format(Locale.US, "%.2f", distance)).append(",");
                    } else {
                        writer.append("-").append(",");
                    }
                }
                writer.append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Integer row : distancesTags.keySet()) {
            for (Integer col : distancesTags.keySet()) {
                Double distance = distancesTags.getOrDefault(row, new HashMap<>()).get(col);
                if (distance != null) {
                    System.out.print(distance + "\t");
                } else {
                    System.out.print("-\t");
                }
            }
            System.out.println();
        }
    }

}
