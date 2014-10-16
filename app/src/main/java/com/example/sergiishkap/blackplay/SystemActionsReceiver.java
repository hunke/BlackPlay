package com.example.sergiishkap.blackplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SystemActionsReceiver extends BroadcastReceiver {
    PlayerServiceHandler playerServiceHandler = PlayerServiceHandler.getInstance();
    public static boolean isScreenOn = true;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            isScreenOn=false;
            playerServiceHandler.setScreenOn(false);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            isScreenOn=true;
            playerServiceHandler.setScreenOn(true);
        }else if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)){
            int state = intent.getIntExtra("state", -1);
            switch (state){
                case 0:
                    playerServiceHandler.setHeadPhonesPlugged(false);
                    break;
                case 1:
                    playerServiceHandler.setHeadPhonesPlugged(true);
                    break;
                default:
                    break;
            }
        }

    }
}
