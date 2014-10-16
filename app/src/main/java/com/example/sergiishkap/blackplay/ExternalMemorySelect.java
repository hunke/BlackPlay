package com.example.sergiishkap.blackplay;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
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
    PresetRepeatShuffleHandler presetRepeatShuffleHandler=PresetRepeatShuffleHandler.getInstance();
    final static String FILTER = "mp3";
    static File rootDir = new File(path);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

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
                if(file.getName().endsWith(FILTER)){
                    HashMap<String,String> filesWithPath = new HashMap<String,String>();
                    filesWithPath.put("fileName",file.getName());
                    filesWithPath.put("path",parentDir.toString());
                    list.add(filesWithPath);
                    System.out.println(list.toString());
                }
                else if(file.isDirectory()){
                    String subPath=path+"/"+file.getName();
                    File subFilePath = new File(subPath);
                    fillPlayList(subFilePath);
                }
            }
        }
    }
    @Override
    protected void onPause() {
        // WHEN THE SCREEN IS ABOUT TO TURN OFF
        if (ScreenReceiver.isScreenOn) {
            presetRepeatShuffleHandler.setScreenOn(false);
        } else {

        }
        super.onPause();
    }
    @Override
    protected void onResume() {
        // ONLY WHEN SCREEN TURNS ON
        if (!ScreenReceiver.isScreenOn) {
            presetRepeatShuffleHandler.setScreenOn(true);
        } else {
        }
        super.onResume();
    }
}
