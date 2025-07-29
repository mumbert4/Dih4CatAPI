package dih4cat.item;


import dih4cat.estructures.*;


import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * Aquesta classe ens gestiona i guarda tots els items del sistema i al distancia que hi ha entre els items
 * Disposa d'un map de Items, amb el seu identificador com a clau i la seva instància de la classe Item com a valor
 * Una matriu de distancies entre els items
 * Un array amb els IDs de tots els items
 */


public class ItemManager {
    Map<Integer, Item>  items;
    LinkedList<String> cols;
    HashMap<Integer, Set<String>> courseTags;
    HashMap<Integer, Integer> courseDuration;
    HashMap<Integer, String> courseLocation;
    public HashMap<String, Integer> mapOrg;
    public HashSet<String> organizers;
    int nPresential, nOnline, nHybrid;
    int nExecuted, nActive, nPlanned;
    private static ItemManager singleton;

    public double percentilHours;
    public static ItemManager getInstance(){
        if(singleton==null) singleton = new ItemManager();
        return singleton;
    }
    private ItemManager(){

    }

    public void initiateData(boolean auto, String path){
        items = new HashMap<>();
        cols= new LinkedList<>();
        courseTags= new HashMap<>();
        courseDuration= new HashMap<>();
        courseLocation = new HashMap<>();
        organizers = new HashSet<>();


        if(!auto){
            JFileChooser f = new JFileChooser();
            f.setFileSelectionMode(JFileChooser.FILES_ONLY);
            f.setCurrentDirectory(new File(System.getProperty("user.dir")+ File.separator + "Settings" + File.separator + "Data"));
            int option = f.showOpenDialog(null);
            if(option == JFileChooser.APPROVE_OPTION){
                createColumns(getAll(f.getSelectedFile()));

            }

        }

        else{
            createColumns(getAll(new File(path)));
        }




    }

    public void initializeData(){
        items = new HashMap<>();
        cols= new LinkedList<>();
        courseTags= new HashMap<>();
        courseDuration= new HashMap<>();
        courseLocation = new HashMap<>();
        organizers = new HashSet<>();
    }



    public Item getItem(int i){// retorna un dih4cat.item segons el seu ID
        return items.get(i);
    }











    //cream dih4cat.item
    public void createItem(int id, HashMap<String,Column> attributes,Set<String>aux, String modality, double duration, String organizer, String status){
        Item item = new Item(id,attributes, modality, duration, organizer, status);
        item.tags = aux;
        items.put(id, item);
    }




    public Set<Integer> getIDs(){
        return items.keySet();
    }



    public HashMap<Integer,Set<String>> getCoursesTags(){
        courseTags = new HashMap<>();
        return courseTags;
    }


    public List<String> getAll(File f)  { // Obtenim les línies del csv

        LinkedList<String> items = new LinkedList<String>();
        FileReader fr = null;
        try {
            fr = new FileReader(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scan = new Scanner(fr);

        while(scan.hasNextLine()) {
            items.add(new String(scan.nextLine()+"\n"));
        }
        return items;
    }

    /**
     * Crea les columnes de cada Item existent
     * @param listItems Llista de tots els items amb les seves característiques
     * Complexitat O (listItems.size)
     */
    public void createColumns(List<String> listItems) {
        cols = getColsFile(listItems.get(0)); //obtenim les columnes del csv


        this.nPresential = 0;
        this.nOnline = 0;
        this.nHybrid = 0;

        this.nPlanned = 0;
        this.nActive = 0;
        this.nExecuted =0;

        LinkedList<Double> hours = new LinkedList<>();
        int idInt = 0;
        for (int i = 1; i < listItems.size(); ++i) {// començam a 1 perque la 0 no ens importa
            Set<String> tags= new HashSet<>();
            HashMap<String,Column> prop = new HashMap<>();//hash map de les propietats de cada dih4cat.item
            String modality = "";
            String organizer ="";
            String status = "";
            double duration = 0;


            int elemAct = 0;
            String aux = "";
            int j = 0;
            while (j < listItems.get(i).length()) {
                if (listItems.get(i).charAt(j) == '"') {//si es una descripció o tags
                    ++j;
                    aux = "";
                    if(listItems.get(i).charAt(j)=='['){//tag
                        while(listItems.get(i).charAt(j)!=']'){
                            if(listItems.get(i).charAt(j)!='[' && listItems.get(i).charAt(j)!='\'')aux += listItems.get(i).charAt(j);
                            ++j;
                        }

                        ColumnTags actItem = new ColumnTags(aux);
                        tags = actItem.getValue();
                        prop.put(cols.get(elemAct), actItem);
                        j+=3;
                    }
                    else{
                        while (!(listItems.get(i).charAt(j) == '"' && listItems.get(i).charAt(j + 1) == ',' && listItems.get(i).charAt(j + 2) != ' ')) {
                            if(j+2>=listItems.get(i).length()) break;
                            aux += listItems.get(i).charAt(j);
                            ++j;
                        }
                        j += 2; //PER COMENÇAR LA SEUENT ITERACIO EN UN STRING
                        String act = cols.get(elemAct).replace("\"", "");




                        ColumnString actItem = new ColumnString(aux);
                        prop.put(cols.get(elemAct), actItem);

                    }

                    ++elemAct;
                    aux = "";
                }

                else if (listItems.get(i).charAt(j) == ',' || j == listItems.get(i).length() - 1) {//si no és descripció o ja acaba

                    if (isInt(aux)) {
                        boolean b =cols.get(elemAct).replace("\"", "").equals("duration_hours");
                        if(b){
                            duration = Integer.parseInt(aux);
                        }

                        ColumnInteger actItem = new ColumnInteger(Integer.parseInt(aux));
                        prop.put(cols.get(elemAct), actItem);
                    } else if (isB(aux)) {

                        ColumnBool actItem = new ColumnBool(Boolean.parseBoolean(aux));
                        prop.put(cols.get(elemAct), actItem);
                    } else if (isDbl(aux)) {
                        boolean b =cols.get(elemAct).replace("\"", "").equals("duration_hours");
                        if(b){//ACTUALMENT ENTRA AQUI
                            duration = Double.parseDouble(aux);
                            hours.add(duration);
                        }
                        ColumnDouble actItem = new ColumnDouble(Double.parseDouble(aux));
                        prop.put(cols.get(elemAct), actItem);
                    } else{

                        if (cols.get(elemAct).equals("Organizer..entity.")){
                            organizers.add(aux);
                            organizer = aux;
                        }
                        else if(cols.get(elemAct).equals("location")){

                            modality = aux;
                            if(modality.equals("Presential")) ++nPresential;
                            else if(modality.equals("Online")) ++nOnline;
                            else if(modality.equals("Hybrid")) ++nHybrid;
                        }
                        else if(cols.get(elemAct).equals("status")){
                            status = aux;
                            if(status.equals("planned")) ++nPlanned;
                            else if(status.equals("executed")) ++nExecuted;
                            else if(status.equals("active")) ++nActive;
                        }

                        ColumnString actItem = new ColumnString(aux);
                        prop.put(cols.get(elemAct), actItem);
                    }
                    aux = "";
                    ++elemAct;
                    ++j;
                }
                else {//agafam el caracter
                    aux += listItems.get(i).charAt(j);
                    ++j;
                }
            }
            prop.put("Distance", new ColumnDouble(0.0));
            createItem(idInt, prop,tags, modality, duration, organizer, status);
            ++idInt;
        }
        System.out.println("Num presential: "+nPresential);
        System.out.println("Num online: "+nOnline);
        System.out.println("Num hybrid: "+nHybrid);

        System.out.println("Num planned: "+nPlanned);
        System.out.println("Num executed: "+nExecuted);
        System.out.println("Num active: "+nActive);

        System.out.println("Organitzadors: " + organizers);

        percentilHours = get95thPercentile(hours);
        System.out.println("Percentil 90: " + percentilHours);
        cols.add("Distance");

        createMapOrg();

    }
    public double get95thPercentile(LinkedList<Double> hours) {
        if (hours == null || hours.isEmpty()) {
            throw new IllegalArgumentException("La lista no puede estar vacía");
        }

        Collections.sort(hours); // Ordenar la lista en orden ascendente

        int index = (int) Math.ceil(0.95 * hours.size()) - 1; // Índice del percentil 90
        return hours.get(index);
    }

    public void createMapOrg(){
        mapOrg = new HashMap<>();
        for(String o : organizers){
            mapOrg.put(o,0);
        }
        for (Item i : items.values()){
            String o = i.getOrganizer();
            mapOrg.put(o, mapOrg.get(o) + 1);
        }
        System.out.println(mapOrg);
    }

    public double getNumLocations(String l){
        if(l.equals("Presencial") || l.equals("Presential")) return nPresential;
        if(l.equals("Online")) return nOnline;
        else return nPresential + nOnline;
    }

    public double getNumStatus(String l){
        if(l.equals("planned")) return nPlanned;
        else if(l.equals("executed")) return nExecuted;
        else return nActive;
    }

    public HashSet<String> getOrganizers(){
        return organizers;
    }






    public LinkedList getColValues(String col) {//obtenim tots els valors d'una columna
        LinkedList l = new LinkedList();
        for(Item i: items.values()){
            l.add(i.getColumn(col));
        }
        return l;
    }






    /**
     * Comprova si un string donat és del tipus Int
     * @param input String que comprovam
     * @return Retorna si el string és del tipus Int
     * Complexitat O (1)
     */
    private boolean isInt(String input) {
        try{
            int inputDbl = Integer.parseInt(input);
            return true;
        }
        catch(NumberFormatException ex)
        {
            return false;
        }
    }


    /**
     * Comprova si un string donat és del tipus Double
     * @param input String que comprovam
     * @return Retorna si el string és del tipus Double
     * Complexitat O (1)
     */
    private boolean isDbl(String input) {
        try{
            double inputDbl = Double.parseDouble(input);
            return true;
        }
        catch(NumberFormatException ex)
        {
            return false;
        }
    }


    /**
     * Comprova si un string donat és del tipus Bool
     * @param input String que comprovam
     * @return Retorna si el string és del tipus Bool
     * Complexitat O (1)
     */
    private boolean isB(String input) {
        return input.equals("True") || input.equals("False") || input.equals("true") || input.equals("false") || input.equals("TRUE") || input.equals("FALSE")|| input.equals("VERDADERO") || input.equals("FALSO");
    }



    //retorna els noms de les cols del fitxer csv
    LinkedList<String> getColsFile(String fila){
        LinkedList<String> a = new LinkedList<>();
        int j = 0;
        String aux = "";

        while(j < fila.length()){
            if(fila.charAt(j)==',' || j == fila.length()-1){
                if (aux.equals("id")){
                    a.add(aux);
                    aux ="";


                }
                else{
                    a.add(aux);
                    aux = "";
                }
            }
            else{
                aux += fila.charAt(j);
            }
            ++j;
        }

        return a;
    }

    public LinkedList<String> getCols(){
        return cols;
    }


    public Set<Vector<Object>> getValues(){
        Set s = new HashSet();
        for(Item i : items.values()){
            Vector<Object> aux = new Vector<>();
            for(String a : cols){
                Column c = i.getColumn(a);
                aux.add(c.getValue());
            }
            s.add(aux);
        }
        return s;
    }

    public LinkedList<Vector<Object>> getKValues(LinkedList<Integer> s){
        System.out.println("Recomeded courses: " + s);
        LinkedList r = new LinkedList<>();
        for(Integer i : s){
            Item n = items.get(i);
            Vector<Object> aux = new Vector<>();
            for(String a : cols){
                Column c = n.getColumn(a);
                aux.add(c.getValue());
            }
            r.add(aux);
        }
        System.out.println("Vector retornat: "+ r);
        return r;
    }
    public void showItems(){
        int i = 0;
        for(Item it: items.values()){
            System.out.println("Estam a l'dih4cat.item: " + i);
            for(String s: cols){

                System.out.print("    Propietat: " + s);
                System.out.println(" valor: " + it.getColumn(s).getValue());
            }
            ++i;
            if(i==20) return;
        }
    }

}
