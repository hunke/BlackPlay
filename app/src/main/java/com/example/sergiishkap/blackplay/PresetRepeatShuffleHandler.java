package com.example.sergiishkap.blackplay;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sergii.shkap on 9/15/2014.
 */
public class PresetRepeatShuffleHandler {
    public static boolean isRepeatOn;
    public static boolean isShuffleOn;
    public static String preset;

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

    public static String getPreset() {
        return preset;
    }

    public static void setPreset(String preset) {
        PresetRepeatShuffleHandler.preset = preset;
    }

}
