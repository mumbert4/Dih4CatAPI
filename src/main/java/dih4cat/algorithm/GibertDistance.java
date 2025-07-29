package dih4cat.algorithm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import dih4cat.domain.CtrlDomain;
import dih4cat.item.Item;
import dih4cat.item.ItemManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GibertDistance {


    private static GibertDistance singleton;

    HashMap<Integer,HashMap<Integer,Double>> distancesTags;
    Integer k;
    Integer comparator;
    Set<String> usefullTags;
    List<AbstractMap.SimpleEntry<Integer,LinkedList<Double>>> distanceCourses,distanceGood;
    List<AbstractMap.SimpleEntry<Integer,Double>> distanceCourses2;
    CtrlDomain d;
    HashSet<String> noTrobats;
    Set<String> allTags;
    HashMap<Integer,Double> distance;
    double percentilTags = 0.91;
    double percentilHours=82;
    HashMap<String,Integer> mapOrg;
    public Integer numCourses;
    Boolean avg = false;
    public static GibertDistance getInstance(){
        if(singleton==null) singleton = new GibertDistance();
        return singleton;
    }

    private GibertDistance(){

        distancesTags = new HashMap<>();
        distance = new HashMap<>();
        d= CtrlDomain.getInstance();
        k = 5;
        noTrobats = new HashSet<>();
    }


    public void completeMatrix(Set<Integer> s){//Feim la matriu de distancies de tags
        distancesTags = new HashMap<>();
        for(Integer i : s ){
            distancesTags.put(i,new HashMap<>());
            for(Integer j: s){
                if(i == j){ //son el mateix node-> distancia = 0
                    distancesTags.get(i).put(j,0.0);
                }
                else{
                    double d;
                    if(distancesTags.containsKey(j) && distancesTags.get(j).containsKey(i)){
                        d = distancesTags.get(j).get(i);
                    }
                    else{
                        d = getDistance(i,j);
                    }
                    distancesTags.get(i).put(j,d);
                }
            }
        }

        //csvMatrix(); //guarda la matriu de distancies en un csv

    }

    public double getDistance(Integer i, Integer j){//distancia entre 2 tags

        Set<String> a = d.getAncestors(i);
        Set<String> b = d.getAncestors(j);
        a.add(d.idToString(i));
        b.add(d.idToString(j));
        Set<String> union = new HashSet<>(a);
        Set<String> intersection = new HashSet<>(a);

        union.addAll(b);
        intersection.retainAll(b);



        double nU = union.size();
        double nI = intersection.size();
        double aux = (nU - nI)/ nU;



        return Math.sqrt(aux);


    }

    public void csvMatrix(){//guardam matriu distancies a un csv
        try (FileWriter writer = new FileWriter("/home/miquel/Documentos/Dih4Cat/Settings/matrix.csv")) {
            // Escribimos el encabezado de la matriz con los nombres en la primera fila
            writer.append("       ,");
            for (Integer col : distancesTags.keySet()) {
                writer.append(d.idToString(col)).append(",");
            }
            writer.append("\n");

            // Escribimos cada fila con los nombres y las distancias
            for (Integer row : distancesTags.keySet()) {
                // Primera columna: nombre de la fila
                writer.append(d.idToString(row)).append(",");

                for (Integer col : distancesTags.keySet()) {
                    // Obtenemos la distancia entre la fila y la columna, o null si no existe
                    Double distance = distancesTags.getOrDefault(row, new HashMap<>()).get(col);

                    // Escribimos el valor de la distancia o "-" si no existe
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




        // Imprimimos cada fila con los nombres y las distancias
        for (Integer row : distancesTags.keySet()) {
            // Primera columna: nombre de la fila

            for (Integer col : distancesTags.keySet()) {
                // Obtenemos la distancia entre la fila y la columna, o null si no existe
                Double distance = distancesTags.getOrDefault(row, new HashMap<>()).get(col);

                // Mostramos el valor de la distancia o "-" si no existe
                if (distance != null) {
                    System.out.print(distance + "\t");
                } else {
                    System.out.print("-\t");
                }
            }
            System.out.println();
        }
    }
    public void courseDistances(Set<String> selectedTags, Set<String> usefullTags, Set<String> unusedTags, String modality, String userStatus , Integer minDuration, Integer maxDuration, Set<String> organizers, boolean format, boolean duration, boolean organizer, boolean status, boolean strongTags){
        mapOrg = d.getMap();
        percentilHours = d.getPercentilHours();
        System.out.println("Gib percentil hours: "+percentilHours);
        System.out.println("GIBERT DISTANCE");
        System.out.println(mapOrg);
        //primer pillam els k cursos que mes s'assemblin per tags, despres li afegim les distancies de lo altre
        allTags = d.getTags(); //tots els tags
        this.usefullTags = new HashSet<>();
        for(String s : selectedTags){
            if(!allTags.contains(s.strip())){
                unusedTags.add(s.strip());
            }
            else {
                this.usefullTags.add(s.strip());
                usefullTags.add(s.strip());
            }
        }


        distanceCourses = new ArrayList<>();
        distanceCourses2 = new ArrayList<>();
        comparator = 0;//iniciam a avg
        Set<Integer> courses = d.getIDs();
        System.out.println("Status cerca: " + userStatus);
        System.out.println("Antes de filtrat: " + courses.size());
        courses = filtrat(courses, format, duration, organizer, status, modality, organizers, minDuration, maxDuration, userStatus, strongTags, selectedTags);
        System.out.println("Despres de filtrat: " + courses.size());
        numCourses = courses.size();
        for(Integer i : courses){// iteram els cursos
            LinkedList<Double> l = new LinkedList<Double>();
            Double distanceTags = getTagsDistance(usefullTags,d.getTagsCourse(i));
            Double distanceHours = getHourDistance(minDuration, maxDuration, (int)d.getDuration(i));
            Double distanceLocation = getLocationDistance(modality, d.getModality(i));
            Double distanceOrganizers = getOrganizersDistance(organizers, d.getOrganizer(i));
            Double distanceStatus = getStatusDistance(userStatus, d.getStatus(i));
            l.push(distanceLocation);
            l.push(distanceOrganizers);
            l.push(distanceHours);
            l.push(distanceTags);

            double a = distanceTags/percentilTags;
            double b = distanceHours/percentilHours;
            double c = (distanceStatus + distanceLocation + distanceOrganizers)/18;


            distanceCourses.add(new AbstractMap.SimpleEntry<Integer,LinkedList<Double>>(i,l));
            distanceCourses2.add(new AbstractMap.SimpleEntry<>(i,a+b+c));
            ItemManager.getInstance().getItem(i).setDistance(distanceTags);
            distance.put(i,a+b+c);

        }
        this.sortList(distanceCourses);
        this.sortList2(distanceCourses2);
        System.out.println(distanceCourses2);
        //System.out.println(distanceCourses2);
        System.out.println("Tags no trobats: " + noTrobats);

    }

    public Set<Integer> filtrat(Set<Integer> courses, boolean format, boolean duration, boolean organizer, boolean status, String modality, Set<String> organizers, Integer minDuration, Integer maxDuration, String userStatus, boolean strongTags, Set<String> selectedTags){
        Set<Integer> s = new HashSet<>();
        for (Integer i : courses){
            boolean valid = true;
            if(format){
                System.out.println(modality + " " + d.getModality(i));
                if(!modality.equals("All")  && !d.getModality(i).equals(modality))valid = false;
                System.out.println(valid);
            }
            if(valid && duration){
                //System.out.println(minDuration + " " + d.getDuration(i) + " " + maxDuration);
                if(d.getDuration(i)>maxDuration || d.getDuration(i)<minDuration) valid = false;
                //  System.out.println(valid);
            }
            if(valid && organizer){
                if(!organizers.contains(d.getOrganizer(i))) valid = false;
            }
            if(valid && status){
                System.out.println(userStatus + " " + d.getStatus(i));
                if(!userStatus.equals("All")  && !d.getStatus(i).equals(userStatus)) valid = false;
                System.out.println(valid);
            }
            if(valid && strongTags){
                Set<String> courseTags = d.getTagsCourse(i);
                boolean hasMatch = false;
                for (String tag : selectedTags) {
                    if (courseTags.contains(tag)) {
                        hasMatch = true;
                        break; // No es necesario seguir buscando, ya sabemos que hay coincidencia
                    }
                }

                if (!hasMatch) valid = false;
            }
            if(valid) s.add(i);
        }
        return s;
    }

    public void calculateDistance(Integer n){
        System.out.println("Calculam noves distancies");
        Set<Integer> s = distancesTags.keySet();
        distancesTags.put(n, new HashMap<>());
        distancesTags.get(n).put(n,0.0);
        for(Integer i: s){
            double d = getDistance(i,n);

            distancesTags.get(n).put(i,d);
            distancesTags.get(i).put(n,d);
        }

        allTags = d.getTags();
    }



    public double getLocationDistance(String userM, String courseM){

        if(userM.equals(courseM)|| userM.equals("All")){
            return 0.0;
        }
        double userL = d.getNumLocations(userM);
        double courseL = d.getNumLocations(courseM);
        double d = 1/userL + 1/courseL;

        return d;
    }

    public double getOrganizersDistance(Set<String> organizers, String course){
        double n =  organizers.size();
        double perUs = 1.0/n; // 1/organitzadors triats
        double s = 0;
        for(String o : organizers){
            if(o.equals(course)){
                s += (Math.pow(1 - perUs,2.0))/mapOrg.get(o);
            }
            else s+= Math.pow(perUs,2.0)/mapOrg.get(o);
        }
        //System.out.println("Distancia organitzadors: " + s);
        return s;
    }
    public Double getTagsDistance(Set<String> userTags, Set<String> courseTags){ //distancia entre tags del usuari i del curs
        Double avg = 0.0;

        if(this.avg){
            for(String c : courseTags){ // iteram els tags d'un curs
                for (String u: userTags){//iteram els tags de l'usuari
                    Double dist = distancesTags.get(d.stringToId(c)).get(d.stringToId(u));//distacnia entre els tags
                    avg += dist;//afegim la distancia

                }

            }
            return avg/(double)(userTags.size()*courseTags.size());
        }
        System.out.println("Metode min");
        //return min;
        for(String c: userTags){
            Double min = 999999999.0;
            System.out.println("Tags que comparam:" + c + " amb " + courseTags);
            for(String u: courseTags){
                Double dist = distancesTags.get(d.stringToId(c)).get(d.stringToId(u));//distacnia entre els tags
                if(dist<min){
                    min = dist;
                }
            }
            System.out.println("Distancia minima: " + min);
            avg += min;
        }
        return avg/(double)(userTags.size());



    }

    public double getStatusDistance(String userS, String courseS){

        if(userS.equals(courseS)|| userS.equals("All") ){
            return 0;
        }
        double userL = d.getNumStatus(userS);
        double courseL = d.getNumStatus(courseS);


        return 1/userL + 1/courseL;

    }

    public Double getHourDistance(int minDuration, int maxDuration, int courseDuration){//retorna la distancia entre
        if(courseDuration >= minDuration && courseDuration <= maxDuration) return 0.0;
        else if(courseDuration > maxDuration) return(double) courseDuration-maxDuration;
        else return (double) minDuration - courseDuration;
    }

    public LinkedList<Integer> getRecomended(){ // retornam els K IDs dels cursos que mes se pareixen
        LinkedList<Integer> s = new LinkedList<>();

        for(int i = 0; i < k; ++i){
            Integer id = distanceCourses2.get(i).getKey();

            ItemManager.getInstance().getItem(id).setDistance(Math.round(distanceCourses2.get(i).getValue() * 1000.0) / 1000.0);
            s.add(id);
            if(i==distanceCourses2.size()-1) break;
        }

        return s;
    }

    public void saveRecommendationsAsJson(String outputPath) {
        LinkedList<Integer> recommended = getRecomended();
        List<Map<String, Object>> outputList = new LinkedList<>();

        for (Integer id : recommended) {
            Item item = ItemManager.getInstance().getItem(id);
            Map<String, Object> entry = new LinkedHashMap<>();

            try {
                entry.put("Activity.name", item.attributes.get("Activity.name").getValue());
                entry.put("tags", item.getTags());
                entry.put("Start.Date", item.attributes.get("Start.Date").getValue());
                entry.put("End..Date", item.attributes.get("End..Date").getValue());
                entry.put("status", item.status);
                entry.put("location", item.modality);
                entry.put("duration_hours", item.duration);
                entry.put("Organizer.Node", item.attributes.get("Organizer.Node").getValue());
                entry.put("Organizer..entity.", item.attributes.get("Organizer..entity.").getValue());
                entry.put("Programa...enllaçar.document", item.attributes.get("Programa...enllaçar.document").getValue());
                entry.put("Distance", Math.round(item.distance * 1000.0) / 1000.0);

                outputList.add(entry);
            } catch (Exception e) {
                System.err.println("Error accediendo a atributos del dih4cat.item " + id + ": " + e.getMessage());
            }
        }

        // Crear carpeta si no existe
        File outputFile = new File(outputPath);
        outputFile.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(outputFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(outputList, writer);
            System.out.println("Recomendaciones guardadas en: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo JSON: " + e.getMessage());
        }
    }

    public void saveRecommendationsAsPDF(String outputPath) {
        LinkedList<Integer> recommended = getRecomended();

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
                Item item = ItemManager.getInstance().getItem(id);

                Table table = new Table(2);
                table.setWidth(100);
                table.setPadding(3);

                table.addCell(new Cell(new Phrase("Activity.name", headerFont)));
                table.addCell(new Cell(new Phrase(String.valueOf(item.attributes.get("Activity.name").getValue()), textFont)));

                table.addCell("Tags");
                table.addCell(item.getTags().toString());

                table.addCell("Start.Date");
                table.addCell(String.valueOf(item.attributes.get("Start.Date").getValue()));


                table.addCell("End..Date");
                table.addCell(String.valueOf(item.attributes.get("End..Date").getValue()));

                table.addCell("Status");
                table.addCell(item.status);

                table.addCell("Location");
                table.addCell(item.modality);

                table.addCell("Duration (hours)");
                table.addCell(String.valueOf(item.duration));

                table.addCell("Organizer.Node");
                table.addCell(String.valueOf(item.attributes.get("Organizer.Node").getValue()));

                table.addCell("Organizer..entity.");
                table.addCell(String.valueOf(item.attributes.get("Organizer..entity.").getValue()));

                table.addCell("Programa...enllaçar.document");
                table.addCell(String.valueOf(item.attributes.get("Programa...enllaçar.document").getValue()));

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

    public LinkedList<Double> trueDistance(LinkedList<Double> l){
        LinkedList<Double> s = new LinkedList<>();
        double aux = 0;
        for(Double d: l){
            aux += d;
        }
        s.add(aux);
        return s;
    }

    public void sortList(List<AbstractMap.SimpleEntry<Integer,LinkedList<Double>>> aux){//ordenam la llista de cursos segons la distancia
        Collections.sort(aux, new Comparator<AbstractMap.SimpleEntry<Integer, LinkedList<Double>>>() {
            @Override
            public int compare(AbstractMap.SimpleEntry<Integer, LinkedList<Double>> o1, AbstractMap.SimpleEntry<Integer, LinkedList<Double>> o2) {
                Double d1 = o1.getValue().get(comparator);

                Double d2 = o2.getValue().get(comparator);
                return d1.compareTo(d2);
            }
        });
    }

    public void sortList2(List<AbstractMap.SimpleEntry<Integer,Double>> aux){//ordenam la llista de cursos segons la distancia
        Collections.sort(aux, new Comparator<AbstractMap.SimpleEntry<Integer, Double>>() {
            @Override
            public int compare(AbstractMap.SimpleEntry<Integer, Double> o1, AbstractMap.SimpleEntry<Integer, Double> o2) {
                Double d1 = o1.getValue();

                Double d2 = o2.getValue();
                return d1.compareTo(d2);
            }
        });
    }



    public void setNCourses(Integer n){// canviam els numero de cursos que volem retornar
        k=n;
    }

    public void setMethod(Boolean b){
        avg = b;
    }

}
