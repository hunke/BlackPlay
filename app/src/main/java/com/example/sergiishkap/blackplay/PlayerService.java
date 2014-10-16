package com.example.sergiishkap.blackplay;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class PlayerService extends IntentService implements MediaPlayer.OnCompletionListener {
    VolumeHandler mSettingsContentObserver;
    PlayerServiceHandler presetRepeatShuffleHandler= PlayerServiceHandler.getInstance();
    MediaPlayer mp=presetRepeatShuffleHandler.getMp();
    public ArrayList<HashMap<String,String>> songList=presetRepeatShuffleHandler.songList;
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
    public void onCreate(){
       super.onCreate();
        mSettingsContentObserver = new VolumeHandler(this,new Handler());
        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver);
    }
    @Override
    public int onStartCommand(Intent intent, int flags,int startId){
        if(mp==null){
            initializeMediaPlayer();
        }
        int actionName=intent.getIntExtra(Constants.ACTION, 0);
        int songIndex=intent.getIntExtra(Constants.SONG_INDEX,Constants.NO_SONG_SELECTED);
        boolean fromPlaylist=intent.getBooleanExtra(Constants.SELECTED_FROM_PLAYLIST,false);
        if(fromPlaylist){
            startNewSong(songIndex);
        }
        presetRepeatShuffleHandler.setSongIndex(songIndex);
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
                System.out.println("RepeatWorks!");
                changeRepeat();
                break;
            case Constants.SHUFFLE:
                System.out.println("ShuffleWorks!");
                changeShuffle();
                break;
            case Constants.HEADPHONES_UNPLUGGED:
                pausePlaying();
                break;
            default:
                System.out.println("Doesn't work!");
                break;
        }
        presetRepeatShuffleHandler.setServiceStarted(true);
        return super.onStartCommand(intent,flags,startId);
    }
    public void resetMediaPlayer(){
        mp.reset();
    }
    public void initializeMediaPlayer(){
        presetRepeatShuffleHandler.setMp(new MediaPlayer());
    }
    public void resumePlaying(){
        mp.start();
        presetRepeatShuffleHandler.setMpPlaying(true);

    }
    public void pausePlaying(){
        if(mp!=null){
            mp.pause();
            presetRepeatShuffleHandler.setMpPlaying(false);
        }
    }
    public void startNewSong(int i){
        if(Constants.NO_SONG_SELECTED==i){
            resetMediaPlayer();
            presetRepeatShuffleHandler.setMpPlaying(false);
        }else{
            String fileLoc=getSelectedFileByLocation(i);
            System.out.println("New Song is prepearing");
            if(null==mp){
                initializeMediaPlayer();
            }
            try{
                mp.reset();
                mp.setDataSource(fileLoc);
                mp.prepare();
                mp.start();
                mp.setOnCompletionListener(this);
                presetRepeatShuffleHandler.setCurrentSongPosition(i);
                presetRepeatShuffleHandler.setSongIndex(i);
                presetRepeatShuffleHandler.setMpPlaying(true);
                presetRepeatShuffleHandler.setSongPathAndName(fileLoc);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void onCompletion(MediaPlayer arg0) {
        nextTrack();
    }
    public void changeRepeat(){
        if(presetRepeatShuffleHandler.isIsRepeatOn()){
            presetRepeatShuffleHandler.setIsRepeatOn(false);
        }else {
            presetRepeatShuffleHandler.setIsRepeatOn(true);
        }
    }
    public void changeShuffle(){
        if(presetRepeatShuffleHandler.isIsShuffleOn()){
            presetRepeatShuffleHandler.setIsShuffleOn(false);
            songList.clear();
            songList=ExternalMemorySelect.getSongList();
        }else {
            presetRepeatShuffleHandler.setIsShuffleOn(true);
            songList=presetRepeatShuffleHandler.songList;
            randoMizeSongList(songList);
        }
    }
    public void changePlayPause(){
        if(mp.isPlaying()){
            pausePlaying();
            presetRepeatShuffleHandler.setMpPlaying(false);
        }
        else if(presetRepeatShuffleHandler.getSongIndex()==Constants.NO_SONG_SELECTED){
            startNewSong(0);
            presetRepeatShuffleHandler.setMpPlaying(true);
        }
        else {
            resumePlaying();
            presetRepeatShuffleHandler.setMpPlaying(true);
        }
    }
    public void nextTrack(){
        int songListSize=songList.size();

        if(!presetRepeatShuffleHandler.isIsRepeatOn()&&presetRepeatShuffleHandler.getCurrentSongPosition()==songListSize-1){
            presetRepeatShuffleHandler.setSongIndex(Constants.NO_SONG_SELECTED);
        }
        else if(presetRepeatShuffleHandler.isIsRepeatOn()&&presetRepeatShuffleHandler.getCurrentSongPosition()==songListSize-1){
            presetRepeatShuffleHandler.setSongIndex(0);
        }
        else{
            presetRepeatShuffleHandler.setSongIndex(presetRepeatShuffleHandler.getCurrentSongPosition()+1);
        }
        startNewSong(presetRepeatShuffleHandler.getSongIndex());
    }
    public void previousTrack(){
        int songListSize=songList.size();

        if(!presetRepeatShuffleHandler.isIsRepeatOn()&&presetRepeatShuffleHandler.getCurrentSongPosition()==0){
            presetRepeatShuffleHandler.setSongIndex(Constants.NO_SONG_SELECTED);
        }
        else if(presetRepeatShuffleHandler.isIsRepeatOn()&&presetRepeatShuffleHandler.getCurrentSongPosition()==0){
            presetRepeatShuffleHandler.setSongIndex(songListSize-1);
        }
        else{
            presetRepeatShuffleHandler.setSongIndex(presetRepeatShuffleHandler.getCurrentSongPosition()-1);
        }
        startNewSong(presetRepeatShuffleHandler.getSongIndex());
    }

}
