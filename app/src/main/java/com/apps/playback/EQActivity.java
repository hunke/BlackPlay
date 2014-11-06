package com.apps.playback;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class EQActivity extends Activity implements SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener {
    PlayerServiceHandler playerServiceHandler = PlayerServiceHandler.getInstance();
    ImageView bass_boost_label = null;
    SeekBar bass_boost = null;
    ToggleButton enabled = null;
    ImageButton flat = null;
    SharedPreferences sharedPreferences;
    Equalizer eq = null;
    BassBoost bb = null;

    int min_level = 0;
    int max_level = 100;

    static final int MAX_SLIDERS = 8; // Must match the XML layout
    SeekBar sliders[] = new SeekBar[MAX_SLIDERS];
    TextView slider_labels[] = new TextView[MAX_SLIDERS];
    int num_sliders = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        sharedPreferences=getSharedPreferences(Constants.PLAYERPREF,MODE_PRIVATE);
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.eq_layout);
        playerServiceHandler.setScreenOn(true);
        enabled = (ToggleButton)findViewById(R.id.enabled);
        enabled.setOnCheckedChangeListener(this);
        enabled.setBackgroundColor(Color.TRANSPARENT);

        flat = (ImageButton)findViewById(R.id.flat);
        flat.setOnClickListener(this);

        bass_boost = (SeekBar)findViewById(R.id.bass_boost);
        bass_boost.setOnSeekBarChangeListener(this);
        bass_boost_label=(ImageView) findViewById (R.id.bass_boost_label);

        sliders[0] = (SeekBar)findViewById(R.id.slider_1);
        slider_labels[0] = (TextView)findViewById(R.id.slider_label_1);
        sliders[1] = (SeekBar)findViewById(R.id.slider_2);
        slider_labels[1] = (TextView)findViewById(R.id.slider_label_2);
        sliders[2] = (SeekBar)findViewById(R.id.slider_3);
        slider_labels[2] = (TextView)findViewById(R.id.slider_label_3);
        sliders[3] = (SeekBar)findViewById(R.id.slider_4);
        slider_labels[3] = (TextView)findViewById(R.id.slider_label_4);
        sliders[4] = (SeekBar)findViewById(R.id.slider_5);
        slider_labels[4] = (TextView)findViewById(R.id.slider_label_5);
        sliders[5] = (SeekBar)findViewById(R.id.slider_6);
        slider_labels[5] = (TextView)findViewById(R.id.slider_label_6);
        sliders[6] = (SeekBar)findViewById(R.id.slider_7);
        slider_labels[6] = (TextView)findViewById(R.id.slider_label_7);
        sliders[7] = (SeekBar)findViewById(R.id.slider_8);
        slider_labels[7] = (TextView)findViewById(R.id.slider_label_8);

        eq = new Equalizer (0, 0);
        if (eq != null)
        {
            eq.setEnabled (true);
            int num_bands = eq.getNumberOfBands();
            num_sliders = num_bands;
            short r[] = eq.getBandLevelRange();
            min_level = r[0];
            max_level = r[1];
            for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++)
            {
                int[] freq_range = eq.getBandFreqRange((short)i);
                sliders[i].setOnSeekBarChangeListener(this);
                slider_labels[i].setText (formatBandLabel (freq_range));
            }
        }
        for (int i = num_sliders ; i < MAX_SLIDERS; i++)
        {
            sliders[i].setVisibility(View.GONE);
            slider_labels[i].setVisibility(View.GONE);
        }

        bb = new BassBoost (0, 0);
        if (bb != null)
        {
        }
        else
        {
            bass_boost.setVisibility(View.GONE);
            bass_boost_label.setVisibility(View.GONE);
        }
        updateUI();
        setEqFromPref();
    }
    public void setEqFromPref(){
        if(sharedPreferences!=null&&sharedPreferences.contains(Constants.EQ_BB_PREF)){
            bass_boost.setProgress(sharedPreferences.getInt(Constants.EQ_BB_PREF,0));
        }
        if(sharedPreferences!=null&&sharedPreferences.contains(Constants.EQ_1stLine)){
            sliders[0].setProgress(sharedPreferences.getInt(Constants.EQ_1stLine,50));
        }
        if(sharedPreferences!=null&&sharedPreferences.contains(Constants.EQ_2ndLine)){
            sliders[1].setProgress(sharedPreferences.getInt(Constants.EQ_2ndLine,50));
        }
        if(sharedPreferences!=null&&sharedPreferences.contains(Constants.EQ_3dLine)){
            sliders[2].setProgress(sharedPreferences.getInt(Constants.EQ_3dLine,50));
        }
        if(sharedPreferences!=null&&sharedPreferences.contains(Constants.EQ_4thLine)){
            sliders[3].setProgress(sharedPreferences.getInt(Constants.EQ_4thLine,50));
        }
        if(sharedPreferences!=null&&sharedPreferences.contains(Constants.EQ_5thLine)){
            sliders[4].setProgress(sharedPreferences.getInt(Constants.EQ_5thLine,50));
        }
        if(sharedPreferences!=null&&sharedPreferences.contains(Constants.EQ_6thLine)){
            sliders[5].setProgress(sharedPreferences.getInt(Constants.EQ_6thLine,50));
        }
        if(sharedPreferences!=null&&sharedPreferences.contains(Constants.EQ_7thLine)){
            sliders[6].setProgress(sharedPreferences.getInt(Constants.EQ_7thLine,50));
        }
        if(sharedPreferences!=null&&sharedPreferences.contains(Constants.EQ_8thLine)){
            sliders[7].setProgress(sharedPreferences.getInt(Constants.EQ_8thLine,50));
        }
    }
    /*=============================================================================
        onProgressChanged
    =============================================================================*/
    @Override
    public void onProgressChanged (SeekBar seekBar, int level,
                                   boolean fromTouch)
    {
        if (seekBar == bass_boost)
        {
            bb.setEnabled (level > 0 ? true : false);
            bb.setStrength ((short)level); // Already in the right range 0-1000
        }
        else if (eq != null)
        {
            int new_level = min_level + (max_level - min_level) * level / 100;

            for (int i = 0; i < num_sliders; i++)
            {
                if (sliders[i] == seekBar)
                {
                    eq.setBandLevel ((short)i, (short)new_level);
                    break;
                }
            }
        }
    }

    /*=============================================================================
        onStartTrackingTouch
    =============================================================================*/
    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
    }
    @Override
    protected void onPause() {
        // WHEN THE SCREEN IS ABOUT TO TURN OFF
        if (!SystemActionsReceiver.isScreenOn) {
            playerServiceHandler.setScreenOn(false);
        } else {
            playerServiceHandler.setScreenOn(true);
        }
        saveEqState();
        super.onPause();
    }

    public void saveEqState(){
        SharedPreferences.Editor editor= getSharedPreferences(Constants.PLAYERPREF, MODE_PRIVATE).edit();
        if(sliders[0]!=null){
            editor.putInt(Constants.EQ_1stLine,sliders[0].getProgress());
        }
        if(sliders[1]!=null){
            editor.putInt(Constants.EQ_2ndLine,sliders[1].getProgress());
        }
        if(bass_boost!=null){
            editor.putInt(Constants.EQ_BB_PREF,bass_boost.getProgress());
        }
        if(sliders[2]!=null){
            editor.putInt(Constants.EQ_3dLine,sliders[2].getProgress());
        }
        if(sliders[3]!=null){
            editor.putInt(Constants.EQ_4thLine,sliders[3].getProgress());
        }
        if(sliders[4]!=null){
            editor.putInt(Constants.EQ_5thLine,sliders[4].getProgress());
        }
        if(sliders[5]!=null){
            editor.putInt(Constants.EQ_6thLine,sliders[5].getProgress());
        }
        if(sliders[6]!=null){
            editor.putInt(Constants.EQ_7thLine,sliders[6].getProgress());
        }
        if(sliders[7]!=null){
            editor.putInt(Constants.EQ_8thLine,sliders[7].getProgress());
        }
        editor.commit();
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
    /*=============================================================================
        onStopTrackingTouch
    =============================================================================*/
    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
    }

    /*=============================================================================
        formatBandLabel
    =============================================================================*/
    public String formatBandLabel (int[] band)
    {
        return milliHzToString(band[0]) + "-" + milliHzToString(band[1]);
    }

    /*=============================================================================
        milliHzToString
    =============================================================================*/
    public String milliHzToString (int milliHz)
    {
        if (milliHz < 1000) return "";
        if (milliHz < 1000000)
            return "" + (milliHz / 1000) + "Hz";
        else
            return "" + (milliHz / 1000000) + "kHz";
    }

    /*=============================================================================
        updateSliders
    =============================================================================*/
    public void updateSliders ()
    {
        for (int i = 0; i < num_sliders; i++)
        {
            int level;
            if (eq != null)
                level = eq.getBandLevel ((short)i);
            else
                level = 0;
            int pos = 100 * level / (max_level - min_level) + 50;
            sliders[i].setProgress (pos);
        }
    }

    /*=============================================================================
        updateBassBoost
    =============================================================================*/
    public void updateBassBoost ()
    {
        if (bb != null)
            bass_boost.setProgress (bb.getRoundedStrength());
        else
            bass_boost.setProgress (0);
    }

    /*=============================================================================
        onCheckedChange
    =============================================================================*/
    @Override
    public void onCheckedChanged (CompoundButton view, boolean isChecked)
    {
        if (view == (View) enabled)
        {
            eq.setEnabled (isChecked);
        }
    }

    /*=============================================================================
        onClick
    =============================================================================*/
    @Override
    public void onClick (View view)
    {
        if (view == (View) flat)
        {
            setFlat();
        }
    }

    /*=============================================================================
        updateUI
    =============================================================================*/
    public void updateUI ()
    {
        updateSliders();
        updateBassBoost();
        enabled.setChecked (eq.getEnabled());
    }

    /*=============================================================================
        setFlat
    =============================================================================*/
    public void setFlat ()
    {
        if (eq != null)
        {
            for (int i = 0; i < num_sliders; i++)
            {
                eq.setBandLevel ((short)i, (short)0);
            }
        }

        if (bb != null)
        {
            bb.setEnabled (false);
            bb.setStrength ((short)0);
        }

        updateUI();
    }
}