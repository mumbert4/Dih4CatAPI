package dih4cat.algorithm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import dih4cat.item.Item;
import dih4cat.item.ItemManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Helper class responsible for writing recommendation outputs (JSON/PDF).
 */
public class GibertOutput {

    public static void saveRecommendationsAsJson(ItemManager itemManager, LinkedList<Integer> recommended, String outputPath) {
        java.util.List<Map<String, Object>> outputList = new java.util.LinkedList<>();

        for (Integer id : recommended) {
            Item item = itemManager.getItem(id);
            Map<String, Object> entry = new LinkedHashMap<>();

            try {
                entry.put("Activity.name", item.attributes.get("Activity.name").getValue());
                entry.put("tags", item.getTags());
                entry.put("Start.Date", item.attributes.get("Start.Date").getValue());
                entry.put("End.Date", item.attributes.get("End.Date").getValue());
                entry.put("status", item.status);
                entry.put("location", item.modality);
                entry.put("duration_hours", item.duration);
                entry.put("Organizer.Node", item.attributes.get("Organizer.Node").getValue());
                entry.put("Organizer.entity", item.attributes.get("Organizer.entity").getValue());
                entry.put("Programa.enllaçar.document",
                        item.attributes.get("Programa.enllaçar.document").getValue());
                entry.put("Distance", Math.round(item.distance * 1000.0) / 1000.0);

                outputList.add(entry);
            } catch (Exception e) {
                System.err.println("Error accediendo a atributos del dih4cat.item " + id + ": " + e.getMessage());
            }
        }

        // Crear carpeta si no existe
        File outputFile = new File(outputPath);
        if (outputFile.getParentFile() != null) outputFile.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(outputFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(outputList, writer);
            System.out.println("Recomendaciones guardadas en: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo JSON: " + e.getMessage());
        }
    }

    public static void saveRecommendationsAsPDF(ItemManager itemManager, LinkedList<Integer> recommended, String outputPath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

            document.add(new Paragraph("Cursos Recomendados", titleFont));
            document.add(Chunk.NEWLINE);

            for (Integer id : recommended) {
                Item item = itemManager.getItem(id);

                Table table = new Table(2);
                table.setWidth(100);
                table.setPadding(3);

                table.addCell(new Cell(new Phrase("Activity.name", headerFont)));
                table.addCell(new Cell(
                        new Phrase(String.valueOf(item.attributes.get("Activity.name").getValue()), textFont)));

                table.addCell("Tags");
                table.addCell(item.getTags().toString());

                table.addCell("Start.Date");
                table.addCell(String.valueOf(item.attributes.get("Start.Date").getValue()));

                table.addCell("End.Date");
                table.addCell(String.valueOf(item.attributes.get("End.Date").getValue()));

                table.addCell("Status");
                table.addCell(item.status);

                table.addCell("Location");
                table.addCell(item.modality);

                table.addCell("Duration (hours)");
                table.addCell(String.valueOf(item.duration));

                table.addCell("Organizer.Node");
                table.addCell(String.valueOf(item.attributes.get("Organizer.Node").getValue()));

                table.addCell("Organizer.entity");
                table.addCell(String.valueOf(item.attributes.get("Organizer.entity").getValue()));

                table.addCell("Programa.enllaçar.document");
                table.addCell(String.valueOf(item.attributes.get("Programa.enllaçar.document").getValue()));

                table.addCell("Distance");
                table.addCell(String.valueOf(Math.round(item.distance * 1000.0) / 1000.0));

                document.add(table);
                document.add(Chunk.NEWLINE);
            }

            document.close();
            System.out.println("PDF guardado en: " + outputPath);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al crear PDF: " + e.getMessage());
        }
    }
}
