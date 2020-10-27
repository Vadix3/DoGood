package com.example.dogood.objects;


import android.graphics.Bitmap;

import java.io.Serializable;

public class GiveItem implements Serializable {
    String id;
    String name;
    String category;
    String state;
    String price;
    String description;
    Bitmap pictures;
    String date; //TODO: solve date issue
    User giver;

    public GiveItem(String id, String name, String category, String state, String price
            , String description, Bitmap pictures, String date, User giver) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.state = state;
        this.price = price;
        this.description = description;
        this.pictures = pictures;
        this.date = date;
        this.giver = giver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getPictures() {
        return pictures;
    }

    public void setPictures(Bitmap pictures) {
        this.pictures = pictures;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getGiver() {
        return giver;
    }

    public void setGiver(User giver) {
        this.giver = giver;
    }

    @Override
    public String toString() {
        return "GiveItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", state='" + state + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", pictures='" + pictures + '\'' +
                ", date='" + date + '\'' +
                ", giver=" + giver +
                '}';
    }
}
