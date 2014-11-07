package com.apps.playback;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ExternalMemorySelect extends ListActivity {
    static String path=Environment.getExternalStorageDirectory().getAbsolutePath();
    PlayerServiceHandler playerServiceHandler = PlayerServiceHandler.getInstance();
    final static String FILTER = "mp3";
    final static String FILTER_FLAC = "flac";
    final static String FILTER_WAV= "wav";
    final static String FILTER_OGG= "ogg";
    final static String FILTER_AAC= "aac";
    static File rootDir = new File(path);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        playerServiceHandler.setScreenOn(true);
        setContentView(R.layout.playlist_from_storage);
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                list,
                R.layout.playlist_view,
                new String[] {"fileName","path"},
                new int[] {R.id.text1,R.id.text2}
        );
        setListAdapter(adapter);
        ListView listView = getListView();
        if(playerServiceHandler.getSongIndex()!=Constants.NO_SONG_SELECTED){
            listView.setSelectionFromTop(playerServiceHandler.getSongIndex(),listView.getTop());
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent service=new Intent(ExternalMemorySelect.this,PlayerService.class);
                service.putExtra(Constants.SONG_INDEX,position);
                service.putExtra(Constants.SELECTED_FROM_PLAYLIST,Constants.FROM_PLAYLIST);
                startService(service);
                Intent intent=new Intent(ExternalMemorySelect.this,MainScreen.class);
                intent.putExtra("songIndex",position);
                startActivity(intent);
            }
        });

    }
    public static ArrayList<HashMap<String,String>> getSongList(){
        fillPlayList(rootDir);
        return list;
    }

    public static ArrayList<HashMap<String, String>> getList() {
        return list;
    }

    public static void setList(ArrayList<HashMap<String, String>> list) {
        ExternalMemorySelect.list = list;
    }

    public static ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

    public static void fillPlayList(File parentDir){
        File [] files = parentDir.listFiles();
        if(files==null){
            System.out.println("Folder is empty");
        }else{
            for(File file:files){
                String fileName=file.getName().toLowerCase();
                if(file.getName().endsWith(FILTER)||file.getName().toLowerCase().endsWith(FILTER_FLAC)||file.getName().toLowerCase().endsWith(FILTER_WAV)||file.getName().toLowerCase().endsWith(FILTER_OGG)||file.getName().endsWith(FILTER_AAC)){
                    HashMap<String,String> filesWithPath = new HashMap<String,String>();
                    filesWithPath.put("fileName",file.getName());
                    filesWithPath.put("path",parentDir.toString());
                    list.add(filesWithPath);
                }
                else if("android".equals(fileName)&&file.isDirectory()||"ringtones".equals(fileName)&&file.isDirectory()){

                }
                else if(file.isDirectory()){
                    String subPath=parentDir.getAbsolutePath()+"/"+file.getName();
                    File subFilePath = new File(subPath);
                    fillPlayList(subFilePath);
                }


            }
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
    }
}
