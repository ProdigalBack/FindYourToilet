package com.example.findyourtoilet;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Toilet implements Serializable {
    private String mStatus;
    private String mAddress;
    private double mLongitude;
    private double mLatitude;
    Toilet(String status,String address,double longitude,double latitude)
    {
        mStatus=status;
        mAddress=address;
        mLongitude=longitude;
        mLatitude=latitude;
    }

    public String getStatus()
    {
        return mStatus;
    }

    public String getAddress()
    {
        return mAddress;
    }

    public double getLongitude()
    {
        return mLongitude;
    }

    public double getLatitude()
    {
        return mLatitude;
    }


}
