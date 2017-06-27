package com.erpam.mert.maptrackanimator.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Utility functions
 */
public class Utils {

    public static UtilSpeed speedUnit = UtilSpeed.UNIT_KMH;

    /**
     * Returns the altitude unit
     *
     * @return altitude unit
     */
    public static String getAltitudeUnit() {
        if (speedUnit == UtilSpeed.UNIT_KMH)
            return "meter";
        else
            return "feet";
    }

    /**
     * Returns the formatted date
     *
     * @param formatString format string
     * @param year         year
     * @param day          day
     * @return string representation of formatted date
     */
    public static String getDate(String formatString, int year, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_YEAR, day);
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(calendar.getTime());
    }

    /**
     * Returns the distance unit
     *
     * @return distance unit
     */
    public static String getDistanceUnit() {
        if (speedUnit == UtilSpeed.UNIT_KMH)
            return "km";
        else
            return "mi";
    }

    /**
     * Returns the speed unit
     *
     * @return speed unit
     */
    public static String getSpeedUnit() {
        if (speedUnit == UtilSpeed.UNIT_KMH)
            return "km/h";
        else
            return "mph";
    }

    /**
     * Converts altitude from meter to feet if necessary and returns it
     *
     * @param altitude input altitude
     * @return (converted) altitude
     */
    public static String makeAltitudeUnitConversion(int altitude) {
        if (speedUnit == UtilSpeed.UNIT_MPH) {
            altitude *= 3.28084;
        }
        return Integer.toString(altitude) + " ";
    }

    /**
     * Converts speed from km/h to mi/h if necessary and returns it
     *
     * @param speed input speed
     * @return (converted) speed
     */
    public static String makeUnitConversion(double speed) {
        if (speedUnit == UtilSpeed.UNIT_MPH) {
            speed = speed * 0.621371;
        }
        speed = (double) ((int) (speed * 10)) / 10;
        return Double.toString(speed) + " ";
    }

    public enum UtilSpeed {UNIT_KMH, UNIT_MPH}
}
