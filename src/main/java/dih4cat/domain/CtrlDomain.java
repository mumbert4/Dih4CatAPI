package dih4cat.domain;

import dih4cat.algorithm.GibertDistance;
import dih4cat.graph.Graph;
import dih4cat.graph.Node;
import dih4cat.item.ItemManager;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.*;

public class CtrlDomain {

    private static CtrlDomain singleton;
    private static ItemManager manager;
    private static Graph graph;
    private static GibertDistance GIB;

    public static CtrlDomain getInstance(){
        if(singleton==null) singleton = new CtrlDomain();
        return singleton;
    }

    private CtrlDomain(){
        initializeCtrlDomain();
    }


    private void initializeCtrlDomain(){
        manager = ItemManager.getInstance();
        graph = Graph.getInstance();


    }
    public void setGibert(){
        GIB = GibertDistance.getInstance();
    }

    //FUNCIONS DATA SCREEN
    public void initializeData(boolean auto, String path){
        manager.initiateData(auto,path);
    }
    public void initializeData(){
        manager.initializeData();
    }

    public LinkedList<String> getCols(){
        return manager.getCols();
    }
    public Set<Vector<Object>> getValues(){
        return manager.getValues();
    }

    //FUNCIONS USER SCREEN
    public void courseDistances(Set<String> selectedTags, Set<String> usefullTags, Set<String> unusedTags, String modality, String userStatus, Integer minDuration, Integer maxDuration, Set<String> organizers, boolean format, boolean duration, boolean organizer, boolean status, boolean strongTags){
        GIB.courseDistances(selectedTags,usefullTags, unusedTags, modality, userStatus ,minDuration, maxDuration, organizers, format, duration, organizer, status, strongTags);
    }

    public LinkedList<Vector<Object>> getKvalues(){
        return manager.getKValues(GIB.getRecomended());
    }

    public void setNCourses(Integer n){
        GIB.setNCourses(n);
    }
    public void setMethod(Boolean b){
        GIB.setMethod(b);
    }


    //FUNCIONS GRAPH SCREEN
    public void initializeGraph(){
        graph.initialize();
    }

    public void importFile(File file){
        graph.importFile(file);
    }

    public DefaultMutableTreeNode getTree(){
        return graph.getTree();
    }

    public void completeMatrix(){
        GIB.completeMatrix(graph.getIDs());
    }

    public Integer getNextNode(HashSet<Integer> aux){
        return graph.getNextNode(aux);
    }

    public String idToString(Integer node){
        return graph.idToString(node);
    }

    public Set<Node> getNeighbours(Integer node){
        return graph.getNeighbours(node);
    }

    public void deleteNode(Integer n){
        graph.deleteNode(n);
    }

    public void changeName(Integer node, String name){
        graph.changeName(node,name);
    }

    public void deleteEdge(Integer n) {
        graph.deleteEdge(n);
    }



    //FUNCIONS GIBERT

    public Set<String> getAncestors(Integer i){
        return graph.getAncestors(i);
    } //retornam els ancestres de un node i

    public Set<String> getTags(){return graph.getNames();}//obtenim tots els tags

    public HashMap<Integer,Set<String>> getCoursesTags(){ //obtenim tags de cada curs
        return manager.getCoursesTags();
    }

    public double getDuration(int i){ //obtenim la duració d'un curs
        return manager.getItem(i).getDuration();
    }

    public int stringToId(String c){//obtenim id d'un node
        return graph.stringToId(c);
    }

    public double getNumLocations(String l) {return manager.getNumLocations(l);}

    public double getNumStatus(String l) {return manager.getNumStatus(l);}

    public HashMap getMap(){
        return manager.mapOrg;
    }

    public double getPercentilHours(){
        return manager.percentilHours;
    }

    public String getModality(int i){
        return  manager.getItem(i).getModality();
    }

    public String getStatus(int i){
        return manager.getItem(i).getStatus();
    }
    public Set<Integer> getIDs(){
        return manager.getIDs();
    }
    public Set<String> getTagsCourse(int i){
        return manager.getItem(i).getTags();
    }


    public String getOrganizer(Integer i){
        return manager.getItem(i).getOrganizer();
    }



}
