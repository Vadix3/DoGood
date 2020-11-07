package com.example.dogood.objects;

import java.util.ArrayList;

public class FirestoreDataContainer {

    private ArrayList<GiveItem> giveItems;
    private ArrayList<AskItem> askItems;
    //TODO: Add users to container


    public FirestoreDataContainer() {
    }

    public FirestoreDataContainer(ArrayList<GiveItem> giveItems, ArrayList<AskItem> askItems) {
        this.giveItems = giveItems;
        this.askItems = askItems;
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
