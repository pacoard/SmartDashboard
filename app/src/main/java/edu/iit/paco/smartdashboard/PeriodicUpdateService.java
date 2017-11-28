package edu.iit.paco.smartdashboard;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Paco on 19/11/2017.
 */

public class PeriodicUpdateService extends IntentService {
        public PeriodicUpdateService() {
            super("PeriodicUpdateService");
        }

        public static final String HOME_URL_PARAM = "HOME_URL";
        public static final String LOCALHOST_URL = "http://10.0.2.2:8000";


        private SmartHomeHTTP smartHomeHTTP;

        // Filter function, in case a different ACTION is requested
        @Override
        protected void onHandleIntent(Intent intent) {
            String homeURL = intent.getStringExtra(HOME_URL_PARAM);
            smartHomeHTTP = new SmartHomeHTTP(homeURL);
           // while (true) {
                String data = smartHomeHTTP.getSensorsData();
                updateDB(data);
            //    SystemClock.sleep(FETCH_INTERVAL_MILLIS);
           // }
        }

        public void updateDB(String data) {
            Log.d("updateDB", "Updating DB...");
            Log.d("updateDB", data);

            DBHelper db = new DBHelper(this);
            try {
                JSONObject obj = new JSONObject(data);
                db.addDataRow(obj.getString("temperature"),
                              obj.getString("humidity"),
                              obj.getString("noiseLevel")
                             );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            db.getLastRow();
        }
}