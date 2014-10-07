package com.example.sergiishkap.blackplay;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Presets extends ListActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        final String[]presets=getResources().getStringArray(R.array.preset_list);
        this.setListAdapter(new ArrayAdapter<String>(this,R.layout.equalizer_list,R.id.presets,presets));
        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String preset=presets[position];
                Intent intent=new Intent(Presets.this,MainScreen.class);
                PresetRepeatShuffleHandler.setPreset(preset);
                startActivity(intent);
            }
        });
    }
}