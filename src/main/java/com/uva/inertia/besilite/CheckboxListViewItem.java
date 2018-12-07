package com.uva.inertia.besilite;

public class CheckboxListViewItem {
    String name;
    int value; /* 0 -&gt; checkbox disable, 1 -&gt; checkbox enable */
    int pk;

    CheckboxListViewItem(String name, int value, int pk){
        this.name = name;
        this.value = value;
        this.pk = pk;
    }
    public String getName(){
        return this.name;
    }
    public int getValue(){
        return this.value;
    }
    public void setValue(int i){
        this.value = i;
    }
    public int getPK(){
        return this.pk;
    }
}
