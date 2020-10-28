package com.example.dogood.objects;

public class User {

    private String name;
    private String email;
    private String password;
    private String city;
    private String phone;
    private String photo;

    public User() {
    }

    public User(String name, String email, String password, String city, String phone, String photo) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.city = city;
        this.phone = phone;
        this.photo = photo;
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
