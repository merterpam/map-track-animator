package com.erpam.mert.maptrackanimator.utility;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

import com.erpam.mert.maptrackanimator.utility.Utils.UtilSpeed;

public class UtilsTest {
    @Test
    public void getAltitudeUnit() throws Exception {
        Utils.speedUnit = UtilSpeed.UNIT_KMH;
        assertEquals("meter", Utils.getAltitudeUnit());

        Utils.speedUnit = UtilSpeed.UNIT_MPH;
        assertEquals("feet", Utils.getAltitudeUnit());

    }

    @Test
    public void getDate() throws Exception {
        assertEquals("Feb 04, 2000", Utils.getDate("MMM dd, yyyy", 2000, 35));
    }

    @Test
    public void getDistanceUnit() throws Exception {
        Utils.speedUnit = UtilSpeed.UNIT_KMH;
        assertEquals("km", Utils.getDistanceUnit());

        Utils.speedUnit = UtilSpeed.UNIT_MPH;
        assertEquals("mi", Utils.getDistanceUnit());
    }

    @Test
    public void getSpeedUnit() throws Exception {
        Utils.speedUnit = UtilSpeed.UNIT_KMH;
        assertEquals("km/h", Utils.getSpeedUnit());

        Utils.speedUnit = UtilSpeed.UNIT_MPH;
        assertEquals("mph", Utils.getSpeedUnit());
    }

    @Test
    public void makeAltitudeUnitConversion() throws Exception {
        Utils.speedUnit = UtilSpeed.UNIT_KMH;
        assertEquals("-10 ", Utils.makeAltitudeUnitConversion(-10));
        assertEquals("0 ", Utils.makeAltitudeUnitConversion(0));
        assertEquals("4 ", Utils.makeAltitudeUnitConversion(4));
        assertEquals("10 ", Utils.makeAltitudeUnitConversion(10));

        Utils.speedUnit = UtilSpeed.UNIT_MPH;
        assertEquals("-32 ", Utils.makeAltitudeUnitConversion(-10));
        assertEquals("0 ", Utils.makeAltitudeUnitConversion(0));
        assertEquals("13 ", Utils.makeAltitudeUnitConversion(4));
        assertEquals("32 ", Utils.makeAltitudeUnitConversion(10));
    }

    @Test
    public void makeUnitConversion() throws Exception {
        Utils.speedUnit = UtilSpeed.UNIT_KMH;
        assertEquals("-10.3 ", Utils.makeUnitConversion(-10.324));
        assertEquals("0.0 ", Utils.makeUnitConversion(0));
        assertEquals("4.1 ", Utils.makeUnitConversion(4.1));
        assertEquals("10.0 ", Utils.makeUnitConversion(10));

        Utils.speedUnit = UtilSpeed.UNIT_MPH;
        assertEquals("-6.2 ", Utils.makeUnitConversion(-10));
        assertEquals("0.0 ", Utils.makeUnitConversion(0));
        assertEquals("2.4 ", Utils.makeUnitConversion(4));
        assertEquals("6.2 ", Utils.makeUnitConversion(10));
    }
}