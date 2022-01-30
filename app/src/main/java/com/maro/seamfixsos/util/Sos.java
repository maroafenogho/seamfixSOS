package com.maro.seamfixsos.util;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Sos {

    @SerializedName("phoneNumbers")
    List<String> phoneNumbers;
    @SerializedName("image")
    String image;
    @SerializedName("location")
    UserLocation userLocation;

    public Sos(List<String> phoneNumbers, String image, UserLocation userLocation) {
        this.phoneNumbers = phoneNumbers;
        this.image = image;
        this.userLocation = userLocation;
    }

    public Sos(){}

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setUserLocation(UserLocation userLocation) {
        this.userLocation = userLocation;
    }
}
