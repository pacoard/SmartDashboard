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
import java.util.List;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity {
    //Instance fields
    String selection = "";
    DBHelper db = new DBHelper(this);
    String[] sensors = new String[]{"Select measurements type", "Temperature", "Humidity", "Noise level"};
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
                Toast.makeText(getApplicationContext(), "You have selected: " + selection, Toast.LENGTH_SHORT).show();
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

        //Testing for adding info to the DB
//        db.addDataRow("00:20:32", "20.041122180002684","48.35922736859142","12.227408465577994");
//        db.addDataRow("01:00:50", "20.765050540236928","47.15570451018764", "10.923370173717544");
//        db.addDataRow("02:00:21", "19.547655333140245", "44.073918845187656","10.006581184493278");
//        db.addDataRow("03:00:04", "20.262159195640834", "45.38633631962485", "10.536616586652425");
//        db.addDataRow("04:15:41", "19.616120420792043", "46.27357853462845", "11.599742245037943");
//        db.addDataRow("05:00:09" , "19.349833139244016", "48.4316264968579", "11.801602889585174");
//        db.addDataRow("06:11:52" , "20.711819625692925", "49.787798400225675", "13.034600927010114");
//        db.addDataRow("07:36:18" , "19.997791719216625", "44.12262253550275", "10.940817934808635");
//        db.addDataRow("08:00:00" , "19.812754224939454", "48.661157583745975", "11.490997316755173");
//        db.addDataRow("09:40:22" , "20.258171789671707", "48.71653399252755", "13.275809649912905");
//        db.addDataRow("10:00:15" , "20.337749386842745", "49.9327190159387", "13.334231142811134");
//        db.addDataRow("11:00:39" , "20.35050906337924", "46.36483603471886", "13.967911849784173");
//        db.addDataRow("12:00:05" , "20.924044520678624", "46.940870162440554", "12.678945963556368");
//        db.addDataRow("13:00:18" , "19.461763132414568", "45.8321452752859", "12.365516892696789");
//        db.addDataRow("14:00:29" , "20.10819262320041", "44.950445224450355", "10.040860366697524");
//        db.addDataRow("15:25:57" , "20.87209798425871", "48.876335371806114", "10.555778193053833");
//        db.addDataRow("16:00:40" , "19.690903666220645", "47.87210672242834", "12.588802535764437");
//        db.addDataRow("17:00:20" , "19.707027534000225", "44.47468903478703", "12.41895097219489");
//        db.addDataRow("18:00:17" , "20.22515682284179", "46.57811594186302", "12.020328136987391");
//        db.addDataRow("19:00:08" , "20.125137924520182", "48.20511371138322", "10.759504206915931");
//        db.addDataRow("20:00:31" , "19.530685833943917", "49.2403920992688", "13.0704659143457");
//        db.addDataRow("21:00:26" , "20.709761351504483", "49.14491765949006", "13.963722685379384");
//        db.addDataRow("22:00:38" , "20.973198672354197", "44.086274847709284", "10.51557678561749");
//        db.addDataRow("23:00:56" , "19.53776683548128", "49.79635861257178", "13.404795723990311");

    }

    public void drawGraph(GraphView graph){
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        Calendar cal = Calendar.getInstance();

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
                series.appendData(new DataPoint(x, y), true, 1000);
            }
            graph.setTitle("Temperature measures");
            graph.getViewport().setYAxisBoundsManual(false);
            graph.getGridLabelRenderer().setVerticalAxisTitle("(ºC)");
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
                series.appendData(new DataPoint(x, y),true,1000);
            }
            graph.setTitle("Humidity measures");
            graph.getViewport().setYAxisBoundsManual(false);
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
                series.appendData(new DataPoint(x, y), true, 1000);
            }
            graph.setTitle("Noise level measures");
            graph.getViewport().setYAxisBoundsManual(false);
            graph.getGridLabelRenderer().setVerticalAxisTitle("(dB)");
        }

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
        graph.getViewport().setMinX(d1.getTime());
        graph.getViewport().setMaxX(d2.getTime());

        //Label formatting
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext(), dateFormat));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);
        graph.getGridLabelRenderer().setHumanRounding(false);


        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.addSeries(series);

    }
}