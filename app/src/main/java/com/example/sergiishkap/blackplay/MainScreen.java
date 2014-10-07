package com.example.sergiishkap.blackplay;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;


public class MainScreen extends Activity {

    public static final String REPEAT_KEY = "REPEATKEY";
    public static final String SHUFFLE_KEY = "SHUFFLEKEY";
    private int songIndex;
    MediaPlayer mp;
    public ArrayList<HashMap<String,String>> songList=PresetRepeatShuffleHandler.songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_screen);
        setRepeatImg();
        setShuffleImg();
        setTrackImg();
        getPreset();
        Intent intent=getIntent();
        int songPosition=intent.getIntExtra("songIndex",0);
        songIndex=songPosition;
    }
    public String getSelectedFileName(int i){
        String filename=songList.get(i).get("fileName");
        return filename;
    }
    public String getSelectedFilePath(int i){
        String filePath=songList.get(i).get("path");
        return filePath;
    }
    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }
    public void buttonToPlaylist_Click(View view) {
        Intent intent = new Intent(MainScreen.this, ExternalMemorySelect.class);
        startActivity(intent);
    }

    public void repeatToggle(View view){
        changeRepeatState(PresetRepeatShuffleHandler.isRepeatOn);
        setRepeatImg();
    }
    public void shuffleToggle(View view){
        changeShuffleState(PresetRepeatShuffleHandler.isShuffleOn);
        setRepeatImg();
    }
    public void changeRepeatState(boolean repeatValue){
        if (!repeatValue){
            PresetRepeatShuffleHandler.setIsRepeatOn(true);
            setRepeatImg();
        }else {
            PresetRepeatShuffleHandler.setIsRepeatOn(false);
            setRepeatImg();
        }
    }
    public void changePlayState(){
        if(mp==null){
            initializeMediaPlayer();
            try{
                mp.reset();
                mp.setDataSource(getSelectedFilePath(songIndex) + "/" + getSelectedFileName(songIndex));
                mp.prepare();
                mp.start();
            }catch (Exception e) {
                e.printStackTrace();
            }
            setPlayImg();
        }else if(mp.isPlaying()){
            mp.pause();
            setPlayImg();
        }else {
            mp.start();
            setPlayImg();
        }

    }
    public void initializeMediaPlayer(){
        mp=new MediaPlayer();
        mp.setScreenOnWhilePlaying(false);
    }
    public void setRepeatImg(){
        ImageButton repeatbtn=(ImageButton)findViewById(R.id.repeat);
        if(PresetRepeatShuffleHandler.isIsRepeatOn()){
            repeatbtn.setImageResource(R.drawable.repeat_on_img);
        }else {
            repeatbtn.setImageResource(R.drawable.repeat_off_img);
        }
    }
    public void setShuffleImg(){
        ImageButton shuffleBtn=(ImageButton)findViewById(R.id.shuffle);
        if(PresetRepeatShuffleHandler.isIsShuffleOn()){
            shuffleBtn.setImageResource(R.drawable.shuffle_on_img);
        }else {
            shuffleBtn.setImageResource(R.drawable.shuffle_off_img);
        }
    }
    public void setPlayImg(){
        ImageButton playBtn=(ImageButton)findViewById(R.id.play);
        if(mp.isPlaying()){
            playBtn.setImageResource(R.drawable.next_track);
        }else {
            playBtn.setImageResource(R.drawable.play);
        }
    }
    public void playFile(View view){
        changePlayState();
    }
    public void getPreset(){
        Button presetBtn=(Button)findViewById(R.id.preset);
        if(PresetRepeatShuffleHandler.getPreset()==null){
            PresetRepeatShuffleHandler.setPreset("Rock");
        }
        presetBtn.setText(PresetRepeatShuffleHandler.getPreset());

    }
    public void changeShuffleState(boolean shuffleState){
        if(shuffleState){
            PresetRepeatShuffleHandler.setIsShuffleOn(false);
            setShuffleImg();
        }else {
            PresetRepeatShuffleHandler.setIsShuffleOn(true);
            setShuffleImg();
        }
    }
    public void setTrackImg(){
        Drawable rightArrow = getResources().getDrawable(R.drawable.gettingawaywithmurder);
        rightArrow.setAlpha(50);
        ImageView bgImg=(ImageView)findViewById(R.id.album_bg);
        bgImg.setImageDrawable(rightArrow);
    }
    public void selectPreset(View view){
        Intent intent=new Intent(MainScreen.this, Presets.class);
        startActivity(intent);
    }
}
