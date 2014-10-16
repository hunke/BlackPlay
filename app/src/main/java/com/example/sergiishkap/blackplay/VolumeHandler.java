package com.example.sergiishkap.blackplay;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;

public class VolumeHandler extends ContentObserver {
    PlayerServiceHandler playerServiceHandler = PlayerServiceHandler.getInstance();
    int previousVolume;
    Context context;

    public VolumeHandler(Context c, Handler handler) {
        super(handler);
        context=c;

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume=playerServiceHandler.getVolumeIndex();

        int delta=previousVolume-currentVolume;
        if(!playerServiceHandler.isScreenOn()){
            if(delta>0)
            {
                audio.setStreamVolume(AudioManager.STREAM_MUSIC,previousVolume,0);
                int ind= playerServiceHandler.getNextSong();
                playerServiceHandler.setNextSong(ind + 1);
            }
        }
        else{
            playerServiceHandler.setVolumeIndex(currentVolume);
        }
    }
}
