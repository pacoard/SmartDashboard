package edu.iit.paco.smartdashboard;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;

import java.util.ArrayList;


public class ActuatorsActivity extends AppCompatActivity {
    private SeekBar thermostatSeekBar;
    private AppCompatButton seekBarHeader;
    
    private Switch lightsKitchen;
    private Switch lightsLivingroom;
    private Switch lightsBedroom;
    private Switch lightsHall;
    private Switch windowsKitchen;
    private Switch windowsLivingroom;
    private Switch windowsBedroom;
    private Switch windowsHall;

    ArrayList<Switch> lightSwitches = new ArrayList<Switch>();
    ArrayList<Switch> windowSwitches = new ArrayList<Switch>();
    String ROOMS[] = {"kitchen", "livingroom", "bedroom", "hall"};

    private String homeURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actuators);

        // GET HOME URL FROM INTENT
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                homeURL= null;
            } else {
                homeURL= extras.getString("url");
                //Log.d("ACTUATORS_ACTIVITY", homeURL);
            }
        } else {
            homeURL= (String) savedInstanceState.getSerializable("url");
        }

        // SET THERMOSTAT SEEKBAR
        seekBarHeader = (AppCompatButton) findViewById(R.id.seekbarButton);
        thermostatSeekBar = (SeekBar) findViewById(R.id.thermostatSeekBar);
        setThermostatSlider();

        // SET INITIAL STATUS FOR ALL SWITCHES
        lightsKitchen =     (Switch) findViewById(R.id.lights_kitchen);
        lightsLivingroom =  (Switch) findViewById(R.id.lights_livingroom);
        lightsBedroom =     (Switch) findViewById(R.id.lights_bedroom);
        lightsHall =        (Switch) findViewById(R.id.lights_hall);
        lightSwitches.add(lightsKitchen);
        lightSwitches.add(lightsLivingroom);
        lightSwitches.add(lightsBedroom);
        lightSwitches.add(lightsHall);
        windowsKitchen =    (Switch) findViewById(R.id.windows_kitchen);
        windowsLivingroom = (Switch) findViewById(R.id.windows_livingroom);
        windowsBedroom =    (Switch) findViewById(R.id.windows_bedroom);
        windowsHall =       (Switch) findViewById(R.id.windows_hall);
        windowSwitches.add(windowsKitchen);
        windowSwitches.add(windowsLivingroom);
        windowSwitches.add(windowsBedroom);
        windowSwitches.add(windowsHall);

        setSwitches();

    }

    public void handleToggle(View v) {
        if (v.getId() == 0xffffffff) {
            Log.d("ACTUATORS=>HANDLETOGGLE","no-id");
        } else {
            // edu.iit.paco.smartdashboard:id/lights_livingroom
            Log.d("ACTUATORS=>HANDLETOGGLE",v.getResources().getResourceName(v.getId()));
            String _switch = v.getResources().getResourceName(v.getId()).substring(31);
            final String[] splited = _switch.split("_");
            Log.d("ACTUATORS=>HANDLETOGGLE",_switch);
            Handler h = new Handler();
            // toggle selected switch
            h.post(new Runnable() {
                @Override
                public void run() {
                    SmartHomeHTTP smartHomeHTTP = new SmartHomeHTTP(homeURL);
                    String data = smartHomeHTTP.toggle(splited[0], splited[1]);
                }
            });
        }
    }

    private void setThermostatSlider() {
        thermostatSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String th = String.valueOf(progress/4+ 60);
                seekBarHeader.setText("THERMOSTAT - " + th + "ºF");
                // send th value to server
                SmartHomeHTTP smartHomeHTTP = new SmartHomeHTTP(homeURL);
                String data = smartHomeHTTP.thermostat(th);
            }
        });
    }

    // Wrap SmartHomeHTTP's function
    private String getJSONfield(String json, String field) {
        return SmartHomeHTTP.getJSONfield(json, field);
    }

    private void setSwitchesList(ArrayList<Switch> switchList, String actuatorsData) {
        int i = 0;
        for (Switch sw: switchList) {
            if (getJSONfield(actuatorsData,ROOMS[i]).equals("true")) sw.setChecked(true);
            else sw.setChecked(false);
            i++;
        }
    }

    private void setSwitches() {
        Handler h = new Handler();
        // Set value of lights and windows switches
        h.post(new Runnable() {
            @Override
            public void run() {
                SmartHomeHTTP smartHomeHTTP = new SmartHomeHTTP(homeURL);
                String data = smartHomeHTTP.getActuatorsStatus();
                Log.d("ACTUATORS_ACTIVITY", data);
                //set switches according to data
                setSwitchesList(lightSwitches, getJSONfield(data, "lights"));
                setSwitchesList(windowSwitches, getJSONfield(data, "windows"));
            }
        });
        // Set value of thermostat
        h.post(new Runnable() {
            @Override
            public void run() {
                SmartHomeHTTP smartHomeHTTP = new SmartHomeHTTP(homeURL);
                String data = smartHomeHTTP.getThermostatValue();
                Log.d("ACTUATORS_ACTIVITY", data);
                //set thermostat slider to actual thermostat value
                thermostatSeekBar.setProgress(Integer.valueOf(data));
                seekBarHeader.setText("THERMOSTAT - " + data + "ºF");
            }
        });
    }
}
