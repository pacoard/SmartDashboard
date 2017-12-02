package edu.iit.paco.smartdashboard;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Interface with the server
 *
 * https://www.learn2crack.com/2013/10/android-asynctask-json-parsing-example.html
 * https://developer.android.com/reference/android/os/AsyncTask.html
 * https://code.tutsplus.com/tutorials/android-from-scratch-using-rest-apis--cms-27117
 */

/* // How to use SmartHomeHTTP to update UI
 handler.post(new Runnable() {
    @Override
    public void run() {
        SmartHomeHTTP sh = new SmartHomeHTTP("https://api.github.com/");
        String temp = sh.getTemperature();
        textView.setText(temp);
    }
});
*/
public class SmartHomeHTTP {

    private String homeURL;

    public SmartHomeHTTP(String homeURL) {
        this.homeURL = homeURL;
    }

    public static String getJSONfield(String json, String field) {
        //JSON parsing
        String value = "";
        try {
            JSONObject obj = new JSONObject(json);
            value = obj.getString(field);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return value;
    }

    public String getActuatorsStatus() {
        String t = "";
        SmartHomeHTTPAsyncTask shat = new SmartHomeHTTPAsyncTask();
        try {
            String res = shat.execute(homeURL.concat("/actuator/status")).get();
            t = res;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return t;
    }

    public String getSensorsData() {
        String t = "";
        SmartHomeHTTPAsyncTask shat = new SmartHomeHTTPAsyncTask();
        try {
            String res = shat.execute(homeURL.concat("/sensor/fullData")).get();
            t = res;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return t;
    }

    public String toggle(String whichActuator, String whichRoom) {
        String t = "";
        SmartHomeHTTPAsyncTask shat = new SmartHomeHTTPAsyncTask();
        try {
            String res = shat.execute(homeURL.concat("/actuator/"+whichActuator+"/"+whichRoom)).get();
            t = res;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return t;
    }

    public String thermostat(String value) {
        String t = "";
        SmartHomeHTTPAsyncTask shat = new SmartHomeHTTPAsyncTask();
        try {
            String res = shat.execute(homeURL.concat("/actuator/thermostat/"+value)).get();
            t = getJSONfield(res, "data");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return t;
    }

    public String getThermostatValue() {
        String t = "";
        SmartHomeHTTPAsyncTask shat = new SmartHomeHTTPAsyncTask();
        try {
            String res = shat.execute(homeURL.concat("/actuator/thermostat/value")).get();
            t = getJSONfield(res, "data");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return t;
    }

    private class SmartHomeHTTPAsyncTask extends AsyncTask<String,Void,String>{
        private URL homeURL;
        private HttpURLConnection conn;
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            URL homeURL;
           /* try {
                homeURL = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }*/
            try {
                homeURL = new URL(params[0]);
                conn = (HttpURLConnection) homeURL.openConnection();
                if (conn.getResponseCode() == 200) {
                    // Success
                    InputStream respInputStream = conn.getInputStream();
                    InputStreamReader respInputStreamReader = new InputStreamReader(respInputStream, "UTF-8");
                    //Get raw response body

                    BufferedReader br = new BufferedReader(respInputStreamReader);
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    result = sb.toString();

                    //JSON parsing
                    /*JsonReader jsonReader = new JsonReader(respBodyReader);
                    jsonReader.beginObject(); // Start processing the JSON object
                    while (jsonReader.hasNext()) { // Loop through all keys
                        String key = jsonReader.nextName(); // Fetch the next key
                        if (key.equals("organization_url")) { // Check if desired key
                            // Fetch the value as a String
                            result = jsonReader.nextString();

                            // Do something with the value

                            break; // Break out of the loop
                        } else {
                            jsonReader.skipValue(); // Skip values of other keys
                        }
                    }
                    jsonReader.close();
                    */
                    conn.disconnect();
                } else {
                    // Error handling
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

    }
}


