package com.example.dogood.objects;


import java.io.Serializable;

public class AskItem implements Serializable {
    private String id; // Id sample: "A21" - Ask item 21
    private String name;
    private String category;
    private String city;
    private String description;
    private String date;
    private User requester;
    private boolean isDiscreteRequest;

    public AskItem() {
    }

    public AskItem(String id, String name, String category, String city, String description
            , String date, User requester, boolean isDiscreteRequest) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.city = city;
        this.description = description;
        this.date = date;
        this.requester = requester;
        this.isDiscreteRequest = isDiscreteRequest;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "AskItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", city='" + city + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", requester=" + requester +
                ", isDiscreteRequest=" + isDiscreteRequest +
                '}';
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public boolean isDiscreteRequest() {
        return isDiscreteRequest;
    }

    public void setDiscreteRequest(boolean discreteRequest) {
        isDiscreteRequest = discreteRequest;
    }
}
