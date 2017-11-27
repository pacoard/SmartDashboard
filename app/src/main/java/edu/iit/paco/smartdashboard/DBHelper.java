package edu.iit.paco.smartdashboard;

/**
 * Created by Paco on 21/11/2017.
 */

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Paco on 18/11/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 3;
    // Database Name
    private static final String DATABASE_NAME = "MeasurementsDB";
    // Books table name
    private static final String HISTORY_TABLE_NAME = "history";
    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_TEMPERATURE = "temperature";
    private static final String KEY_HUMIDITY = "humidity";
    private static final String KEY_NOISE = "noiselevel";

    public DBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_HISTORY_TABLE = "CREATE TABLE "+HISTORY_TABLE_NAME+" ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "date TEXT, "+
                "temperature TEXT, humidity TEXT, noiselevel TEXT, )";
        // create history table
        db.execSQL(CREATE_HISTORY_TABLE);

        // todo
        /*String CREATE_BOOK_TABLE = "CREATE TABLE "+USERS_TABLE_NAME+" ( " +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "date TEXT, "+
        "temperature TEXT, humidity TEXT, noiselevel TEXT, )";
        // create history table
        db.execSQL(CREATE_BOOK_TABLE);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older measurement table if existed
        db.execSQL("DROP TABLE IF EXISTS "+HISTORY_TABLE_NAME);
        // create fresh measurements table
        this.onCreate(db);
    }


    /*CRUD operations (create "add", read "get", update, delete) */
    //Paco adding to the DB table
    public void addDataRow(String date, String t, String h, String nl){
        //Log.d("addData", book.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        //values.put(KEY_TITLE, book.getTitle()); // get title
        //values.put(KEY_AUTHOR, book.getAuthor()); // get author
        //values.put(KEY_RATING, book.getRating()); // get rating
        // 3. insert
        db.insert(HISTORY_TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/values
        // 4. Close dbase
        db.close();
    }

    // Get All temperature measurements given a certain Date
    public List<String> getTempHistory(String date) {
        List<String> temperatures = new LinkedList<String>();
        // 1. build the query
        String query = "SELECT temperature FROM " + HISTORY_TABLE_NAME + " WHERE date =" + date.trim();
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // 3. go over each row
        if (cursor.moveToFirst()) {
            do {
                // Add measurement to the list
                temperatures.add(cursor.getString(1)); //Primary key is column 0
            } while (cursor.moveToNext());
        }
        Log.d("getTempHistory()", temperatures.toString());
        return temperatures; // return list
    }

    public List<String> getHumHistory(String date) {
        List<String> humidities = new LinkedList<String>();
        // 1. build the query
        String query = "SELECT humidity FROM " + HISTORY_TABLE_NAME + " WHERE date =" + date.trim();
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // 3. go over each row
        if (cursor.moveToFirst()) {
            do {
                // Add measurement to the list
                humidities.add(cursor.getString(1)); //Primary key is column 0
            } while (cursor.moveToNext());
        }
        Log.d("getHumHistory()", humidities.toString());
        return humidities; // return list
    }
    public List<String> getNoiseHistory(String date) {
        List<String> noise = new LinkedList<String>();
        // 1. build the query
        String query = "SELECT noiselevel FROM " + HISTORY_TABLE_NAME + " WHERE date =" + date.trim();
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // 3. go over each row
        if (cursor.moveToFirst()) {
            do {
                // Add measurement to the list
                noise.add(cursor.getString(1)); //Primary key is column 0
            } while (cursor.moveToNext());
        }
        Log.d("getNoiseHistory()", noise.toString());
        return noise; // return list
    }
    public String[] getLastRow() {
        String[] lastRow = new String[4];
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
