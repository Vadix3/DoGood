package com.example.dogood.objects;

import java.util.ArrayList;

public class User {

    private String name;
    private String email;
    private String password;
    private String city;
    private String phone;
    private String photo;

    private ArrayList<GiveItem> giveItems = new ArrayList<>();
    private ArrayList<AskItem> askItems = new ArrayList<>();

    public User() {
    }

    public User(String name, String email, String password, String city, String phone) {
        //TODO: city? latlng? what else?

        this.name = name;
        this.email = email;
        this.password = password;
        this.city = city;
        this.phone = phone;
    }

    public User(String name, String email) { // A constructor in case I log in with google or facebook
        this.name = name;
        this.email = email;
        this.password = "pending";
        this.city = "pending";
        this.phone = "pending";
    }

    public void addGiveItem(GiveItem item){
        this.giveItems.add(item);
    }
    public void addAskItem(AskItem item){
        this.askItems.add(item);
    }

    public ArrayList<GiveItem> getGiveItems() {
        return giveItems;
    }

    public void setGiveItems(ArrayList<GiveItem> giveItems) {
        this.giveItems = giveItems;
    }

    public ArrayList<AskItem> getAskItems() {
        return askItems;
    }

    public void setAskItems(ArrayList<AskItem> askItems) {
        this.askItems = askItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", city='" + city + '\'' +
                ", phone='" + phone + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
