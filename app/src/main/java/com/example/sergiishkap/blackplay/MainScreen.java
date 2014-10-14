package com.example.sergiishkap.blackplay;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;


public class MainScreen extends Activity implements MediaPlayer.OnCompletionListener {

    public ArrayList<HashMap<String,String>> songList=PresetRepeatShuffleHandler.songList;
    public void randoMizeSongList(ArrayList<HashMap<String,String>> randomizedSongList){
        long seed=System.nanoTime();
        Collections.shuffle(randomizedSongList,new Random(seed));
    }
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
        Intent intent=getIntent();
        int songPosition=intent.getIntExtra("songIndex",99999999);
        PresetRepeatShuffleHandler.setSongIndex(songPosition);
        if("yes".equals(intent.getStringExtra("fromEQ"))){
            setSongMetadata(getSelectedFilePath(PresetRepeatShuffleHandler.getCurrentSongPosition())+"/"+getSelectedFileName(PresetRepeatShuffleHandler.getCurrentSongPosition()));
            if(mp!=null&&mp.isPlaying()){
                setPlayImg(true);
            }
        }else{
            changePlayState();
            PresetRepeatShuffleHandler.setCurrentSongPosition(songPosition);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if(PresetRepeatShuffleHandler.getCurrentSongPosition()==99999999){
            setDefaultAlbumImage();
        }
    }
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            playPreviousSong();
            System.out.println("Success!");
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
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
        setShuffleImg();
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
        TextView artistName=(TextView)findViewById(R.id.artist);
        TextView songNameView=(TextView)findViewById(R.id.composition);
        TextView albumText=(TextView)findViewById(R.id.albumName);
        String artist=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        byte [] data= retriever.getEmbeddedPicture();
        if(artist==null||artist==""){
            artistName.setText(getSelectedFileName(PresetRepeatShuffleHandler.getSongIndex()));
        }else{
            String httpArtist=artist.replace(" ","%20").toLowerCase();
            artistName.setText(artist);
        }
        String songName=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if(songName==null||songName==""){
            songNameView.setText(getSelectedFilePath(PresetRepeatShuffleHandler.getSongIndex()));
        }else{
            songNameView.setText(songName);
        }
        String albumName=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        if(albumName==null||albumName==""){
            albumText.setText("");
        }else {
            albumText.setText(albumName);
        }
        if(data!=null){
            ImageView bgImg=(ImageView)findViewById(R.id.album_bg);
            Bitmap bitmap= BitmapFactory.decodeByteArray(data,0,data.length);
            bgImg.setImageBitmap(bitmap);
            bgImg.setAdjustViewBounds(true);
            Drawable rightArrow = new BitmapDrawable(getResources(),bitmap);
            rightArrow.setAlpha(100);
            bgImg.setImageDrawable(rightArrow);
        }
        else{
            setDefaultAlbumImage();
        }
}
    public void onCompletion(MediaPlayer arg0) {
        playNextSong();
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
                    mp.setOnCompletionListener(this);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if(PresetRepeatShuffleHandler.getCurrentSongPosition()==PresetRepeatShuffleHandler.getSongIndex()){
            if(mp.isPlaying()){
                mp.pause();
                setPlayImg(false);
                String fileLoc=getSelectedFilePath(PresetRepeatShuffleHandler.getSongIndex()) + "/" + getSelectedFileName(PresetRepeatShuffleHandler.getSongIndex());
                setSongMetadata(fileLoc);
            }else {
                mp.start();
                setPlayImg(true);
            }
        }else if(PresetRepeatShuffleHandler.getSongIndex()==99999999){
            resetSongMeta();
            setPlayImg(false);
            mp.reset();
        }
        else{
                try {
                    mp.reset();
                    String fileLoc=getSelectedFilePath(PresetRepeatShuffleHandler.getSongIndex()) + "/" + getSelectedFileName(PresetRepeatShuffleHandler.getSongIndex());
                    mp.setDataSource(fileLoc);
                    mp.prepare();
                    mp.start();
                    setSongMetadata(fileLoc);
                    setPlayImg(true);
                    PresetRepeatShuffleHandler.setCurrentSongPosition(PresetRepeatShuffleHandler.getSongIndex());
                    mp.setOnCompletionListener(this);
                }catch (Exception e){
                    e.printStackTrace();
                }
        }
    }
    public void resetSongMeta(){
        TextView artistName=(TextView)findViewById(R.id.artist);
        TextView songNameView=(TextView)findViewById(R.id.composition);
        TextView albumText=(TextView)findViewById(R.id.albumName);
        artistName.setText("");
        songNameView.setText("");
        albumText.setText("");
    }
    public void setPlayImg(boolean playState){
        ImageButton playBtn=(ImageButton)findViewById(R.id.play_btn);
        if(playState){
            playBtn.setImageResource(R.drawable.pause);
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
    public void setDefaultAlbumImage(){
        ImageView bgImg=(ImageView)findViewById(R.id.album_bg);
        Drawable rightArrow = getResources().getDrawable(R.drawable.gettingawaywithmurder);
        rightArrow.setAlpha(50);
        bgImg.setImageDrawable(rightArrow);
    }
    public void playNextSong(){
        int songListSize=songList.size();
        ImageButton nextTrack=(ImageButton)findViewById(R.id.next_track);
        if(mp!=null){
            if(!PresetRepeatShuffleHandler.isIsRepeatOn()&&PresetRepeatShuffleHandler.getCurrentSongPosition()==songListSize-1){
                PresetRepeatShuffleHandler.setSongIndex(99999999);
            }
            else if(PresetRepeatShuffleHandler.isIsRepeatOn()&&PresetRepeatShuffleHandler.getCurrentSongPosition()==songListSize-1){
                PresetRepeatShuffleHandler.setSongIndex(0);
            }
            else{
                PresetRepeatShuffleHandler.setSongIndex(PresetRepeatShuffleHandler.getCurrentSongPosition()+1);
            }
            changePlayState();
        }else {

        }
    }
    public void nextSong(View view){
        playNextSong();
    }
    public void playPreviousSong(){
        int songListSize=songList.size();
        ImageButton previousTrack=(ImageButton)findViewById(R.id.previous_track);
        int index= PresetRepeatShuffleHandler.getSongIndex();
        if(mp!=null){
            if(index==99999999){

            }
            else if(PresetRepeatShuffleHandler.isIsRepeatOn()&&index==0){
                PresetRepeatShuffleHandler.setSongIndex(songListSize - 1);
            }
            else if(!PresetRepeatShuffleHandler.isIsRepeatOn()&&index==0){
                mp.reset();
                setPlayImg(false);
            }
            else{
                PresetRepeatShuffleHandler.setSongIndex(index-1);
            }
            changePlayState();
        }else {

        }
    }
    public void previousSong(View view){
        playPreviousSong();
    }

    public void playFile(View view){
        changePlayState();
    }
    public void changeShuffleState(boolean shuffleState){
        if(shuffleState){
            PresetRepeatShuffleHandler.setIsShuffleOn(false);
            setShuffleImg();
            songList.clear();
            songList=ExternalMemorySelect.getSongList();
        }else {
            PresetRepeatShuffleHandler.setIsShuffleOn(true);
            setShuffleImg();
            songList=PresetRepeatShuffleHandler.songList;
            randoMizeSongList(songList);
        }
    }

    public void selectPreset(View view){
        Intent intent=new Intent(MainScreen.this, EQActivity.class);
        startActivity(intent);
    }

}
