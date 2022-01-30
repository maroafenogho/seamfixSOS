package com.maro.seamfixsos.util;

import com.google.gson.annotations.SerializedName;

public class UserLocation {
    @SerializedName("longitude")
    double longitude;
    @SerializedName("latitude")
    double latitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public UserLocation(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public UserLocation(){}
}
