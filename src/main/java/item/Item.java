package item;

import estructures.Column;
import estructures.ColumnDouble;

import java.util.HashMap;
import java.util.Set;
/**
 *Classe que representa la instància d'un item, format pel seu ID i els seus atributs
 */

public class Item {

    private int id;
    public HashMap<String, Column> attributes;

    public Set<String> tags;
    public double duration;
    public String modality, organizer, status;
    public double distance;
    public Item() {
    }

    public Set<String> getTags() {
        return tags;
    }

    /**
     * COntructora de la classe item
     *
     * @param id         ID que se li assignara al item
     * @param attributes Array dels atributs de l'item
     *                   Complexitat O (attributes.size)
     */
    public Item(int id, HashMap<String, Column> attributes, String modality,double duration, String organizer, String status) {
        this.id = id;
        this.attributes = attributes;
        this.modality = modality;
        this.duration = duration;
        this.organizer = organizer;
        this.status = status;
    }
    //Getters


    /**
     * Obtenir el ID del Item
     *
     * @return id del Item
     * Complexitat O (1)
     */
    public int getId() {
        return id;
    }


    public double getDuration(){
        return duration;
    }

    public String getModality(){
        return modality;
    }

    public String getOrganizer(){
        return organizer;
    }
    /**
     * Obtenir el nombre d'atributs que te
     *
     * @return Nombre d'atributs del item
     * Complexitat O(1)
     */
    public int getSizeAttributes() {
        return attributes.size();
    }


    public String getStatus(){
        return status;
    }

    public Column getColumn(String n) {
        return attributes.get(n);
    }


    public HashMap<String, Column> getAttributes() {
        return attributes;
    }

    public void setDistance(double d){
        attributes.remove("Distance");
        attributes.put("Distance", new ColumnDouble(d));
        this.distance = d;
    }
}