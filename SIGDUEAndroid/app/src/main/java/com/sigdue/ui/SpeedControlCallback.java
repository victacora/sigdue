package com.sigdue.ui;

import android.util.Log;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.sigdue.ui.MoviePlayer.FrameCallback;

public class SpeedControlCallback implements FrameCallback {
    private static final boolean CHECK_SLEEP_TIME = false;
    private static final long ONE_MILLION = 1000000;
    public static String TAG= "SControlCallback";
    private long mFixedFrameDurationUsec;
    private boolean mLoopReset;
    private long mPrevMonoUsec;
    private long mPrevPresentUsec;

    public void setFixedPlaybackRate(int fps) {
        this.mFixedFrameDurationUsec = ONE_MILLION / ((long) fps);
    }

    public void preRender(long presentationTimeUsec) {
        if (this.mPrevMonoUsec == 0) {
            this.mPrevMonoUsec = System.nanoTime() / 1000;
            this.mPrevPresentUsec = presentationTimeUsec;
            return;
        }
        long frameDelta;
        if (this.mLoopReset) {
            this.mPrevPresentUsec = presentationTimeUsec - 33333;
            this.mLoopReset = CHECK_SLEEP_TIME;
        }
        if (this.mFixedFrameDurationUsec != 0) {
            frameDelta = this.mFixedFrameDurationUsec;
        } else {
            frameDelta = presentationTimeUsec - this.mPrevPresentUsec;
        }
        if (frameDelta < 0) {
            Log.w(TAG, "Weird, video times went backward");
            frameDelta = 0;
        } else if (frameDelta == 0) {
            Log.i(TAG, "Warning: current frame and previous frame had same timestamp");
        } else if (frameDelta > 10000000) {
            Log.i(TAG, "Inter-frame pause was " + (frameDelta / ONE_MILLION) + "sec, capping at 5 sec");
            frameDelta = 5000000;
        }
        long desiredUsec = this.mPrevMonoUsec + frameDelta;
        for (long nowUsec = System.nanoTime() / 1000; nowUsec < desiredUsec - 100; nowUsec = System.nanoTime() / 1000) {
            long sleepTimeUsec = desiredUsec - nowUsec;
            if (sleepTimeUsec > 500000) {
                sleepTimeUsec = 500000;
            }
            try {
                Thread.sleep(sleepTimeUsec / 1000, ((int) (sleepTimeUsec % 1000)) * ExtensionData.MAX_EXPANDED_BODY_LENGTH);
            } catch (InterruptedException e) {
            }
        }
        this.mPrevMonoUsec += frameDelta;
        this.mPrevPresentUsec += frameDelta;
    }

    public void postRender() {
    }

    public void loopReset() {
        this.mLoopReset = true;
    }
}
