package com.example.sergiishkap.blackplay;

import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

public class PlayerServiceHandler extends Observable{
    private static PlayerServiceHandler instance=null;
    public static PlayerServiceHandler getInstance(){
        if(instance==null){
            instance=new PlayerServiceHandler();
        }
        return instance;
    }

    private boolean isRepeatOn=true;
    public boolean isShuffleOn;
    public boolean isServiceStarted() {
        return serviceStarted;
    }

    public String getSongPathAndName() {
        return songPathAndName;
    }

    public void setSongPathAndName(String songPathAndName1) {
        songPathAndName = songPathAndName1;
        triggerObservers(Constants.SONG_META_CHANGED);
    }

    public String songPathAndName;
    public boolean isMpPlaying() {
        return mpPlaying;
    }

    public void setMpPlaying(boolean mpPlaying1) {
        mpPlaying = mpPlaying1;
        System.out.println("Playing is: " + mpPlaying);
        triggerObservers(Constants.PLAY_STATE_CHANGED);
    }

    private boolean mpPlaying=false;
    public void setServiceStarted(boolean serviceStarted1) {
        serviceStarted = serviceStarted1;
    }

    public boolean serviceStarted;

    public MediaPlayer getMp() {
        return mp;
    }

    public MediaPlayer setMp(MediaPlayer mp1) {
        mp = mp1;
        return mp;
    }

    public MediaPlayer mp;
    public boolean isIsRepeatOn() {
        return isRepeatOn;
    }
    public ArrayList<HashMap<String,String>> songList=ExternalMemorySelect.getSongList();
    public void setIsRepeatOn(boolean isRepeatOn1) {
        isRepeatOn = isRepeatOn1;
        triggerObservers(Constants.REPEAT_STATE_CHANGED);
    }

    public boolean isIsShuffleOn() {
        return isShuffleOn;
    }
    private void triggerObservers(int action) {
        setChanged();
        notifyObservers(action);
    }
    public void setIsShuffleOn(boolean isShuffleOn1) {
        isShuffleOn = isShuffleOn1;
        triggerObservers(Constants.SHUFFLE_STATE_CHANGED);
    }
    public int getCurrentSongPosition() {
        return currentSongPosition;
    }

    public void setCurrentSongPosition(int currentSongPosition1) {
        currentSongPosition = currentSongPosition1;
    }

    public int currentSongPosition;

    public int getSongIndex() {
        return songIndex;
    }

    public void setSongIndex(int songIndex1) {
        songIndex = songIndex1;
        triggerObservers(Constants.SONG_INDEX_CHANGED);
    }

    public int songIndex;

    public int getVolumeIndex() {
        return volumeIndex;
    }

    public void setVolumeIndex(int volumeIndex) {
        this.volumeIndex = volumeIndex;
    }

    public int volumeIndex;

    public int getNextSong() {
        return nextSong;
    }

    public void setNextSong(int nextSong) {
        this.nextSong = nextSong;
        triggerObservers(Constants.NEXT_SONG_BG);
    }

    public int nextSong;

    public boolean isScreenOn() {
        return screenOn;
    }

    public void setScreenOn(boolean screenOn) {
        this.screenOn = screenOn;
        System.out.println("ScreenStateChanged to:"+screenOn);

    }

    public boolean screenOn=true;

    public boolean isHeadPhonesPlugged() {
        return headPhonesPlugged;
    }

    public void setHeadPhonesPlugged(boolean headPhonesPlugged) {
        this.headPhonesPlugged = headPhonesPlugged;
        if(headPhonesPlugged){
            triggerObservers(Constants.HEADPHONES_UNPLUGGED);
        }else{
            triggerObservers(Constants.HEADPHONES_PLUGGED);
        }
    }

    public boolean headPhonesPlugged;

    public boolean isOnCall() {
        return onCall;
    }

    public void setOnCall(boolean onCall) {
        this.onCall = onCall;
        if(onCall){
            triggerObservers(Constants.ON_CALL);
        }else {
            triggerObservers(Constants.CALL_FINISHED);
        }
    }

    public boolean onCall;
}
