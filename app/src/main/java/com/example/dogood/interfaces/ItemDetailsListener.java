package com.example.dogood.interfaces;

import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.GiveItem;

public interface ItemDetailsListener {
    void getSelectedItem(GiveItem giveItem, AskItem askItem, boolean isGiveItem);
}
