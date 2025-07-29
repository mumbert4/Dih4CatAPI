package dih4cat.graph;

import java.util.HashSet;
import java.util.Set;

public class Node {

    String name;

    HashSet<Node> childs;
    Node parent;

    HashSet<String> ancestors;
    int x,y;
    int mod;
    Integer id;
    public Node(String name, Integer nodeId){
        this.name = name;
        childs = new HashSet<>();
        id = nodeId;
        parent = null;
        ancestors = new HashSet<>();
    }
    public void deleteChild(Node n){
        childs.remove(n);
    }
    public void deleteParent(){
        parent = null;
    }
    public void destroy(){
        if(parent !=null) parent.deleteChild(this);
        for(Node n: childs){
            n.deleteParent();
        }
    }

    public Set<Integer> getChildIds(){
        Set<Integer> s = new HashSet<>();
        for(Node n : childs) s.add(n.id);

        return s;
    }

    public Integer getId(){
        return id;
    }
    public void addChild(Node node){
        childs.add(node);
    }

    public void setParent(Node node){
        parent = node;
        setAncestors();
    }

    public String getName(){
        return this.name;
    }

    public HashSet<Node> getChilds(){
        return childs;
    }

    public Node getParent(){
        return parent;
    }

    public int getNumParents(){

        return parent!= null ? 1 : 0; // si es null retorna 0, sinos retorna 1
    }

    public HashSet<String> getAncestors(){
        return ancestors;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public void setAncestors(){
        if(hasParent()){
            ancestors = new HashSet(parent.getAncestors());
            ancestors.add(parent.getName());
        }
    }

    public boolean hasChild(String s){
        for(Node n : childs){
            if(n.name.equals(s)) return true;
        }
        return false;
    }

    public boolean hasParent(){
        if(parent==null) return false;
        return true;
    }
}
