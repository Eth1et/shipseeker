package com.example.shipseeker.model;

import android.graphics.Bitmap;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CruiseRoute {
    private String name;
    private String description;
    private String departPlace;
    private Timestamp departTimestamp;
    private int price = -1;
    private int slots;
    private String imageName;
    private Map<String, Integer> bookedBy;
    private String id;

    private Bitmap image;

    public CruiseRoute() {
        bookedBy = new HashMap<>();
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Integer> getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(Map<String, Integer> bookedBy) {
        this.bookedBy = bookedBy;
    }

    public CruiseRoute(String name, String description, String departPlace, Timestamp departTimestamp,
                       int price, int slots, String imageName, Map<String, Integer> bookedBy, String id) {
        this.name = name;
        this.description = description;
        this.departPlace = departPlace;
        this.departTimestamp = departTimestamp;
        this.price = price;
        this.slots = slots;
        this.imageName = imageName;
        this.bookedBy = bookedBy;
        this.id = id;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartPlace() {
        return departPlace;
    }

    public void setDepartPlace(String departurePlace) {
        this.departPlace = departurePlace;
    }

    public Timestamp getDepartTimestamp() {
        return departTimestamp;
    }

    public String _getDepartureTimestampString() {
        if (departTimestamp != null) {
            Date date = departTimestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM.dd. HH:mm", Locale.getDefault());
            return sdf.format(date);
        }
        return "";
    }

    public void setDepartTimestamp(Timestamp departTimestamp) {
        this.departTimestamp = departTimestamp;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Bitmap _getImage() {
        return image;
    }

    public void _setImage(Bitmap image) {
        this.image = image;
    }

    public void clone(CruiseRoute other){
        name = other.getName();
        description = other.getDescription();
        departPlace = other.getDepartPlace();
        departTimestamp = other.getDepartTimestamp();
        price = other.getPrice();
        slots = other.getSlots();
        imageName = other.getImageName();
        bookedBy = other.getBookedBy();
        id = other.getId();
        image = other._getImage();
    }
}
