package edu.iit.paco.smartdashboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SensorsActivity extends AppCompatActivity {
    private EditText tempEditText;
    private EditText humEditText;
    private EditText noiseEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        tempEditText = (EditText) findViewById(R.id.editTextTemperature);
        humEditText = (EditText) findViewById(R.id.editTextHumidity);
        noiseEditText = (EditText) findViewById(R.id.editTextNoise);
        refresh(null);

    }

    public void refresh(View v) {
        DBHelper db = new DBHelper(this);
        String[] data = db.getLastRow();
        db.close();

        tempEditText.setText(data[2]);
        humEditText.setText(data[3]);
        noiseEditText.setText(data[4]);
    }
}
