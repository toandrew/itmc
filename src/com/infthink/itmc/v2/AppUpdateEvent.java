package com.infthink.itmc.v2;

public class AppUpdateEvent {

    private boolean mShowChecking;

    public AppUpdateEvent(boolean showChecking) {
        mShowChecking = showChecking;
    }

    public boolean isShowChecking() {
        return mShowChecking;
    }

}
