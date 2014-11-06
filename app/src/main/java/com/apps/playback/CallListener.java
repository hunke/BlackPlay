package com.apps.playback;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallListener extends PhoneStateListener {
    PlayerServiceHandler playerServiceHandler = PlayerServiceHandler.getInstance();

    public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (playerServiceHandler.isOnCall()&&!playerServiceHandler.isForcePaused()){
                        playerServiceHandler.setOnCall(false);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    playerServiceHandler.setOnCall(true);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    playerServiceHandler.setOnCall(true);
                    break;
                default:
                    break;
               }
            }
    }
