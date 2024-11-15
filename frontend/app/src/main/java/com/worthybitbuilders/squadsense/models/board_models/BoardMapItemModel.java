package com.worthybitbuilders.squadsense.models.board_models;

import java.io.Serializable;
import java.util.List;

public class BoardMapItemModel extends BoardBaseItemModel {
    public static class AddressModel {
        public String title;
        public String description;
        public double latitude;
        public double longitude;

        public AddressModel() {}

        public AddressModel(String title, String description, double latitude, double longitude) {
            this.title = title;
            this.description = description;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    private List<AddressModel> addresses;

    public BoardMapItemModel() {
        super("", "CellMap");
    }

    public BoardMapItemModel(List<AddressModel> addresses) {
        super("", "CellMap");
        this.addresses = addresses;
    }

    public List<AddressModel> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressModel> addresses) {
        this.addresses = addresses;
    }

    @Override
    public String getContent() {
        if (addresses.size() > 1) {
            return String.valueOf(addresses.size()) + " addresses";
        } else if (addresses.size() == 1) return addresses.get(0).title;
        else return "";
    }
}
