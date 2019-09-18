package com.example.getdown;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ContactInfo implements Parcelable {

    private String name;
    private Place place;
    private String phoneNumber;

    public ContactInfo(String name, Place address, String phoneNumber) {
        this.name = name;
        this.place = address;
        this.phoneNumber = phoneNumber;
    }

    protected ContactInfo(Parcel in) {
        name = in.readString();
        //place = in.readParcelable(Place.class.getClassLoader());
        phoneNumber = in.readString();
    }

    public static final Creator<ContactInfo> CREATOR = new Creator<ContactInfo>() {
        @Override
        public ContactInfo createFromParcel(Parcel in) {
            return new ContactInfo(in);
        }

        @Override
        public ContactInfo[] newArray(int size) {
            return new ContactInfo[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(Place place) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.phoneNumber);
        place.writeToParcel(parcel, i);
    }


}
