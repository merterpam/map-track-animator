package com.erpam.mert.maptrackanimator.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.erpam.mert.maptrackanimator.R;
import com.erpam.mert.maptrackanimator.model.Coordinate;
import com.erpam.mert.maptrackanimator.model.Track;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
    }

    public void btnMapOnClick(View v) {
        Intent intent = new Intent(MainActivity.this, TrackActivity.class);
        intent.putExtra(TrackActivity.LAP_TAG, buildLap());
        startActivity(intent);
    }

    private Track buildLap() {
        Track track = new Track();

        ArrayList<Coordinate> coordinates = new ArrayList<>();

        double longitude = -73.997156;
        double latitude = 40.722763;
        for (int i = 0; i < 30; i++) {
            longitude += (i % 10) * 0.0001;
            latitude += (i % 5) * 0.0001;

            Coordinate coordinate = new Coordinate(longitude, latitude, System.currentTimeMillis() / 1000 + i * 2);
            coordinate.setAltitude(200 - i * 3);

            coordinates.add(coordinate);
        }
        track.setCoordinates(coordinates);
        track.calculcateStatistics();
        return track;
    }
}
