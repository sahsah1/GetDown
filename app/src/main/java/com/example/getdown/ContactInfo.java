package com.example.getdown;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ContactInfo implements Serializable {

    private String name;
    private Place place;
    private String phoneNumber;

    public ContactInfo(String name, Place address, String phoneNumber) {
        this.name = name;
        this.place = address;
        this.phoneNumber = phoneNumber;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public Place getPlace() {
        return place;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}
