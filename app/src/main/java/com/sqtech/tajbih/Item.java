package com.sqtech.tajbih;

public class Item {
    private String itemName;
    private int currentValue=0;
    private int targetValue;

    public Item(String itemName, int currentValue, int targetValue) {
        this.itemName = itemName;
        this.currentValue = currentValue;
        this.targetValue = targetValue;
    }

    public String getItemName() {
        return itemName;
    }


    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public int getTargetValue() {
        return targetValue;
    }

}
