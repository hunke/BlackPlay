package com.example.sergiishkap.blackplay;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class PlayerService extends IntentService implements MediaPlayer.OnCompletionListener {
    MediaPlayer mp;
    public ArrayList<HashMap<String,String>> songList=PresetRepeatShuffleHandler.songList;
    public void randoMizeSongList(ArrayList<HashMap<String,String>> randomizedSongList){
        long seed=System.nanoTime();
        Collections.shuffle(randomizedSongList, new Random(seed));
    }
    public String getSelectedFileByLocation(int i){
        String filename=songList.get(i).get("fileName");
        String filePath=songList.get(i).get("path");
        String fileLocation=filePath+"/"+filename;
        return fileLocation;
    }

    public PlayerService(){
        super("PlayerService");
    }
    protected void onHandleIntent(Intent intent) {
        int action=intent.getIntExtra(Constants.ACTION,Constants.DEFAULT_ACTION);
        int songIndex=intent.getIntExtra(Constants.SONG_INDEX,Constants.NO_SONG_SELECTED);
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags,int startId){
        if(null==mp){
            initializeMediaPlayer();
        }
        int actionName=intent.getIntExtra(Constants.ACTION, 0);
        int songIndex=intent.getIntExtra(Constants.SONG_INDEX,Constants.NO_SONG_SELECTED);
        startNewSong(songIndex);
        PresetRepeatShuffleHandler.setSongIndex(songIndex);
        switch (actionName){
            case Constants.PLAY_PAUSE:
                System.out.println("Play/PauseWorks");
                changePlayPause();
               break;
            case Constants.NEXT_SONG:
                System.out.println("NextSongWorks!");
                nextTrack();
                break;
            case Constants.PREVIOUS_SONG:
                System.out.println("PrSongWorks!");
                previousTrack();
                break;
            case Constants.REPEAT:
                changeRepeat();
                System.out.println("RepeatWorks!");
                break;
            case Constants.SHUFFLE:
                System.out.println("ShuffleWorks!");
                changeShuffle();
                break;
            default:
                System.out.println("Doesn't work!");
                break;
        }
        PresetRepeatShuffleHandler.setServiceStarted(true);
        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public void onDestroy() {
        if(null!=mp){
            mp.release();
        }
    }
    public void resetMediaPlayer(){
        mp.reset();
    }
    public void initializeMediaPlayer(){
        mp=new MediaPlayer();
    }
    public void resumePlaying(){
        mp.start();
        PresetRepeatShuffleHandler.setMpPlaying(true);

    }
    public void pausePlaying(){
        mp.pause();
        PresetRepeatShuffleHandler.setMpPlaying(false);
    }
    public void startNewSong(int i){
        if(i!=Constants.NO_SONG_SELECTED){
            try{
                mp.setDataSource(getSelectedFileByLocation(i));
                mp.prepare();
                mp.start();
                PresetRepeatShuffleHandler.setMpPlaying(true);
                PresetRepeatShuffleHandler.setCurrentSongPosition(i);
                mp.setOnCompletionListener(this);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void onCompletion(MediaPlayer arg0) {
        nextTrack();
    }
    public void changeRepeat(){
        if(PresetRepeatShuffleHandler.isIsRepeatOn()){
            PresetRepeatShuffleHandler.setIsRepeatOn(false);
        }else {
            PresetRepeatShuffleHandler.setIsRepeatOn(true);
        }
    }
    public void changeShuffle(){
        if(PresetRepeatShuffleHandler.isIsShuffleOn()){
            PresetRepeatShuffleHandler.setIsShuffleOn(false);
            songList.clear();
            songList=ExternalMemorySelect.getSongList();
        }else {
            PresetRepeatShuffleHandler.setIsShuffleOn(true);
            songList=PresetRepeatShuffleHandler.songList;
            randoMizeSongList(songList);
        }
    }
    public void changePlayPause(){
        if(mp.isPlaying()){
            pausePlaying();
        }
        else if(PresetRepeatShuffleHandler.getSongIndex()==Constants.NO_SONG_SELECTED){
            startNewSong(0);
        }
        else {
            resumePlaying();
        }
    }
    public void nextTrack(){
        int songListSize=songList.size();

        if(!PresetRepeatShuffleHandler.isIsRepeatOn()&&PresetRepeatShuffleHandler.getCurrentSongPosition()==songListSize-1){
            PresetRepeatShuffleHandler.setSongIndex(Constants.NO_SONG_SELECTED);
        }
        else if(PresetRepeatShuffleHandler.isIsRepeatOn()&&PresetRepeatShuffleHandler.getCurrentSongPosition()==songListSize-1){
            PresetRepeatShuffleHandler.setSongIndex(0);
            PresetRepeatShuffleHandler.setSongPathAndName(getSelectedFileByLocation(PresetRepeatShuffleHandler.getSongIndex()));
        }
        else{
            PresetRepeatShuffleHandler.setSongIndex(PresetRepeatShuffleHandler.getCurrentSongPosition()+1);
            PresetRepeatShuffleHandler.setSongPathAndName(getSelectedFileByLocation(PresetRepeatShuffleHandler.getSongIndex()));
        }
        startNewSong(PresetRepeatShuffleHandler.getSongIndex());
    }
    public void previousTrack(){
        int songListSize=songList.size();
        int index= PresetRepeatShuffleHandler.getSongIndex();

        if(PresetRepeatShuffleHandler.isIsRepeatOn()&&index==0){
            PresetRepeatShuffleHandler.setSongIndex(songListSize - 1);
            PresetRepeatShuffleHandler.setSongPathAndName(getSelectedFileByLocation(PresetRepeatShuffleHandler.getSongIndex()));
        }
        else if(!PresetRepeatShuffleHandler.isIsRepeatOn()&&index==0){
            resetMediaPlayer();
            PresetRepeatShuffleHandler.setMpPlaying(false);
        }
        else{
            PresetRepeatShuffleHandler.setSongIndex(index-1);
            PresetRepeatShuffleHandler.setSongPathAndName(getSelectedFileByLocation(PresetRepeatShuffleHandler.getSongIndex()));
        }
        startNewSong(PresetRepeatShuffleHandler.getSongIndex());
    }
}
