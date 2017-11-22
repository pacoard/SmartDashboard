package edu.iit.paco.smartdashboard;

/**
 * Created by Paco on 21/11/2017.
 */

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
    private static final String DATABASE_NAME = "BookDB";
    // Books table name
    private static final String HISTORY_TABLE_NAME = "history";
    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_RATING = "rating";

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
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS "+HISTORY_TABLE_NAME);
        // create fresh books table
        this.onCreate(db);
    }

    /*CRUD operations (create "add", read "get", update, delete) */
    public void addDataRow(String date, String t, String h, String nl){
        Log.d("addBook", book.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, book.getTitle()); // get title
        values.put(KEY_AUTHOR, book.getAuthor()); // get author
        values.put(KEY_RATING, book.getRating()); // get rating
        // 3. insert
        db.insert(TABLE_BOOKS, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/values
        // 4. Close dbase
        db.close();
    }

    // Get All Books
    public List<Book> getTempHistory() {
        List<Book> books = new LinkedList<Book>();
        // 1. build the query
        String query = "SELECT * FROM " + TABLE_BOOKS;
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // 3. go over each row, build book and add it to list
        Book book = null;
        if (cursor.moveToFirst()) {
            do {
                book = new Book();
                book.setId(Integer.parseInt(cursor.getString(0)));
                book.setTitle(cursor.getString(1));
                book.setAuthor(cursor.getString(2));
                book.setRating(cursor.getInt(3));
                // Add book to books
                books.add(book);
            } while (cursor.moveToNext());
        }
        Log.d("getAllBooks()", books.toString());
        return books; // return books
    }

    public List<Book> getHumHistory() {}
    public List<Book> getNoiseHistory() {}
    public String[] getLastRow() {return null}

}}
