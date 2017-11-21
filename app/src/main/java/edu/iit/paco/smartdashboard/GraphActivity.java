package edu.iit.paco.smartdashboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity {

    //Instance fields
    String source = "";
    String selection = "";
    String[] sensors = new String[]{"Temperature", "Humidity", "Noise level"};
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.UK);
    Date x = new Date();
    LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        final Spinner dropdown = (Spinner)findViewById(R.id.expandableList);

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,sensors);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        dropdown.setAdapter(aa);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

         @Override
         public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
             int item = dropdown.getSelectedItemPosition();
             selection = sensors[item];
             Toast.makeText(getApplicationContext(), "You have selected: " + selection, Toast.LENGTH_SHORT).show();
         }
         @Override
         public void onNothingSelected(AdapterView<?> adapterView) {

         }
        });

        //When the user selects date and sensor, query de BBDD for data

        //Read information from the file
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("TEMPERATURE.txt")));

            // do reading, usually loop until end of file reading
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                //Get source of the measurements
                if (currentLine.toLowerCase().contains("temperature")){
                    source = "TEMPERATURE";
                } else if (currentLine.toLowerCase().contains("humidity")){
                    source = "HUMIDITY";
                } else if (currentLine.toLowerCase().contains("noise level")){
                    source = "NOISE";
                } else if (currentLine.contains(",")){
                    //Add X value
                    try {
                        Log.d("HOURS",currentLine.substring(0,2).trim());
                        Log.d("MINUTES",currentLine.substring(3,5).trim());
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(currentLine.substring(0,2).trim()));
                        cal.set(Calendar.MINUTE,Integer.parseInt(currentLine.substring(3,5).trim()));
                         x = cal.getTime();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Add Y value
                    double y = Double.parseDouble(currentLine.substring(7,10).trim());
                    Log.d("MEDIDAy", currentLine.substring(7,10).trim());
                    //Add new measurement to de Datapoint set
                    series.appendData(new DataPoint(x,y),true, 24);
                }
            }
        } catch (IOException e) {
            System.err.println("An IOException was caught :"+e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("An IOException was caught :"+e.getMessage());
                }
            }
        }
        graph.addSeries(series);

        //Define X and Y axis depending the source
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,00);
        cal.set(Calendar.MINUTE,00);
        Date d1 = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
        Date d2 = cal.getTime();


        if (source.equals("TEMPERATURE")) {
            //set manual X bounds for TEMPERATURE (time)
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(d1.getTime());
            graph.getViewport().setMaxX(d2.getTime());

            // set manual Y bounds TEMPERATURE
            graph.getViewport().setYAxisBoundsManual(false);
        } else if (source.equals("HUMIDITY")){
            // set manual X bounds for HUMIDITY (time)
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(24);

            // set manual Y bounds HUMIDITY
            graph.getViewport().setYAxisBoundsManual(false);
        } else if (source.equals("NOISE")){
            // set manual X bounds for NOISE LEVEL (time)
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(24);

            // set manual Y bounds NOISE LEVEL
            graph.getViewport().setYAxisBoundsManual(false);
        }


        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext(),dateFormat));
        graph.getGridLabelRenderer().setNumHorizontalLabels(5);
        graph.getGridLabelRenderer().setHumanRounding(false);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
    }
}