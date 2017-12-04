package edu.iit.paco.smartdashboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity {
    //Instance fields
    String selection = "";

    String[] sensors = new String[]{"Temperature", "Humidity", "Noise level"};
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.UK);
    Date x = new Date();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        final GraphView graph = (GraphView) findViewById(R.id.graph);
        final Spinner dropdown = (Spinner) findViewById(R.id.expandableList);


        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sensors);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        dropdown.setAdapter(aa);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int item = dropdown.getSelectedItemPosition();
                selection = sensors[item];
                //Call draw graph method
                try{
                    graph.removeAllSeries(); //Redraw the graph
                    graph.onDataChanged(false,false);
                    graph.getGridLabelRenderer().setTextSize(25);
                    drawGraph(graph);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void drawGraph(GraphView graph){
        DBHelper db = new DBHelper(this);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        Calendar cal = Calendar.getInstance();

        int nPoints = db.getNumberOfRows();
        //When the user selects date and sensor, query de BBDD for data
        if (selection.equals("Temperature")) {
            //Call get temperature history from the DB
            List<String> temperatures = db.getTempHistory();
            Log.d("Temperatures from DB", temperatures.toString());
            for (String item : temperatures) {
                String[] pairs = item.split(",");
                String[] time = pairs[0].split(":");
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                cal.set(Calendar.SECOND, Integer.parseInt(time[2]));
                x = cal.getTime();
                double y = Double.parseDouble(pairs[1]);
                series.appendData(new DataPoint(x, y), true, nPoints);
            }
            graph.setTitle("Temperature measures");
            graph.getViewport().setMinY(40);
            graph.getViewport().setMaxY(100);
            graph.getGridLabelRenderer().setVerticalAxisTitle("(ºF)");
        } else if (selection.equals("Humidity")) {
            //Call get humidity history from the DB
            List<String> humidity = db.getHumHistory();
            for (String item : humidity) {
                String[] pairs = item.split(",");
                String[] time = pairs[0].split(":");
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                cal.set(Calendar.SECOND, Integer.parseInt(time[2]));
                x = cal.getTime();
                double y = Double.parseDouble(pairs[1]);
                series.appendData(new DataPoint(x, y),true,nPoints);
            }
            graph.setTitle("Humidity measures");
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(110);
            graph.getGridLabelRenderer().setVerticalAxisTitle("(% humidity)");
        } else if (selection.equals("Noise level")) {
            //Call get noise history from the DB
            List<String> noise = db.getNoiseHistory();
            for (String item : noise) {
                String[] pairs = item.split(",");
                String[] time = pairs[0].split(":");
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                cal.set(Calendar.SECOND, Integer.parseInt(time[2]));
                x = cal.getTime();
                Log.d("Y sin round", pairs[1]);
                double y = Double.parseDouble(pairs[1]);
                y = Math.round(y*100.0)/100.0;
                Log.d("Y con round", String.valueOf(y));
                series.appendData(new DataPoint(x, y), true, nPoints);
            }
            graph.setTitle("Noise level measures");

            graph.getViewport().setMinY(5);
            graph.getViewport().setMaxY(150);
            graph.getGridLabelRenderer().setVerticalAxisTitle("(dB)");
        }

        db.close();

        //Define X with dates
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 01);
        Date d1 = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date d2 = cal.getTime();

        //set manual X bounds (time)
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getViewport().setMinX(d1.getTime());
        graph.getViewport().setMaxX(d2.getTime());

        //Label formatting
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this, dateFormat));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);
        graph.getGridLabelRenderer().setNumVerticalLabels(4);
        graph.getGridLabelRenderer().setHumanRounding(false);


        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScrollableY(true);
        graph.addSeries(series);
    }
}