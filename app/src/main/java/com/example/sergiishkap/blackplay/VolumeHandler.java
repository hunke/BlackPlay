package com.example.sergiishkap.blackplay;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.os.PowerManager;

public class VolumeHandler extends ContentObserver {
    PresetRepeatShuffleHandler presetRepeatShuffleHandler=PresetRepeatShuffleHandler.getInstance();
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
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

        int delta=previousVolume-currentVolume;
        if(!presetRepeatShuffleHandler.isScreenOn()){
            if(delta<0)
            {
                audio.setStreamVolume(AudioManager.STREAM_MUSIC,previousVolume,0);
                int ind=presetRepeatShuffleHandler.getNextSong();
                presetRepeatShuffleHandler.setNextSong(ind+1);
            }
        }
    }
}
