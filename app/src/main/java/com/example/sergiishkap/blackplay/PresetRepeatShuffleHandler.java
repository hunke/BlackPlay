package com.example.sergiishkap.blackplay;

import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;
import java.util.Random;

/**
 * Created by sergii.shkap on 9/15/2014.
 */
public class PresetRepeatShuffleHandler {
    private static PresetRepeatShuffleHandler instance=null;
    public static PresetRepeatShuffleHandler getInstance(){
        if(instance==null){
            instance=new PresetRepeatShuffleHandler();
        }
        return instance;
    }
    protected PresetRepeatShuffleHandler() {
        // Exists only to defeat instantiation.
    }
    private boolean isRepeatOn;
    public boolean isShuffleOn;
    public boolean isServiceStarted() {
        return serviceStarted;
    }

    public String getSongPathAndName() {
        return songPathAndName;
    }

    public void setSongPathAndName(String songPathAndName1) {
        songPathAndName = songPathAndName1;
    }

    public String songPathAndName;
    public boolean isMpPlaying() {
        return mpPlaying;
    }

    public void setMpPlaying(boolean mpPlaying1) {
        mpPlaying = mpPlaying1;
    }

    private boolean mpPlaying;
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
    }

    public boolean isIsShuffleOn() {
        return isShuffleOn;
    }

    public void setIsShuffleOn(boolean isShuffleOn1) {
        isShuffleOn = isShuffleOn1;
    }
    public int getCurrentSongPosition() {
        return currentSongPosition;
    }

    public void setCurrentSongPosition(int currentSongPosition1) {
        currentSongPosition = currentSongPosition1;
    }

    public boolean isPlayerInitialized() {
        return playerInitialized;
    }

    public void setPlayerInitialized(boolean playerInitialized1) {
        playerInitialized = playerInitialized1;
    }

    public boolean playerInitialized;
    public int currentSongPosition;

    public int getSongIndex() {
        return songIndex;
    }

    public void setSongIndex(int songIndex1) {
        songIndex = songIndex1;
    }

    public int songIndex;
}
