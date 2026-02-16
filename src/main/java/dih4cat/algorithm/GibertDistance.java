package dih4cat.algorithm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import dih4cat.domain.CtrlDomain;
import dih4cat.item.Item;
import dih4cat.item.ItemManager;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Servicio que implementa el algoritmo de distancia de Gibert para
 * calcular similitud entre cursos y preferencias del usuario.
 */
@Service
public class GibertDistance {

    HashMap<Integer, HashMap<Integer, Double>> distancesTags;
    Integer k;
    Integer comparator;
    Set<String> usefullTags;
    List<AbstractMap.SimpleEntry<Integer, LinkedList<Double>>> distanceCourses, distanceGood;
    List<AbstractMap.SimpleEntry<Integer, Double>> distanceCourses2;
    private CtrlDomain ctrlDomain;
    private final ItemManager itemManager;
    private final dih4cat.config.ApplicationConfiguration appConfig;
    HashSet<String> noTrobats;
    Set<String> allTags;
    HashMap<Integer, Double> distance;
    double percentilTags = 0.91;
    double percentilHours = 82;
    HashMap<String, Integer> mapOrg;
    public Integer numCourses;
    Boolean avg = false;

    /**
     * Constructor público para inyección de dependencias.
     * La inyección de CtrlDomain se realiza a través de setter para evitar
     * dependencias circulares.
     * @param itemManager Gestor de items inyectado
     */
    public GibertDistance(ItemManager itemManager, dih4cat.config.ApplicationConfiguration appConfig) {
        this.itemManager = itemManager;
        this.appConfig = appConfig;
        distancesTags = new HashMap<>();
        distance = new HashMap<>();
        k = 5;
        noTrobats = new HashSet<>();
    }

    /**
     * Establece la instancia de CtrlDomain (inyección de setter para evitar ciclos).
     * @param ctrlDomain Instancia del servicio CtrlDomain
     */
    public void setCtrlDomain(CtrlDomain ctrlDomain) {
        this.ctrlDomain = ctrlDomain;
    }

    public void completeMatrix(Set<Integer> s) {// Feim la matriu de distancies de tags
        distancesTags = new HashMap<>();
        for (Integer i : s) {
            distancesTags.put(i, new HashMap<>());
            for (Integer j : s) {
                if (i == j) { // son el mateix node-> distancia = 0
                    distancesTags.get(i).put(j, 0.0);
                } else {
                    double d;
                    if (distancesTags.containsKey(j) && distancesTags.get(j).containsKey(i)) {
                        d = distancesTags.get(j).get(i);
                    } else {
                        d = getDistance(i, j);
                    }
                    distancesTags.get(i).put(j, d);
                }
            }
        }

        // csvMatrix(); //guarda la matriu de distancies en un csv

    }

    public double getDistance(Integer i, Integer j) {// distancia entre 2 tags

        Set<String> a = ctrlDomain.getAncestors(i);
        Set<String> b = ctrlDomain.getAncestors(j);
        a.add(ctrlDomain.idToString(i));
        b.add(ctrlDomain.idToString(j));
        Set<String> union = new HashSet<>(a);
        Set<String> intersection = new HashSet<>(a);

        union.addAll(b);
        intersection.retainAll(b);

        double nU = union.size();
        double nI = intersection.size();
        double aux = (nU - nI) / nU;

        return Math.sqrt(aux);

    }

    public void csvMatrix() {// guardam matriu distancies a un csv
        String matrixPath = (appConfig != null && appConfig.getMatrix() != null) ? appConfig.getMatrix() : "settings/matrix.csv";
        GibertMatrixWriter.writeMatrix(distancesTags, ctrlDomain, matrixPath);
    }

    public void courseDistances(Set<String> selectedTags, Set<String> usefullTags, Set<String> unusedTags,
            String modality, String userStatus, Integer minDuration, Integer maxDuration, Set<String> organizers,
            boolean format, boolean duration, boolean organizer, boolean status, boolean strongTags,
            Integer numCoursesWanted, String fromTo, String untilTo) {
        mapOrg = ctrlDomain.getMap();
        percentilHours = ctrlDomain.getPercentilHours();
        System.out.println("Gib percentil hours: " + percentilHours);
        System.out.println("GIBERT DISTANCE");
        System.out.println(mapOrg);
        k = numCoursesWanted;
        // primer pillam els k cursos que mes s'assemblin per tags, despres li afegim
        // les distancies de lo altre
        allTags = ctrlDomain.getTags(); // tots els tags
        this.usefullTags = new HashSet<>();
        for (String s : selectedTags) {
            if (!allTags.contains(s.strip())) {
                unusedTags.add(s.strip());
            } else {
                this.usefullTags.add(s.strip());
                usefullTags.add(s.strip());
            }
        }

        distanceCourses = new ArrayList<>();
        distanceCourses2 = new ArrayList<>();
        comparator = 0;// iniciam a avg
        Set<Integer> courses = ctrlDomain.getIDs();
        courses = filtrat(courses, format, duration, organizer, status, modality, organizers, minDuration, maxDuration,
                userStatus, strongTags, selectedTags, fromTo, untilTo);
        numCourses = courses.size();
        for (Integer i : courses) {// iteram els cursos
            LinkedList<Double> l = new LinkedList<Double>();
            Double distanceTags = getTagsDistance(usefullTags, ctrlDomain.getTagsCourse(i));
            Double distanceHours = getHourDistance(minDuration, maxDuration, (int) ctrlDomain.getDuration(i));
            Double distanceLocation = getLocationDistance(modality, ctrlDomain.getModality(i));
            Double distanceOrganizers = getOrganizersDistance(organizers, ctrlDomain.getOrganizer(i));
            Double distanceStatus = getStatusDistance(userStatus, ctrlDomain.getStatus(i));
            l.push(distanceLocation);
            l.push(distanceOrganizers);
            l.push(distanceHours);
            l.push(distanceTags);

            double a = distanceTags / percentilTags;
            double b = distanceHours / percentilHours;
            double c = (distanceStatus + distanceLocation + distanceOrganizers) / 18;

            distanceCourses.add(new AbstractMap.SimpleEntry<Integer, LinkedList<Double>>(i, l));
            distanceCourses2.add(new AbstractMap.SimpleEntry<>(i, a + b + c));
            itemManager.getItem(i).setDistance(distanceTags);
            distance.put(i, a + b + c);

        }
        this.sortList(distanceCourses);
        this.sortList2(distanceCourses2);
        System.out.println(distanceCourses2);
        // System.out.println(distanceCourses2);
        System.out.println("Tags no trobats: " + noTrobats);

    }

    public Set<Integer> filtrat(Set<Integer> courses, boolean format, boolean duration, boolean organizer,
            boolean status, String modality, Set<String> organizers, Integer minDuration, Integer maxDuration,
            String userStatus, boolean strongTags, Set<String> selectedTags, String fromTo, String untilTo) {
        Set<Integer> s = new HashSet<>();
        for (Integer i : courses) {
            boolean valid = true;
            if (format) {
                System.out.println(modality + " " + ctrlDomain.getModality(i));
                if (!modality.equals("All") && !ctrlDomain.getModality(i).equals(modality))
                    valid = false;
                System.out.println(valid);
            }
            if (valid && duration) {
                // System.out.println(minDuration + " " + d.getDuration(i) + " " + maxDuration);
                if (ctrlDomain.getDuration(i) > maxDuration || ctrlDomain.getDuration(i) < minDuration)
                    valid = false;
                // System.out.println(valid);
            }
            if (valid && organizer) {
                if (!organizers.contains(ctrlDomain.getOrganizer(i)))
                    valid = false;
            }
            if (valid && status) {
                System.out.println(userStatus + " " + ctrlDomain.getStatus(i));
                if (!userStatus.equals("All") && !ctrlDomain.getStatus(i).equals(userStatus))
                    valid = false;
                System.out.println(valid);
            }
            if (valid && strongTags) {
                Set<String> courseTags = ctrlDomain.getTagsCourse(i);
                boolean hasMatch = false;
                for (String tag : selectedTags) {
                    if (courseTags.contains(tag)) {
                        hasMatch = true;
                        break; // No es necesario seguir buscando, ya sabemos que hay coincidencia
                    }
                }

                if (!hasMatch)
                    valid = false;
            }

            if (valid && (fromTo != null || untilTo != null)) {
                try {
                    // Obtener fechas del curso
                    String startDateStr = (String) itemManager.getItem(i).attributes.get("Start.Date")
                            .getValue();
                    String endDateStr = (String) itemManager.getItem(i).attributes.get("End.Date")
                            .getValue();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate courseStart = LocalDate.parse(startDateStr, formatter);
                    LocalDate courseEnd = LocalDate.parse(endDateStr, formatter);

                    if (fromTo != null && !fromTo.isEmpty()) {
                        LocalDate filterFrom = LocalDate.parse(fromTo, formatter);
                        // El curso debe empezar después o el mismo día que 'fromTo'
                        // O, ¿el rango del curso debe solaparse? Asumiré: curso empieza >= fromTo
                        if (courseStart.isBefore(filterFrom)) {
                            valid = false;
                        }
                    }

                    if (valid && untilTo != null && !untilTo.isEmpty()) {
                        LocalDate filterUntil = LocalDate.parse(untilTo, formatter);
                        // El curso debe terminar antes o el mismo día que 'untilTo'
                        if (courseEnd.isAfter(filterUntil)) {
                            valid = false;
                        }
                    }
                } catch (Exception e) {
                    // Si hay error parseando fechas (formato incorrecto o vacío), lo ignoramos o
                    // invalidamos.
                    // Para ser seguros, si no se puede parsear la fecha del curso, quizás mejor no
                    // mostrarlo si hay filtro.
                    // System.err.println("Error parsing dates for course " + i + ": " +
                    // e.getMessage());
                    // valid = false; // Descomentar si se quiere ser estricto
                }
            }
            if (valid)
                s.add(i);
        }
        return s;
    }

    public void calculateDistance(Integer n) {
        System.out.println("Calculam noves distancies");
        Set<Integer> s = distancesTags.keySet();
        distancesTags.put(n, new HashMap<>());
        distancesTags.get(n).put(n, 0.0);
        for (Integer i : s) {
            double distance = getDistance(i, n);

            distancesTags.get(n).put(i, distance);
            distancesTags.get(i).put(n, distance);
        }

        allTags = ctrlDomain.getTags();
    }

    public double getLocationDistance(String userM, String courseM) {

        if (userM.equals(courseM) || userM.equals("All")) {
            return 0.0;
        }
        double userL = ctrlDomain.getNumLocations(userM);
        double courseL = ctrlDomain.getNumLocations(courseM);
        double distance = 1 / userL + 1 / courseL;

        return distance;
    }

    public double getOrganizersDistance(Set<String> organizers, String course) {
        double n = organizers.size();
        double perUs = 1.0 / n; // 1/organitzadors triats
        double s = 0;
        for (String o : organizers) {
            Integer count = mapOrg.get(o);
            double denominator = (count != null) ? count : 1.0; // Avoid NPE and potential division by zero
            if (o.equals(course)) {
                s += (Math.pow(1 - perUs, 2.0)) / denominator;
            } else
                s += Math.pow(perUs, 2.0) / denominator;
        }
        // System.out.println("Distancia organitzadors: " + s);
        return s;
    }

    public Double getTagsDistance(Set<String> userTags, Set<String> courseTags) { // distancia entre tags del usuari i
                                                                                  // del curs
        Double avg = 0.0;

        if (this.avg) {
            for (String c : courseTags) { // iteram els tags d'un curs
                for (String u : userTags) {// iteram els tags de l'usuari
                    Double dist = distancesTags.get(ctrlDomain.stringToId(c)).get(ctrlDomain.stringToId(u));// distacnia entre els tags
                    avg += dist;// afegim la distancia

                }

            }
            return avg / (double) (userTags.size() * courseTags.size());
        }
        // return min;
        for (String c : userTags) {
            Double min = 999999999.0;
            for (String u : courseTags) {
                Double dist = distancesTags.get(ctrlDomain.stringToId(c)).get(ctrlDomain.stringToId(u));// distacnia entre els tags
                if (dist < min) {
                    min = dist;
                }
            }
            avg += min;
        }
        return avg / (double) (userTags.size());

    }

    public double getStatusDistance(String userS, String courseS) {

        if (userS.equals(courseS) || userS.equals("All")) {
            return 0;
        }
        double userL = ctrlDomain.getNumStatus(userS);
        double courseL = ctrlDomain.getNumStatus(courseS);

        return 1 / userL + 1 / courseL;

    }

    public Double getHourDistance(int minDuration, int maxDuration, int courseDuration) {// retorna la distancia entre
        if (courseDuration >= minDuration && courseDuration <= maxDuration)
            return 0.0;
        else if (courseDuration > maxDuration)
            return (double) courseDuration - maxDuration;
        else
            return (double) minDuration - courseDuration;
    }

    public LinkedList<Integer> getRecomended() { // retornam els K IDs dels cursos que mes se pareixen
        LinkedList<Integer> s = new LinkedList<>();

        for (int i = 0; i < k; ++i) {
            Integer id = distanceCourses2.get(i).getKey();

            itemManager.getItem(id)
                    .setDistance(Math.round(distanceCourses2.get(i).getValue() * 1000.0) / 1000.0);
            s.add(id);
            if (i == distanceCourses2.size() - 1)
                break;
        }

        return s;
    }

    public void saveRecommendationsAsJson(String outputPath) {
        LinkedList<Integer> recommended = getRecomended();
        GibertOutput.saveRecommendationsAsJson(itemManager, recommended, outputPath);
    }

    public void saveRecommendationsAsPDF(String outputPath) {
        LinkedList<Integer> recommended = getRecomended();
        GibertOutput.saveRecommendationsAsPDF(itemManager, recommended, outputPath);
    }

    public LinkedList<Double> trueDistance(LinkedList<Double> l) {
        LinkedList<Double> s = new LinkedList<>();
        double aux = 0;
        for (Double d : l) {
            aux += d;
        }
        s.add(aux);
        return s;
    }

    public void sortList(List<AbstractMap.SimpleEntry<Integer, LinkedList<Double>>> aux) {// ordenam la llista de cursos
                                                                                          // segons la distancia
        Collections.sort(aux, new Comparator<AbstractMap.SimpleEntry<Integer, LinkedList<Double>>>() {
            @Override
            public int compare(AbstractMap.SimpleEntry<Integer, LinkedList<Double>> o1,
                    AbstractMap.SimpleEntry<Integer, LinkedList<Double>> o2) {
                Double d1 = o1.getValue().get(comparator);

                Double d2 = o2.getValue().get(comparator);
                return d1.compareTo(d2);
            }
        });
    }

    public void sortList2(List<AbstractMap.SimpleEntry<Integer, Double>> aux) {// ordenam la llista de cursos segons la
                                                                               // distancia
        Collections.sort(aux, new Comparator<AbstractMap.SimpleEntry<Integer, Double>>() {
            @Override
            public int compare(AbstractMap.SimpleEntry<Integer, Double> o1,
                    AbstractMap.SimpleEntry<Integer, Double> o2) {
                Double d1 = o1.getValue();

                Double d2 = o2.getValue();
                return d1.compareTo(d2);
            }
        });
    }

    public void setNCourses(Integer n) {// canviam els numero de cursos que volem retornar
        k = n;
    }

    public void setMethod(Boolean b) {
        avg = b;
    }

}
