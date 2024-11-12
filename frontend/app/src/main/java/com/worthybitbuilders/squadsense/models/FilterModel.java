package com.worthybitbuilders.squadsense.models;

/**
 * This class is for the home fragment projects list
 */
public class FilterModel {
    public enum TypeFilter {TEXT, AVATAR}
    private String value;
    private TypeFilter type;

    public FilterModel(String value, TypeFilter type)
    {
        this.value = value;
        this.type = type;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TypeFilter getType() {
        return type;
    }

    public void setType(TypeFilter type) {
        this.type = type;
    }
}
