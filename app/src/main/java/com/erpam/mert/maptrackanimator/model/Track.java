package com.erpam.mert.maptrackanimator.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.erpam.mert.maptrackanimator.BR;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Track model
 */

public class Track extends BaseObservable implements Parcelable {

    private double avgSpeed;
    private List<Coordinate> coordinates;
    private int day;
    private long duration;
    private long id;
    private double maxSpeed;
    private double length;
    private int year;

    //Cached cumulative length traversed from the beginning at each coordinate index
    private List<Double> lengthArray;

    //Speed at each coordinate index
    private List<Double> speedArray;


    public Track() {
        this.coordinates = new ArrayList<>();
    }

    private Track(Parcel in) {
        id = in.readLong();
        maxSpeed = in.readDouble();
        avgSpeed = in.readDouble();
        duration = in.readLong();
        length = in.readDouble();
        year = in.readInt();
        day = in.readInt();
        if (in.readByte() == 0x01) {
            coordinates = new ArrayList<Coordinate>();
            in.readList(coordinates, LatLng.class.getClassLoader());
        } else {
            coordinates = null;
        }
    }

    @Bindable
    public double getAvgSpeed() {
        return avgSpeed;
    }

    private void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = Math.floor(avgSpeed * 10) / 10;
        notifyPropertyChanged(BR.avgSpeed);
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
        if (coordinates.size() < 2)
            duration = 0;
        else {
            duration = coordinates.get(coordinates.size() - 1).getTimestamp() - coordinates.get(0).getTimestamp();
        }
        notifyPropertyChanged(BR._all);
    }

    public int getDay() {
        return day;
    }

    private void setDay(int day) {
        this.day = day;
    }

    public long getDuration() {
        return duration;
    }

    @Bindable
    public String getDurationInMin() {
        long duration = getDuration();
        return Long.toString(duration / 60);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Bindable
    public double getLength() {
        return length;
    }

    private void setLength(double length) {
        this.length = Math.floor(length * 10) / 10;
        notifyPropertyChanged(BR.length);
    }

    @Bindable
    public double getMaxSpeed() {
        return maxSpeed;
    }

    private void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = Math.floor(maxSpeed * 10) / 10;
        notifyPropertyChanged(BR.maxSpeed);
    }

    public int getYear() {
        return year;
    }

    private void setYear(int year) {
        this.year = year;
    }

    /**
     * Calculates the date based on timestamp
     */
    public void calculateDate() {
        if (coordinates.size() > 0) {
            long timeStamp = coordinates.get(0).getTimestamp() * 1000;
            Calendar c = new GregorianCalendar(TimeZone.getDefault());
            c.setTimeInMillis(timeStamp);
            this.setYear(c.get(Calendar.YEAR));
            this.setDay(c.get(Calendar.DAY_OF_YEAR));
        }
    }

    /**
     * Calculates the statistics of the lap
     */
    public void calculcateStatistics() {

        long id = coordinates.get(0).getLapId();
        double maxSpeed = 0;
        double maxAngle = 0;
        double lengthCumulative = 0;

        speedArray = new ArrayList<>();
        speedArray.add(0.0);

        lengthArray = new ArrayList<>();
        lengthArray.add(0.0);

        for (int i = 1; i < coordinates.size(); i++) {

            Location startLoc = new Location("");
            startLoc.setLatitude(coordinates.get(i - 1).getLatitude());
            startLoc.setLongitude(coordinates.get(i - 1).getLongitude());

            Location middleLoc = new Location("");
            middleLoc.setLatitude(coordinates.get(i).getLatitude());
            middleLoc.setLongitude(coordinates.get(i).getLongitude());

            double length = startLoc.distanceTo(middleLoc) / 1000;
            lengthArray.add(length);

            double duration;

            if (i < coordinates.size() - 1) {
                Location endLoc = new Location("");
                endLoc.setLatitude(coordinates.get(i + 1).getLatitude());
                endLoc.setLongitude(coordinates.get(i + 1).getLongitude());
                length += middleLoc.distanceTo(endLoc) / 1000;
                duration = ((double) (coordinates.get(i + 1).getTimestamp() - coordinates.get(i - 1).getTimestamp())) / 3600;
            } else {
                duration = ((double) (coordinates.get(i).getTimestamp() - coordinates.get(i - 1).getTimestamp())) / 3600;
            }

            speedArray.add(length / duration);
        }

        for (int i = 1; i < coordinates.size(); i++) {
            double speed = speedArray.get(i);
            double angle = speedArray.get(i);

            if (maxSpeed < speed)
                maxSpeed = speed;

            if (maxAngle < angle)
                maxAngle = angle;

            lengthCumulative += lengthArray.get(i);
        }

        double duration = ((double) this.getDuration());
        double avgSpeed = 0;
        if (duration != 0)
            avgSpeed = lengthCumulative / duration * (3600.0);

        this.setId(id);
        this.setMaxSpeed(maxSpeed);
        this.setAvgSpeed(avgSpeed);
        this.setLength(lengthCumulative);
        this.calculateDate();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Track) {
            Track anotherTrack = (Track) anObject;
            return this.getId() == anotherTrack.getId();
        }
        return false;
    }

    public double getSpeedAtCoordinate(int index) {
        return speedArray.get(index);
    }

    @Bindable
    public String getStartTime() {
        if (coordinates.size() < 1)
            return "";
        long timeStamp = coordinates.get(0).getTimestamp() * 1000;
        Date date = new Date(timeStamp);
        DateFormat format = new SimpleDateFormat("HH:mm");
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeDouble(maxSpeed);
        dest.writeDouble(avgSpeed);
        dest.writeLong(duration);
        dest.writeDouble(length);
        dest.writeInt(year);
        dest.writeInt(day);
        if (coordinates == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(coordinates);
        }
    }

    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };


}
