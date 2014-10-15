package com.example.sergiishkap.blackplay;


import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
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
import java.util.logging.Handler;


public class MainScreen extends Activity{

    public static final String apiURL="http://developer.echonest.com/api/v4/artist/images?api_key=6XY1VAB7JI048NKWW&name=";
    public static final String apiURLSuffix="&format=json&results=1&start=0&license=unknown";
    PresetRepeatShuffleHandler presetRepeatShuffleHandler=PresetRepeatShuffleHandler.getInstance();

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_screen);
        setRepeatImg();
        setShuffleImg();
        if(!presetRepeatShuffleHandler.isServiceStarted()){
            Intent service= new Intent(this,PlayerService.class);
            startService(service);
        }
        Intent intent=getIntent();
        int songPosition=intent.getIntExtra("songIndex",Constants.NO_SONG_SELECTED);
        presetRepeatShuffleHandler.setSongIndex(songPosition);

    }
    public void buttonToPlaylist_Click(View view) {
        Intent intent = new Intent(MainScreen.this, ExternalMemorySelect.class);
        startActivity(intent);
    }

    public void repeatToggle(View view){
        Intent service = new Intent(MainScreen.this,PlayerService.class);
        service.putExtra(Constants.ACTION,Constants.REPEAT);
        startService(service);
        setRepeatImg();
    }
    public void shuffleToggle(View view){
        Intent service=new Intent(MainScreen.this,PlayerService.class);
        service.putExtra(Constants.ACTION,Constants.SHUFFLE);
        startService(service);
        setShuffleImg();
    }
    public void nextSong(View view){
        intent=new Intent(this,PlayerService.class);
        intent.putExtra(Constants.ACTION, Constants.NEXT_SONG);
        startService(intent);
        setPlayImg();
    }
    public void previousSong(View view){
        Intent service=new Intent(this,PlayerService.class);
        service.putExtra(Constants.ACTION, Constants.PREVIOUS_SONG);
        startService(service);
        setPlayImg();
    }

    public void playFile(View view){
        intent=new Intent(this,PlayerService.class);
        intent.putExtra(Constants.ACTION,Constants.PLAY_PAUSE);
        startService(intent);
        setPlayImg();
    }

    public void selectPreset(View view){
        Intent intent=new Intent(MainScreen.this, EQActivity.class);
        startActivity(intent);
    }

    public void setSongMetadata(String filePathAndFileName){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(filePathAndFileName);
        TextView artistName=(TextView)findViewById(R.id.artist);
        TextView songNameView=(TextView)findViewById(R.id.composition);
        TextView albumText=(TextView)findViewById(R.id.albumName);
        String artist=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        byte [] data= retriever.getEmbeddedPicture();
        if(artist!=null&&artist!=""){
            artistName.setText(artist);
        }else{
            artistName.setText(presetRepeatShuffleHandler.getSongPathAndName());
        }
        String songName=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if(songName!=null&&songName!=""){
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

    public void resetSongMeta(){
        TextView artistName=(TextView)findViewById(R.id.artist);
        TextView songNameView=(TextView)findViewById(R.id.composition);
        TextView albumText=(TextView)findViewById(R.id.albumName);
        artistName.setText("");
        songNameView.setText("");
        albumText.setText("");
    }
    public void setPlayImg(){
        ImageButton playBtn=(ImageButton)findViewById(R.id.play_btn);
        if(presetRepeatShuffleHandler.isMpPlaying()){
            playBtn.setImageResource(R.drawable.pause);
        }else {
            playBtn.setImageResource(R.drawable.play);
        }

    }
    public void setRepeatImg(){
        ImageButton repeatbtn=(ImageButton)findViewById(R.id.repeat);
        if(presetRepeatShuffleHandler.isIsRepeatOn()){
            repeatbtn.setImageResource(R.drawable.repeat_on_img);
        }else {
            repeatbtn.setImageResource(R.drawable.repeat_off_img);
        }
    }
    public void setShuffleImg(){
        ImageButton shuffleBtn=(ImageButton)findViewById(R.id.shuffle);
        if(presetRepeatShuffleHandler.isIsShuffleOn()){
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
}
