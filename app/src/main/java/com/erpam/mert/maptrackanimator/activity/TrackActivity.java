package com.erpam.mert.maptrackanimator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.erpam.mert.maptrackanimator.R;
import com.erpam.mert.maptrackanimator.databinding.SlidingLaplayoutBinding;
import com.erpam.mert.maptrackanimator.fragment.GraphFragment;
import com.erpam.mert.maptrackanimator.fragment.MapFragment;
import com.erpam.mert.maptrackanimator.helper.TrackAnimationHelper;
import com.erpam.mert.maptrackanimator.listener.OnAnimationChangeListener;
import com.erpam.mert.maptrackanimator.model.Track;
import com.erpam.mert.maptrackanimator.utility.Utils;

/**
 * Track activity for map animation, contains a slide-up layout and a fragment holder
 * Contains controls for track animation
 */
public class TrackActivity extends AppCompatActivity {

    public static String LAP_TAG = "mLap";

    private TrackAnimationHelper animationHelper;
    private GraphFragment graphFragment;
    private boolean isPlaying = false;
    private MapFragment mapFragment;
    private OnAnimationChangeListener mFragmentListener;
    private Handler mHandler;
    private ImageView playButton;
    private SeekBar seekBar;
    private TextView speedTextView;
    private int timeMultiplier = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lap);

        Intent intent = getIntent();
        Track mTrack = intent.getParcelableExtra(LAP_TAG);

        animationHelper = new TrackAnimationHelper(mTrack);
        mHandler = new Handler();

        seekBar = (SeekBar) findViewById(R.id.seekBar_layout);
        speedTextView = (TextView) findViewById(R.id.speed_textview_lap);
        playButton = (ImageView) findViewById(R.id.play_laplayout);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    animationHelper.mapScaleToTime(progress, seekBar.getMax());
                    speedTextView.setText(" " + Utils.makeUnitConversion(animationHelper.getSpeed()) + Utils.getSpeedUnit() + " ");
                    mFragmentListener.onCoordinateChange(animationHelper);
                    stopPlaying();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SlidingLaplayoutBinding binding = SlidingLaplayoutBinding.bind(findViewById(R.id.handle_laplayout));
        binding.setLap(mTrack);

        final ImageView fragmentButton = (ImageView) findViewById(R.id.fragmentButton_laplayout);
        if (!animationHelper.isAnimatable()) {
            seekBar.setClickable(false);
            playButton.setClickable(false);
            fragmentButton.setClickable(false);
        }

        mapFragment = MapFragment.newInstance(animationHelper);
        graphFragment = GraphFragment.newInstance(animationHelper);

        if (mFragmentListener == null || mFragmentListener instanceof MapFragment) {
            replaceFragment(mapFragment);
        } else {
            replaceFragment(graphFragment);
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    /**
     * onClick event for fragment button
     * Makes the transition between {@link GraphFragment} and {@link MapFragment}
     *
     * @param v the view of fragment button
     */
    public void fragmentButtonOnClick(View v) {
        if (mFragmentListener instanceof MapFragment) {
            replaceFragment(graphFragment);
            speedTextView.setVisibility(View.INVISIBLE);
            mFragmentListener = graphFragment;
        } else {
            replaceFragment(mapFragment);
            speedTextView.setVisibility(View.VISIBLE);
            mFragmentListener = mapFragment;
        }

        mFragmentListener.onCoordinateChange(animationHelper);
    }

    /**
     * Starts/Stops the track animation
     *
     * @param v the view of play ubtton
     */
    public void playButtonOnClick(View v) {
        if (isPlaying)
            stopPlaying();
        else
            startPlaying();
    }

    /**
     * Replaces the current fragment with the new fragment
     *
     * @param newFragment the new fragment to be displayed, should implement {@link OnAnimationChangeListener}
     */
    private void replaceFragment(Fragment newFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.lapFragment, newFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

        mFragmentListener = (OnAnimationChangeListener) newFragment;
    }

    /**
     * Starts the track animation at {@link GraphFragment}/{@link MapFragment}
     */
    private void startPlaying() {
        playButton.setImageResource(R.drawable.pause);
        isPlaying = true;

        if (animationHelper.isAtEnd()) {
            animationHelper.resetTime();
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (isPlaying) {
                    int progress = animationHelper.mapTimeToScale(seekBar.getMax());
                    seekBar.setProgress(progress);
                    speedTextView.setText(" " + Utils.makeUnitConversion(animationHelper.getSpeed()) + Utils.getSpeedUnit() + " ");
                    mFragmentListener.onCoordinateChange(animationHelper);

                    if (animationHelper.isAtEnd()) {
                        stopPlaying();
                    }

                    animationHelper.increaseCurrentTime(250 * timeMultiplier / 1000);
                    mHandler.postDelayed(this, 250);
                }
            }
        });
    }

    /**
     * Stops the track animation at {@link GraphFragment}/{@link MapFragment}
     */
    private void stopPlaying() {
        playButton.setImageResource(R.drawable.play);
        isPlaying = false;
    }
}