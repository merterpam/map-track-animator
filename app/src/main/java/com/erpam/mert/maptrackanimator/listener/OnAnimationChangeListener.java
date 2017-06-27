package com.erpam.mert.maptrackanimator.listener;

import com.erpam.mert.maptrackanimator.helper.TrackAnimationHelper;

/**
 * Interface animation change callback
 *
 * Author: @merterpam
 */

public interface OnAnimationChangeListener {
    /**
     * Callback function for animation change event
     * @param animationHelper current instance of {@link TrackAnimationHelper} responsible for animation
     */
    void onCoordinateChange(TrackAnimationHelper animationHelper);
}
