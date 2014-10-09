package com.example.sergiishkap.blackplay;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceResponse;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;


public class MainScreen extends Activity {

    public static final String REPEAT_KEY = "REPEATKEY";
    public static final String SHUFFLE_KEY = "SHUFFLEKEY";
    public ArrayList<HashMap<String,String>> songList=PresetRepeatShuffleHandler.songList;
    MediaPlayer mp=PresetRepeatShuffleHandler.getMp();
    public static final String apiURL="http://developer.echonest.com/api/v4/artist/images?api_key=6XY1VAB7JI048NKWW&name=";
    public static final String apiURLSuffix="&format=json&results=1&start=0&license=unknown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_screen);
        setRepeatImg();
        setShuffleImg();
        setTrackImg();
        Intent intent=getIntent();
        int songPosition=intent.getIntExtra("songIndex",99999999);
        PresetRepeatShuffleHandler.setSongIndex(songPosition);
        if("yes".equals(intent.getStringExtra("fromEQ"))){
            if(PresetRepeatShuffleHandler.getCurrentSongPosition()==99999999){

            }else{
                setSongMetadata(getSelectedFilePath(PresetRepeatShuffleHandler.getCurrentSongPosition())+"/"+getSelectedFileName(PresetRepeatShuffleHandler.getCurrentSongPosition()));
            }
        }else{
            changePlayState();
            PresetRepeatShuffleHandler.setCurrentSongPosition(songPosition);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }


    public String getSelectedFileName(int i){
        String filename=songList.get(i).get("fileName");
        return filename;
    }
    public String getSelectedFilePath(int i){
        String filePath=songList.get(i).get("path");
        return filePath;
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
    public void setSongMetadata(String filePathAndFileName){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(filePathAndFileName);
        String artist=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String httpArtist=artist.replace(" ","%20").toLowerCase();
        String songName=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        TextView artistName=(TextView)findViewById(R.id.artist);
        artistName.setText(artist);
        TextView songNameView=(TextView)findViewById(R.id.composition);
        songNameView.setText(songName);
        String albumName=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        TextView albumText=(TextView)findViewById(R.id.albumName);
        albumText.setText(albumName);




    }
    public void changePlayState(){
        if(mp==null){
            if(PresetRepeatShuffleHandler.getSongIndex()==99999999){

            }
            else{
                initializeMediaPlayer();
                try{
                    String fileLoc=getSelectedFilePath(PresetRepeatShuffleHandler.getSongIndex()) + "/" + getSelectedFileName(PresetRepeatShuffleHandler.getSongIndex());
                    mp.reset();
                    mp.setDataSource(fileLoc);
                    mp.prepare();
                    mp.start();
                    setSongMetadata(fileLoc);
                    setPlayImg(true);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if(PresetRepeatShuffleHandler.getCurrentSongPosition()==PresetRepeatShuffleHandler.getSongIndex()){
            if(mp.isPlaying()){
                mp.pause();
                setPlayImg(false);
            }else {
                mp.start();
                setPlayImg(true);
            }
        }else{
            try {
                mp.reset();
                String fileLoc=getSelectedFilePath(PresetRepeatShuffleHandler.getSongIndex()) + "/" + getSelectedFileName(PresetRepeatShuffleHandler.getSongIndex());
                mp.setDataSource(fileLoc);
                mp.prepare();
                mp.start();
                setSongMetadata(fileLoc);
                setPlayImg(true);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void setPlayImg(boolean playState){
        ImageButton playBtn=(ImageButton)findViewById(R.id.play_btn);
        if(playState){
            playBtn.setImageResource(R.drawable.next_track);
        }else {
            playBtn.setImageResource(R.drawable.play);
        }
    }
    public void initializeMediaPlayer(){
        mp=PresetRepeatShuffleHandler.setMp(new MediaPlayer());
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

    public void playFile(View view){
        changePlayState();
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
        Intent intent=new Intent(MainScreen.this, EQActivity.class);
        startActivity(intent);
    }
}
