package dih4cat.graph;

import dih4cat.algorithm.GibertDistance;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Graph {

    private static Graph singleton;
    private Map<String, Node> nodes;
    private Map<Integer,Node> nodesId;
    Integer nodeId;




    private Graph(){
        initialize();
    }
    public static Graph getInstance(){
        if(singleton == null) singleton = new Graph();
        return singleton;
    }

    public HashSet<String> getAncestors(Integer i){
        return nodesId.get(i).getAncestors();
    }
    public void initialize(){
        nodes = new HashMap<>();
        nodeId = 0;
        nodesId = new HashMap<>();

    }

    public void changeName(Integer id, String name){
        nodes.remove(idToString(id));
        nodesId.get(id).setName(name);
        nodes.put(name, nodesId.get(id));

    }

    public Set<Integer> getIDs(){
        return nodesId.keySet();
    }



    public void importFile(File file){
        try{
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                String parentNode = "";
                String auxNode = "";
                Boolean nodeCompleted = false;
                ArrayList<String> childNodes = new ArrayList<>();
                for (int i = 0; i < data.length(); ++i) {
                    Character c = data.charAt(i);
                    if ((c.equals(':') || c.equals(';')) && !nodeCompleted) {
                        nodeCompleted = true;
                    } else {
                        if (!nodeCompleted) {
                            parentNode += c;
                        } else {
                            if (c.equals(',') || c.equals(';')) {
                                childNodes.add(auxNode.strip());

                                auxNode = "";
                            } else auxNode += c;
                        }
                    }

                }


                if (!nodes.containsKey(parentNode)) {
                    addNode(new Node(parentNode, nodeId));
                }

                Node n = nodes.get(parentNode);
                for (String s : childNodes) {
                    if (nodes.containsKey(s)) {// si el fill existeix
                        addEdge(n, nodes.get(s),false);
                    } else {
                        Node aux = new Node(s, nodeId);
                        addNode(aux);
                        addEdge(n, aux, false);
                    }

                }
            }

        }catch(FileNotFoundException e){
            System.out.println("Fichero no encontrado");
            e.printStackTrace();
        }
        System.out.println("Nodes : " + nodes.keySet());
    }

    public void deleteNode(Integer i){
        nodesId.get(i).destroy();
        nodes.remove(idToString(i));
        nodesId.remove(i);

    }

    public void deleteEdge(Integer n){
        nodesId.get(n).parent.deleteChild(nodesId.get(n));
        nodesId.get(n).deleteParent();

    }

    public Integer getSize(){
        return nodeId + 1;
    }
    public void addNode(Node n){
        nodes.put(n.getName(),n);
        nodesId.put(nodeId,n);
        ++nodeId;
    }

    public void addNode(String s){
        Node node = new Node(s,nodeId);
        nodes.put(s,node);
        nodesId.put(nodeId,node);
        ++nodeId;
    }
    public void addEdge(Node father, Node child, Boolean nou){    //En el cas d'un graf unidireccional, afegim una aresta que va de A a B,
        //Node a -> Pare ------ Node b -> fill
        father.addChild(child);
        child.setParent(father);
        if(nou)GibertDistance.getInstance().calculateDistance(child.getId());
    }

    public Boolean validEdge(Integer father, Integer child){
        Set<Integer> visited = new HashSet<Integer>(); //set amb els IDs dels nodes ja visitats
        //Farem dfs per velocitat

        //Miram que el fill nomes tengui un pare
        if(nodesId.get(child).hasParent()) return false;


        //MIRAM QUE NO FAGUEM UN CICLE
        if(nodesId.get(father).hasParent()){
            Stack<Node> s = new Stack<>();
            s.add(nodesId.get(father).getParent());
            while(!s.empty()){
                Node aux = s.pop();
                Integer id = aux.getId();
                if(id==child) return false;
                visited.add(id);
                if(aux.hasParent()){
                    Node n = aux.getParent();
                    if(!visited.contains(n.getId())) s.add(n);

                }

            }
        }


        return true;
    }

    public Integer stringToId(String s){//obtenim id de un node a partir del nom
        return nodes.get(s).getId();
    }

    public Set<String> getNames(){
        return nodes.keySet();
    }
    public Integer getNextNode(Set<Integer> draw){//per obtenir següent node a pintar, casi sempre es l'arrel
        Integer act = -1;
        Integer nTrue = 10;
        for(int i = 0; i < nodeId; ++i){

            if(!draw.contains(i)){
                Integer numParents = nodesId.get(i).getNumParents();

                if(numParents < nTrue) {
                    act = i;
                    nTrue = numParents;
                }
            }

        }
        return act;

    }

    public Node getNode(Integer n){
        return nodesId.get(n);
    }

    public HashSet<Node>getNeighbours(Integer a){
        if(!nodesId.containsKey(a)){
            return null;
        }
        return nodesId.get(a).getChilds();
    }

    public Set<Integer> getChildsIds(Integer n){
        return nodesId.get(n).getChildIds();
    }

    public void  printGraph(){
        System.out.println("Nodes");
        for (Integer s : nodesId.keySet()){
            System.out.println(nodesId.get(s).getName() + "::  " + s);
        }
    }

    public DefaultMutableTreeNode getTree(){

        if(nodes.size()>0){
          return createTree(getNode(getRoot()));

        }
        else return new DefaultMutableTreeNode("No data");
    }

    public DefaultMutableTreeNode createTree(Node node){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(node.getName());
        for(Node child : node.getChilds()){
            root.add(createTree(child));
        }
        return root;
    }

    public Integer getRoot(){
        Integer act = -1;
        Integer nTrue = 10;
        for(Node n:  nodesId.values()){
            Integer numParents = n.getNumParents();
            if(numParents < nTrue) {
                act = n.id;
                nTrue = numParents;
            }
        }
        System.out.println("L'arrel que retornam és: " + nodesId.get(act).getName() + " amb: " + nTrue + " valors");
        return act;
    }


    public String idToString(Integer n){
        return nodesId.get(n).getName();
    }

}
