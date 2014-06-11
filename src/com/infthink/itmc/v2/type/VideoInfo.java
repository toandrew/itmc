package com.infthink.itmc.v2.type;

import android.graphics.drawable.Drawable;

public class VideoInfo {
    private static int MAX_FAILED_COUNT = 3;
    private int failedCount = 0;
    public String html5Page;
    public boolean localVideo;
    public String playSeconds;
    public Drawable thumbDrawable;
    public String vId;
    public String videoUri;

    public synchronized boolean isFailed() {
        try {
            if (failedCount >= MAX_FAILED_COUNT - 1) {
                return true;
            }
        } catch(Exception ex) {
            
        }
        return false;
    }

    public synchronized void setFailedOnce() {
        try {
            failedCount++;
        } catch (Exception ex) {
        }
    }
}
