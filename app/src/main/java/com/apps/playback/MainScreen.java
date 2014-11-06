package com.apps.playback;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Observable;
import java.util.Observer;


public class MainScreen extends Activity implements Observer{

    public static final String apiURL="https://itunes.apple.com/search?term=";
    public static final String apiURLSuffix="&entity=song";
    PlayerServiceHandler playerServiceHandler = PlayerServiceHandler.getInstance();
    Intent intent;
    private AdView adView;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpreferences=getSharedPreferences(Constants.PLAYERPREF,MODE_PRIVATE);
        if(sharedpreferences!=null&&sharedpreferences.contains(Constants.REPEAT_PREF)){
            playerServiceHandler.setIsRepeatOn(sharedpreferences.getBoolean(Constants.REPEAT_PREF,true));
        }
        if(sharedpreferences!=null&&sharedpreferences.contains(Constants.SHUFFLE_PREF)){
            playerServiceHandler.setIsShuffleOn(sharedpreferences.getBoolean(Constants.SHUFFLE_PREF, false));
        }
        final TelephonyManager tm =(TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        playerServiceHandler.addObserver(this);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        playerServiceHandler.setScreenOn(true);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        BroadcastReceiver mReceiver = new SystemActionsReceiver();
        registerReceiver(mReceiver, filter);
        setContentView(R.layout.main_screen);
        adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(deviceId)
                .build();
        adView.loadAd(adRequest);
        setRepeatImg();
        setShuffleImg();
        setSongMetadata(playerServiceHandler.getSongPathAndName());
        if(!playerServiceHandler.isServiceStarted()){
            Intent service= new Intent(this,PlayerService.class);
            startService(service);
        }
        Intent intent=getIntent();
        int songPosition=intent.getIntExtra("songIndex",Constants.NO_SONG_SELECTED);
        playerServiceHandler.setSongIndex(songPosition);
        boolean fromPL=intent.getBooleanExtra(Constants.SELECTED_FROM_PLAYLIST,false);
        if(fromPL){
          playSelectedTrack();
        }

    }
    @Override
    public void update(Observable observable, Object data) {
        int actionTriggered=(Integer)data;
        switch (actionTriggered){
            case Constants.SHUFFLE_STATE_CHANGED:
                setShuffleImg();
                break;
            case Constants.REPEAT_STATE_CHANGED:
                setRepeatImg();
                break;
            case Constants.PLAY_STATE_CHANGED:
                setPlayImg();
                break;
            case Constants.SONG_META_CHANGED:
                setSongMetadata(playerServiceHandler.getSongPathAndName());
                break;
            case Constants.SONG_INDEX_CHANGED:
                setPlayImg();
                break;
            case Constants.PREVIOUS_SONG_BG:
                previousTrack();
                break;
            case Constants.HEADPHONES_UNPLUGGED:
                forcePause();
                break;
            case Constants.NEXT_SONG_BG:
                nextTrack();
                break;
            case Constants.ON_CALL:
                pauseOnCall();
                break;
            case Constants.CALL_FINISHED:
                forceResume();
                break;
            default:
                break;
        }
    }
    @Override
    protected void onPause() {
        // WHEN THE SCREEN IS ABOUT TO TURN OFF
        if (!SystemActionsReceiver.isScreenOn) {
            playerServiceHandler.setScreenOn(false);
        } else {
            playerServiceHandler.setScreenOn(true);
        }
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }
    @Override
    protected void onResume() {
        // ONLY WHEN SCREEN TURNS ON
        if (!playerServiceHandler.isScreenOn()) {
            playerServiceHandler.setScreenOn(true);
        } else {
        }
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }
    @Override
    protected void onStop(){
        savePreferences();
        super.onStop();
    }
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
    public void savePreferences(){
        SharedPreferences.Editor editor= getSharedPreferences(Constants.PLAYERPREF, MODE_PRIVATE).edit();
        editor.putBoolean(Constants.REPEAT_PREF,playerServiceHandler.isIsRepeatOn());
        editor.putBoolean(Constants.SHUFFLE_PREF,playerServiceHandler.isIsShuffleOn());
        editor.commit();
    }
    public void forceResume(){
        Intent service=new Intent(MainScreen.this,PlayerService.class);
        service.putExtra(Constants.ACTION,Constants.CALL_FINISHED);
        startService(service);
    }
    public void playSelectedTrack(){
        Intent service=new Intent(MainScreen.this,PlayerService.class);
        service.putExtra(Constants.ACTION,Constants.PLAY_SELECTED_TRACK);
        startService(service);
    }
    public void buttonToPlaylist_Click(View view) {
        Intent intent = new Intent(MainScreen.this, ExternalMemorySelect.class);
        startActivity(intent);
    }
    public void forcePause(){
        Intent service = new Intent(MainScreen.this,PlayerService.class);
        service.putExtra(Constants.ACTION,Constants.HEADPHONES_UNPLUGGED);
        startService(service);
    }
    public void pauseOnCall(){
        Intent service = new Intent(MainScreen.this,PlayerService.class);
        service.putExtra(Constants.ACTION,Constants.ON_CALL);
        startService(service);
    }

    public void repeatToggle(View view){
        Intent service = new Intent(MainScreen.this,PlayerService.class);
        service.putExtra(Constants.ACTION,Constants.REPEAT);
        startService(service);
    }
    public void shuffleToggle(View view){
        Intent service=new Intent(MainScreen.this,PlayerService.class);
        service.putExtra(Constants.ACTION,Constants.SHUFFLE);
        startService(service);
    }
    public void nextSong(View view){
        nextTrack();
    }
    public void nextTrack(){
        intent=new Intent(this,PlayerService.class);
        intent.putExtra(Constants.ACTION, Constants.NEXT_SONG);
        startService(intent);
    }
    public void previousSong(View view){
        previousTrack();
    }
    public void previousTrack(){
        Intent service=new Intent(this,PlayerService.class);
        service.putExtra(Constants.ACTION, Constants.PREVIOUS_SONG);
        startService(service);

    }
    public void unloadBackground() {
        ImageView bgimg=(ImageView)findViewById(R.id.album_bg);
        if (bgimg != null){
            bgimg.setImageDrawable(null);
        }
    }
    public void playFile(View view){
        intent=new Intent(this,PlayerService.class);
        intent.putExtra(Constants.ACTION,Constants.PLAY_PAUSE);
        startService(intent);
    }

    public void selectPreset(View view){
        Intent intent=new Intent(MainScreen.this, EQActivity.class);
        startActivity(intent);
    }

    public void setSongMetadata(String filePathAndFileName){
        unloadBackground();
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        if(null==filePathAndFileName){
            resetSongMeta();
        }else{
            retriever.setDataSource(filePathAndFileName);
            TextView artistName=(TextView)findViewById(R.id.artist);
            TextView songNameView=(TextView)findViewById(R.id.composition);
            TextView albumText=(TextView)findViewById(R.id.albumName);
            String artist=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

            byte [] data= retriever.getEmbeddedPicture();
            if(artist!=null&&artist!=""){
                artistName.setText(artist);
            }else{
                artistName.setText(playerServiceHandler.getSongPathAndName());
            }
            String songName=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            if(songName!=null&&""!=songName){
                songNameView.setText(songName);
            }else{
                songNameView.setText("");
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
                bgImg.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            else{
                setDefaultAlbumImage();
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
        setDefaultAlbumImage();
    }
    public void setPlayImg(){
        ImageButton playBtn=(ImageButton)findViewById(R.id.play_btn);
        if(playerServiceHandler.isMpPlaying()){
            playBtn.setImageResource(R.drawable.pause);
        }else {
            playBtn.setImageResource(R.drawable.play);
        }

    }
    public void setRepeatImg(){
        ImageButton repeatbtn=(ImageButton)findViewById(R.id.repeat);
        if(playerServiceHandler.isIsRepeatOn()){
            repeatbtn.setImageResource(R.drawable.repeat_on_img);
        }else {
            repeatbtn.setImageResource(R.drawable.repeat_off_img);
        }
    }
    public void setShuffleImg(){
        ImageButton shuffleBtn=(ImageButton)findViewById(R.id.shuffle);
        if(playerServiceHandler.isIsShuffleOn()){
            shuffleBtn.setImageResource(R.drawable.shuffle_on_img);
        }else {
            shuffleBtn.setImageResource(R.drawable.shuffle_off_img);
        }
    }
    public void setDefaultAlbumImage(){
        unloadBackground();
        ImageView bgImg=(ImageView)findViewById(R.id.album_bg);
        Drawable rightArrow = getResources().getDrawable(R.drawable.logo_combo);
        rightArrow.setAlpha(100);
        bgImg.setImageDrawable(rightArrow);
        bgImg.setScaleType(ImageView.ScaleType.FIT_XY);
    }
}