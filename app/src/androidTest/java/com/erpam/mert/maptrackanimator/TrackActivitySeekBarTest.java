package com.erpam.mert.maptrackanimator;


import android.app.Activity;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.view.View;
import android.widget.SeekBar;

import com.erpam.mert.maptrackanimator.activity.MainActivity;
import com.erpam.mert.maptrackanimator.activity.TrackActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TrackActivitySeekBarTest {

    private Activity currentActivity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testSeekBarFlow() {
        try {
            onView(withId(R.id.btnMapAnimator)).perform(click());

            (onView(withId(R.id.seekBar_layout))).check(new ViewAssertion() {
                @Override
                public void check(View view, NoMatchingViewException noViewFoundException) {
                    assertEquals(0, ((SeekBar) view).getProgress());
                }
            });

            onView(withId(R.id.play_laplayout)).perform(click());

            sleep(5000);

            (onView(withId(R.id.seekBar_layout))).check(new ViewAssertion() {
                @Override
                public void check(View view, NoMatchingViewException noViewFoundException) {
                    assertNotEquals(0, ((SeekBar) view).getProgress());
                }
            });

            sleep(2500);

            (onView(withId(R.id.seekBar_layout))).check(new ViewAssertion() {
                @Override
                public void check(View view, NoMatchingViewException noViewFoundException) {
                    assertEquals(100, ((SeekBar) view).getProgress());
                }
            });

            (onView(withId(R.id.play_laplayout))).perform(click());
            (onView(withId(R.id.seekBar_layout))).check(new ViewAssertion() {
                @Override
                public void check(View view, NoMatchingViewException noViewFoundException) {
                    if(!(((SeekBar) view).getProgress() > 0 && ((SeekBar) view).getProgress() < 100)) {
                        assertEquals(0, -1);
                    }
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
