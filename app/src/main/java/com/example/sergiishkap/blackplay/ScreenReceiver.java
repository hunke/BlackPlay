package com.example.sergiishkap.blackplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by sergii.shkap on 10/16/2014.
 */
public class ScreenReceiver extends BroadcastReceiver {
    PresetRepeatShuffleHandler presetRepeatShuffleHandler=PresetRepeatShuffleHandler.getInstance();
    public static boolean isScreenOn = true;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            presetRepeatShuffleHandler.setScreenOn(false);
            isScreenOn=false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            presetRepeatShuffleHandler.setScreenOn(true);
            isScreenOn=true;
        }
    }
}
