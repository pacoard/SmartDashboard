package edu.iit.paco.smartdashboard;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "MeasurementsDB";

    private static final String HISTORY_TABLE_NAME = "history";

    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_TEMPERATURE = "temperature";
    private static final String KEY_HUMIDITY = "humidity";
    private static final String KEY_NOISE = "noiselevel";

    private static final String USER_TABLE_NAME = "users";

    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_HOME_URL = "homeurl";

    public DBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE "+HISTORY_TABLE_NAME+" ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, "+
                "temperature TEXT, humidity TEXT, noiselevel TEXT)");

        db.execSQL("CREATE TABLE "+USER_TABLE_NAME+" ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, " +
                "email TEXT UNIQUE, password TEXT, homeurl TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS "+HISTORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+USER_TABLE_NAME);
        // create fresh measurements table
        this.onCreate(db);
    }

    public boolean createUser(String name, String email, String password, String homeurl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_PASSWORD,password);
        values.put(KEY_HOME_URL,homeurl);

        try {
            db.insertWithOnConflict(USER_TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_ABORT);
        } catch (SQLiteConstraintException e) {
            // Show error message here
            return false;
        }

        Log.d("createUser",values.toString());
        db.close();
        return true;
    }

    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT password FROM "+ USER_TABLE_NAME + " WHERE email= '" + email +"'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst(); // There will be only ONE result, as email is an UNIQUE field
            String realPassword = cursor.getString(0);
            db.close();
            return password.equals(realPassword);
        } else {
            db.close();
            return false;
        }
    }
    public void getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+ USER_TABLE_NAME;
        
        List<String> rows = new LinkedList<String>();
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                // Add measurement to the list
                rows.add(c.getString(0)+","+c.getString(1)+","+c.getString(2)+","+c.getString(3)+","+c.getString(4));
            } while (c.moveToNext());
        }
        Log.d("USERS_TABLE", rows.toString());
    }

    public String getUserHomeURL(String email) {
        String url;
        String query = "SELECT " + KEY_HOME_URL + " FROM " + USER_TABLE_NAME + " WHERE "+KEY_EMAIL+" = '" + email +"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        url = cursor.getString(0);
        db.close();

        return url;
    }

    public String getUserName(String usermail) {
        String username;
        String query = "SELECT " + KEY_NAME + " FROM " + USER_TABLE_NAME + " WHERE "+KEY_EMAIL+" = '" + usermail +"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        username = cursor.getString(0);
        db.close();

        return username;
    }


    public void addDataRow(String t, String h, String nl){
        String date = new SimpleDateFormat("hh:mm:ss").format(new Date());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, date);
        values.put(KEY_TEMPERATURE, t);
        values.put(KEY_HUMIDITY,h);
        values.put(KEY_NOISE,nl);
        // 3. insert
        db.insert(HISTORY_TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/values
        // 4. Close dbase
        Log.d("addRow",values.toString());
        db.close();
    }

    // Get All temperature measurements given a certain Date
    public List<String> getTempHistory() {
        List<String> temperatures = new LinkedList<String>();
        // 1. build the query
        String query = "SELECT date, temperature FROM " + HISTORY_TABLE_NAME + " ORDER BY date ASC";
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // 3. go over each row
        boolean cursorOK = cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                // Add measurement to the list
                temperatures.add(cursor.getString(0) + "," + cursor.getString(1)); //Primary key is column 0
            } while (cursor.moveToNext());
        }
        Log.d("getTempHistory()", temperatures.toString());
        return temperatures; // return list
    }

    public List<String> getHumHistory() {
        List<String> humidities = new LinkedList<String>();
        // 1. build the query
        String query = "SELECT date,humidity FROM " + HISTORY_TABLE_NAME + " ORDER BY date ASC";
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // 3. go over each row
        if (cursor.moveToFirst()) {
            do {
                // Add measurement to the list
                humidities.add(cursor.getString(0) + "," + cursor.getString(1)); //Primary key is column 0
            } while (cursor.moveToNext());
        }
        Log.d("getHumHistory()", humidities.toString());
        return humidities; // return list
    }
    public List<String> getNoiseHistory() {
        List<String> noise = new LinkedList<String>();
        // 1. build the query
        String query = "SELECT date,noiselevel FROM " + HISTORY_TABLE_NAME + " ORDER BY date ASC";
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // 3. go over each row
        if (cursor.moveToFirst()) {
            do {
                // Add measurement to the list
                noise.add(cursor.getString(0) + "," + cursor.getString(1)); //Primary key is column 0
            } while (cursor.moveToNext());
        }
        Log.d("getNoiseHistory()", noise.toString());
        return noise; // return list
    }
    public String[] getLastRow() {
        String[] lastRow = new String[5];
        String query = "SELECT  * FROM " + HISTORY_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToLast()){
            lastRow[0] = cursor.getString(0); //Id
            lastRow[1] = cursor.getString(1); //Date
            lastRow[2] = cursor.getString(2); //Temperature
            lastRow[3] = cursor.getString(3); //Humidity
            lastRow[4] = cursor.getString(4); //Noise level

            //Logcat Information
            Log.d("Last measurement", "Last DB row: "+ Arrays.toString(lastRow));
        }
        return lastRow;
    }

}
