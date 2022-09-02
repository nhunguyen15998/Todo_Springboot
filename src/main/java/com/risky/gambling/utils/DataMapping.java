package com.risky.gambling.utils;

public class DataMapping {
    public String key;
    public String value;
    private static DataMapping dataMapping = null;

    // public DataMapping(String key, String value) {
    //     this.key = key;
    //     this.value = value;
    // }

    // public DataMapping(int key, String value) {
    //     this.key = String.valueOf(key);
    //     this.value = value;
    // }
    public static DataMapping getInstance(String key, String value)
    {
        if(dataMapping == null){
            DataMapping item = new DataMapping();
            item.key = key;
            item.value = value;
            return item;
        }
        return dataMapping;
    }

    public static DataMapping getInstance(int key, String value)
    {
        if(dataMapping == null){
            DataMapping item = new DataMapping();
            item.key = String.valueOf(key);
            item.value = value;
            return item;
        }
        return dataMapping;
    }

    public String toString()
    {
        return this.value;
    }
}
