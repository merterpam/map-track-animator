package com.erpam.mert.maptrackanimator.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Coordinate model
 */

public class Coordinate implements Parcelable
{
    private int id;
    private long lapId;
    private double longitude;
    private double latitude;
    private int altitude;
    private long timestamp;
    private float accuracy;

    public Coordinate() {
        this.setLapId(-1);

    }

    public Coordinate(double lng, double lat, long t) {
        longitude = lng;
        latitude = lat;
        timestamp = t;
    }

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

    /**
     *
     * @return Timestamp in seconds
     */
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public long getLapId() {
        return lapId;
    }

    public void setLapId(long lapId) {
        this.lapId = lapId;
    }

    protected Coordinate(Parcel in) {
        id = in.readInt();
        lapId = in.readLong();
        longitude = in.readDouble();
        latitude = in.readDouble();
        altitude = in.readInt();
        timestamp = in.readLong();
        accuracy = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(lapId);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeInt(altitude);
        dest.writeLong(timestamp);
        dest.writeFloat(accuracy);
    }

    public static final Parcelable.Creator<Coordinate> CREATOR = new Parcelable.Creator<Coordinate>() {
        @Override
        public Coordinate createFromParcel(Parcel in) {
            return new Coordinate(in);
        }

        @Override
        public Coordinate[] newArray(int size) {
            return new Coordinate[size];
        }
    };

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
}
