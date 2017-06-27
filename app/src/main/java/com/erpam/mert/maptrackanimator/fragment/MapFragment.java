package com.erpam.mert.maptrackanimator.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erpam.mert.maptrackanimator.helper.TrackAnimationHelper;
import com.erpam.mert.maptrackanimator.listener.OnAnimationChangeListener;
import com.erpam.mert.maptrackanimator.model.Coordinate;
import com.erpam.mert.maptrackanimator.R;
import com.erpam.mert.maptrackanimator.model.Track;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback, OnAnimationChangeListener {

    private static final String ANIMATION_PARAM = "animationHelper";

    private TrackAnimationHelper animationHelper;
    private Marker marker;
    private Polyline polyline;
    private BitmapDescriptor markerIconBitmap;

    private GoogleMap mMap;
    private LatLngBounds.Builder bounds;

    public MapFragment() {
    }

    /**
     * Creates a new instace of {@link MapFragment}
     *
     * @param animationHelper the controller of Track animation
     * @return a new instance of {@link MapFragment}
     */
    public static MapFragment newInstance(TrackAnimationHelper animationHelper) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelable(ANIMATION_PARAM, animationHelper);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCoordinateChange(TrackAnimationHelper animationHelper) {
        buildMarker();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            animationHelper = getArguments().getParcelable(ANIMATION_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.map_marker);
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        markerIconBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), size, size, false));

        getMapAsync(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Called when the map is ready. It zooms the map to the location of the track, draws the path and marker
     *
     * @param googleMap active map instance
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_silvermap));

        bounds = new LatLngBounds.Builder();

        Track track = animationHelper.getTrack();
        ArrayList<LatLng> latLngCoordinates = new ArrayList<>();
        for (Coordinate coord : track.getCoordinates()) {
            LatLng loc = new LatLng(coord.getLatitude(), coord.getLongitude());
            latLngCoordinates.add(loc);
            bounds.include(loc);
        }

        buildPath(latLngCoordinates);

        marker = null;
        buildMarker();

        // Calculate height of the map
        // 60 px = Sliding Layout
        // Manual calculation is necessary because newLatLngBounds with no size parameters require that both map and view are ready
        // Which may not be the case here
        int paddingVerticalPx = 0;
        paddingVerticalPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getActivity().getResources().getDisplayMetrics());
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels - paddingVerticalPx;
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), width, height, 80));

        float bearing = calculateBearing(track);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(mMap.getCameraPosition().target)
                .zoom(mMap.getCameraPosition().zoom)
                .bearing(bearing)
                .build()));
    }

    /**
     * Builds the marker based on {@link MapFragment#animationHelper}
     */
    private void buildMarker() {
        Coordinate coordinate = animationHelper.getInterpolatedCoordinate();
        if (coordinate == null) {
            List<Coordinate> coordinates = animationHelper.getTrack().getCoordinates();
            coordinate = coordinates.get(coordinates.size() - 1);
        }
        LatLng loc = new LatLng(coordinate.getLatitude(), coordinate.getLongitude());

        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .icon(markerIconBitmap)
                    .anchor(0.5f, 0.5f)
                    .draggable(false));
        } else
            marker.setPosition(loc);
    }

    /**
     * Build the path
     *
     * @param latLngCoordinates Coordinates of the path
     */
    private void buildPath(ArrayList<LatLng> latLngCoordinates) {

        if (polyline != null)
            polyline.remove();

        polyline = mMap.addPolyline(new PolylineOptions()
                .addAll(latLngCoordinates)
                .width(10)
                .jointType(JointType.ROUND)
                .color(ContextCompat.getColor(getContext(), R.color.orangeTint)));
    }

    /**
     * Calculates the bearing of the track based on the first and last coordinates
     *
     * @param track Track to be calculated
     * @return Degree deviation of track from north
     */
    private float calculateBearing(Track track) {
        Location loc1 = new Location("");
        loc1.setLatitude(track.getCoordinates().get(0).getLatitude());
        loc1.setLongitude(track.getCoordinates().get(0).getLongitude());

        Location loc2 = new Location("");
        loc2.setLatitude(track.getCoordinates().get(track.getCoordinates().size() - 1).getLatitude());
        loc2.setLongitude(track.getCoordinates().get(track.getCoordinates().size() - 1).getLongitude());

        return loc2.bearingTo(loc1);
    }
}