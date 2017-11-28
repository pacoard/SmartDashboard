package edu.iit.paco.smartdashboard;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
//https://guides.codepath.com/android/Starting-Background-Services

// To use this alarm:
/*
    // Bind this method to an on/off button
    public void tryService(View v) {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), PeriodicUpdateAlarm.class);
        intent.putExtra("url", PeriodicUpdateService.LOCALHOST_URL);
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
*/

public class PeriodicUpdateAlarm extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final int FETCH_INTERVAL_MILLIS = 2000;
    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, PeriodicUpdateService.class);
        i.putExtra(PeriodicUpdateService.HOME_URL_PARAM, intent.getStringExtra("url"));
        context.startService(i);
    }
}


