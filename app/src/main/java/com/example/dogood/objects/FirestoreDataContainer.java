package com.example.dogood.objects;

import java.util.ArrayList;

public class FirestoreDataContainer {

    private ArrayList<GiveItem> giveItems;

    public FirestoreDataContainer() {
    }

    public FirestoreDataContainer(ArrayList<GiveItem> giveItems) {
        this.giveItems = giveItems;
    }

    public ArrayList<GiveItem> getGiveItems() {
        return giveItems;
    }

    public void setGiveItems(ArrayList<GiveItem> giveItems) {
        this.giveItems = giveItems;
    }
}
