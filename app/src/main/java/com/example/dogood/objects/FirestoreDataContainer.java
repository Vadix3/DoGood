package com.example.dogood.objects;

import java.util.ArrayList;

public class FirestoreDataContainer {

    private ArrayList<GiveItem> giveItems;
    private ArrayList<AskItem> askItems;
    private ArrayList<User> users;

    public FirestoreDataContainer() {
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public FirestoreDataContainer(ArrayList<GiveItem> giveItems, ArrayList<AskItem> askItems
            , ArrayList<User> users) {
        this.giveItems = giveItems;
        this.askItems = askItems;
        this.users = users;
    }

    public ArrayList<AskItem> getAskItems() {
        return askItems;
    }

    public void setAskItems(ArrayList<AskItem> askItems) {
        this.askItems = askItems;
    }

    public ArrayList<GiveItem> getGiveItems() {
        return giveItems;
    }

    public void setGiveItems(ArrayList<GiveItem> giveItems) {
        this.giveItems = giveItems;
    }
}
