package com.erpam.mert.maptrackanimator.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erpam.mert.maptrackanimator.R;
import com.erpam.mert.maptrackanimator.activity.TrackActivity;
import com.erpam.mert.maptrackanimator.helper.TrackAnimationHelper;
import com.erpam.mert.maptrackanimator.listener.OnAnimationChangeListener;
import com.erpam.mert.maptrackanimator.model.Coordinate;
import com.erpam.mert.maptrackanimator.model.Track;
import com.erpam.mert.maptrackanimator.utility.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;


/**
 * A {@link Fragment} which contains two graphs.
 * Implements [@link OnLapFragmentListener} to receive animation changes from {@link TrackActivity}
 * Use the {@link GraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class GraphFragment extends Fragment implements OnAnimationChangeListener {

    private static final String ANIMATION_PARAM = "animationHelper";

    private Track mTrack;
    private LineChart speedChart;
    private LineChart altitudeChart;
    private MarkerView mMarker;
    private List<Entry> altitudeEntries = new ArrayList<>();
    private List<Entry> speedEntries = new ArrayList<>();

    private TextView speedText;
    private TextView altitudeText;

    private Coordinate currentCoordinate;
    private double currentSpeed;

    private long firstTimestamp;

    public GraphFragment() {
    }

    /**
     * Creates a new instace of {@link GraphFragment}
     *
     * @param animationHelper the controller of Track animation
     * @return a new instance of {@link GraphFragment}
     */
    public static GraphFragment newInstance(TrackAnimationHelper animationHelper) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putParcelable(ANIMATION_PARAM, animationHelper);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            TrackAnimationHelper animationHelper = getArguments().getParcelable(ANIMATION_PARAM);
            mTrack = animationHelper.getTrack();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getContext().getResources().getDisplayMetrics());
        mMarker = new MarkerView(getContext(), R.layout.layout_dot);
        mMarker.setOffset(-size / 2, -size / 2);

        firstTimestamp = mTrack.getCoordinates().get(0).getTimestamp();
        speedChart = (LineChart) view.findViewById(R.id.speed_chart);
        altitudeChart = (LineChart) view.findViewById(R.id.altitude_chart);

        speedText = (TextView) view.findViewById(R.id.tvSpeedValue);
        altitudeText = (TextView) view.findViewById(R.id.tvAltitudeValue);

        populateEntries();

        speedChart.getAxisLeft().setAxisMinimum(0);
        setData(speedChart, "Speed", speedEntries);

        setData(altitudeChart, "Altitude", altitudeEntries);

        updateCharts();

        return view;
    }

    @Override
    public void onCoordinateChange(TrackAnimationHelper animationHelper) {
        currentCoordinate = animationHelper.getInterpolatedCoordinate();
        currentSpeed = animationHelper.getSpeed();

        updateCharts();
    }

    /**
     * Populates {@link GraphFragment#altitudeEntries}, {@link GraphFragment#speedEntries}, {@link GraphFragment#currentCoordinate} and {@link GraphFragment#currentSpeed}
     */
    private void populateEntries() {
        mTrack.calculcateStatistics();

        if (altitudeEntries.size() == 0) {
            for (Coordinate coor : mTrack.getCoordinates()) {

                altitudeEntries.add(new Entry(coor.getTimestamp() - firstTimestamp, coor.getAltitude()));
            }
        }

        if (speedEntries.size() == 0) {
            for (int i = 1; i < mTrack.getCoordinates().size(); i++) {
                Coordinate end = mTrack.getCoordinates().get(i);
                long time = (end.getTimestamp() - firstTimestamp);
                speedEntries.add(new Entry(time, (float) (double) mTrack.getSpeedAtCoordinate(i)));
            }
        }

        if (currentCoordinate == null) {
            currentCoordinate = mTrack.getCoordinates().get(0);
            currentSpeed = 0;
        }
    }

    /**
     * Specializes the chart
     *
     * @param chart   Chart to be specialized
     * @param label   Label of the chart
     * @param entries Entries of the chart
     */
    private void setData(LineChart chart, String label, List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, label);

        dataSet.setDrawFilled(false);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setColor(ContextCompat.getColor(getContext(), R.color.orangeTint));
        dataSet.setLineWidth(2.5f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawHighlightIndicators(false);

        LineData data = new LineData(dataSet);

        chart.setData(data);
        chart.setScaleEnabled(false);

        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.getLegend().setEnabled(false);
        chart.setMarker(mMarker);
        chart.setTouchEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setYOffset(16.0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(5, true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format("%.1f", value / 60);
            }
        });

        chart.setExtraBottomOffset(8.0f);

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setLabelCount(4, true);
        chart.getAxisLeft().setXOffset(8.0f);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format("%.0f", value);
            }
        });

        int textColor = ContextCompat.getColor(getContext(), R.color.textColor);

        xAxis.setTextColor(textColor);
        chart.getAxisLeft().setTextColor(textColor);

        chart.invalidate();
    }

    /**
     * Updates the graphs according to the current position of animation
     */
    private void updateCharts() {
        if (mTrack != null) {
            long time = currentCoordinate.getTimestamp() - mTrack.getCoordinates().get(0).getTimestamp();
            speedChart.highlightValue(time, 0);
            speedChart.invalidate();

            altitudeChart.highlightValue(time, 0);
            altitudeChart.invalidate();

            StringBuilder speedBuilder = new StringBuilder();
            speedBuilder.append(Utils.makeUnitConversion(currentSpeed))
                    .append(Utils.getSpeedUnit())
                    .append(" @ ")
                    .append(String.format("%.2f", (float) time / 60))
                    .append(" min");
            speedText.setText(speedBuilder.toString());

            StringBuilder altitudeBuilder = new StringBuilder();
            altitudeBuilder.append(Utils.makeAltitudeUnitConversion(currentCoordinate.getAltitude()))
                    .append(Utils.getAltitudeUnit())
                    .append(" @ ")
                    .append(String.format("%.2f", (float) time / 60))
                    .append(" min");
            altitudeText.setText(altitudeBuilder.toString());
        }
    }
}
