package edu.iit.paco.smartdashboard;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    private boolean serviceStarted = false;
    private String username;
    //private String homeURL;
    private String homeURL = PeriodicUpdateService.LOCALHOST_URL;

    private Button sensorsBtn;
    private Button actuatorsBtn;
    private Button statisticsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sensorsBtn = (Button) findViewById(R.id.btn_sensors);
        actuatorsBtn = (Button) findViewById(R.id.btn_actuators);
        statisticsBtn = (Button) findViewById(R.id.btn_statistics);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
        //  @Override
        // public void onClick(View view) {
        //        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        // .setAction("Action", null).show();
        //    };
        // });

        // Set username, get it from the database or intent

        // Set homeURL, get it from the database or intent

    }

    public void tryService(View v) {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), PeriodicUpdateAlarm.class);
        intent.putExtra("url", homeURL);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, PeriodicUpdateAlarm.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        // Star alarm
        if (!serviceStarted) {
            Log.d(TAG, "serviceStarded = true");
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    PeriodicUpdateAlarm.FETCH_INTERVAL_MILLIS, pIntent);
            serviceStarted = true;
            // Stop alarm
        } else {
            Log.d(TAG, "serviceStarded = false");
            alarm.cancel(pIntent);
            serviceStarted = false;
        }
    }

    public void sensorsBtn(View v) {
        Intent intent = new Intent(this, SensorsActivity.class);
        startActivity(intent);
    }
    public void actuatorsBtn(View v) {
        Intent intent = new Intent(this, ActuatorsActivity.class);
        startActivity(intent);
    }
    public void statisticsBtn(View v) {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }
}


