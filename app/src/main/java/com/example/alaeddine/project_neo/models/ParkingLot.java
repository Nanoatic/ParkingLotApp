package com.example.alaeddine.project_neo.models;

/**
 * Created by Alaeddine on 22-Nov-17.
 */

public class ParkingLot {
    private int id;
    private String name;
    private String photo;
    private int max_places;
    private float rating;
    private int open_hour;
    private int close_hour;
    private int price;
    private String info;
    private float lat;
    private float longt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getMax_places() {
        return max_places;
    }

    public void setMax_places(int max_places) {
        this.max_places = max_places;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getOpen_hour() {
        return open_hour;
    }

    public void setOpen_hour(int open_hour) {
        this.open_hour = open_hour;
    }

    public int getClose_hour() {
        return close_hour;
    }

    public void setClose_hour(int close_hour) {
        this.close_hour = close_hour;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public float getLongt() {
        return longt;
    }

    public void setLongt(int longt) {
        this.longt = longt;
    }
}
