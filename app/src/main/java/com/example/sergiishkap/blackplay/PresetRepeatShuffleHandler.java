package com.example.sergiishkap.blackplay;

import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by sergii.shkap on 9/15/2014.
 */
public class PresetRepeatShuffleHandler {
    public static boolean isRepeatOn;
    public static boolean isShuffleOn;
    public static boolean isServiceStarted() {
        return serviceStarted;
    }

    public static String getSongPathAndName() {
        return songPathAndName;
    }

    public static void setSongPathAndName(String songPathAndName) {
        PresetRepeatShuffleHandler.songPathAndName = songPathAndName;
    }

    public static String songPathAndName;
    public static boolean isMpPlaying() {
        return mpPlaying;
    }

    public static void setMpPlaying(boolean mpPlaying) {
        PresetRepeatShuffleHandler.mpPlaying = mpPlaying;
    }

    public static boolean mpPlaying;
    public static void setServiceStarted(boolean serviceStarted) {
        PresetRepeatShuffleHandler.serviceStarted = serviceStarted;
    }

    public static boolean serviceStarted;

    public static MediaPlayer getMp() {
        return mp;
    }

    public static MediaPlayer setMp(MediaPlayer mp) {
        PresetRepeatShuffleHandler.mp = mp;
        return mp;
    }

    public static MediaPlayer mp;
    public static boolean isIsRepeatOn() {
        return isRepeatOn;
    }
    public static ArrayList<HashMap<String,String>> songList=ExternalMemorySelect.getSongList();
    public static void setIsRepeatOn(boolean isRepeatOn) {
        PresetRepeatShuffleHandler.isRepeatOn = isRepeatOn;
    }

    public static boolean isIsShuffleOn() {
        return isShuffleOn;
    }

    public static void setIsShuffleOn(boolean isShuffleOn) {
        PresetRepeatShuffleHandler.isShuffleOn = isShuffleOn;
    }
    public static int getCurrentSongPosition() {
        return currentSongPosition;
    }

    public static void setCurrentSongPosition(int currentSongPosition1) {
        currentSongPosition = currentSongPosition1;
    }

    public static int currentSongPosition;

    public static int getSongIndex() {
        return songIndex;
    }

    public static void setSongIndex(int songIndex1) {
        songIndex = songIndex1;
    }

    public static int songIndex;
}
