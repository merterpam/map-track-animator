package com.erpam.mert.maptrackanimator.helper;

import android.os.Parcel;
import android.os.Parcelable;

import com.erpam.mert.maptrackanimator.model.Coordinate;
import com.erpam.mert.maptrackanimator.model.Track;

/**
 * Helper class for track animation
 */
public class TrackAnimationHelper implements Parcelable {

    private Track track;
    private long firstCoordinate;
    private long lapDuration;

    private long currentTime;
    private boolean animatable;

    public TrackAnimationHelper(Track track) {

        this.track = track;
        track.calculcateStatistics();
        currentTime = 0;
        animatable = true;

        if (track != null && track.getCoordinates().size() > 1) {
            long lapLast = track.getCoordinates().get(track.getCoordinates().size() - 1).getTimestamp();
            long lapFirst = track.getCoordinates().get(0).getTimestamp();
            firstCoordinate = lapFirst;
            lapDuration = lapLast - lapFirst;
        } else {
            animatable = false;
        }
    }

    protected TrackAnimationHelper(Parcel in) {
        animatable = in.readByte() != 0x00;
        currentTime = in.readLong();
        firstCoordinate = in.readLong();
        track = (Track) in.readValue(Track.class.getClassLoader());
        lapDuration = in.readLong();
    }

    public Track getTrack() {
        return track;
    }

    public boolean isAnimatable() {
        return animatable;
    }

    public boolean isAtEnd() {
        return currentTime == lapDuration;
    }

    public void resetTime() {
        currentTime = 0;
    }

    /**
     * Maps current time to a scale
     *
     * @param range maximum range
     * @return current position in the range
     */
    public int mapTimeToScale(int range) {

        double ratio = ((double) currentTime) / lapDuration;

        return (int) (ratio * range);
    }

    /**
     * Maps current scale to time
     *
     * @param index current position in the range
     * @param range maximum range
     */
    public void mapScaleToTime(int index, int range) {

        double ratio = ((double) index) / range;

        currentTime = (long) (ratio * lapDuration);
    }

    /**
     * Increases current time
     *
     * @param deltaTime time amount to be increased
     */
    public void increaseCurrentTime(long deltaTime) {
        currentTime += deltaTime;
        if (currentTime > lapDuration)
            currentTime = lapDuration;
    }

    /**
     * Returns speed at the current time
     * If the animation is at the beginning or at the end, returns 0.0
     *
     * @return current speed
     */
    public double getSpeed() {
        double speed;
        if (currentTime == 0 || currentTime == lapDuration)
            speed = 0.0;
        else {
            int index = getIntervalIndex(track);
            speed = track.getSpeedAtCoordinate(index);
        }

        return speed;
    }

    /**
     * Returns the coordinate at the current time
     * @return current coordinate
     */
    public Coordinate getInterpolatedCoordinate() {

        Coordinate coordinate;
        int index = getIntervalIndex(track);
        coordinate = getCoordinateInBetween(track, index);

        return coordinate;
    }

    /**
     * Returns the closest coordinate which is greater than the current coordinate
     * @param track the track which contains the coordinates
     * @return closest greater coordinate to the current coordinate
     */
    private int getIntervalIndex(Track track) {
        int index = -1;
        for (int i = 1; i < track.getCoordinates().size(); i++) {
            if (currentTime <= getTimeInIndex(track, i)) {
                index = i;
                break;
            }
        }

        return index;
    }

    /**
     * Returns the time elapsed from the start of the track when i'th coordinate is recorded
     * @param track the track which contains the coordinates
     * @param i the index of the coordinate
     * @return time elapsed from the start of the track
     */
    private long getTimeInIndex(Track track, int i) {

        long maxTime = 0;
        long time = track.getCoordinates().get(i).getTimestamp() - track.getCoordinates().get(0).getTimestamp();
        if (time > maxTime)
            maxTime = time;

        return maxTime;
    }

    /**
     * Returns the interpolated coordinate at the current time
     * @param track the track which contains the coordinates
     * @param index closest greater coordinate to the current coordinate
     * @return the interpolated coordinate
     */
    private Coordinate getCoordinateInBetween(Track track, int index) {

        Coordinate startCoord = track.getCoordinates().get(index - 1);
        Coordinate endCoord = track.getCoordinates().get(index);
        long duration = endCoord.getTimestamp() - startCoord.getTimestamp();
        long elapsedTime = currentTime - (startCoord.getTimestamp() - track.getCoordinates().get(0).getTimestamp());

        double ratio = ((double) elapsedTime) / duration;

        double latitude = startCoord.getLatitude() * (1 - ratio) + endCoord.getLatitude() * ratio;
        double longitude = startCoord.getLongitude() * (1 - ratio) + endCoord.getLongitude() * ratio;
        int altitude = (int) (startCoord.getAltitude() * (1 - ratio) + endCoord.getAltitude() * ratio);
        long timestamp = (long) (startCoord.getTimestamp() * (1 - ratio) + endCoord.getTimestamp() * ratio);

        Coordinate interpolatedCoordinate = new Coordinate();
        interpolatedCoordinate.setLatitude(latitude);
        interpolatedCoordinate.setLongitude(longitude);
        interpolatedCoordinate.setAltitude(altitude);
        interpolatedCoordinate.setTimestamp(timestamp);
        return interpolatedCoordinate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (animatable ? 0x01 : 0x00));
        dest.writeLong(currentTime);
        dest.writeLong(firstCoordinate);
        dest.writeValue(track);
        dest.writeLong(lapDuration);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TrackAnimationHelper> CREATOR = new Parcelable.Creator<TrackAnimationHelper>() {
        @Override
        public TrackAnimationHelper createFromParcel(Parcel in) {
            return new TrackAnimationHelper(in);
        }

        @Override
        public TrackAnimationHelper[] newArray(int size) {
            return new TrackAnimationHelper[size];
        }
    };
}
