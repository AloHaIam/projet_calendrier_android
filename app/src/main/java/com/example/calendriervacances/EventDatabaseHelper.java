package com.example.calendriervacances;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import java.util.List;
import java.util.ArrayList;


public class EventDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 1;

    public EventDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE events (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, date INTEGER)";
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS events");
        onCreate(db);
    }

    public void addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", event.getTitle());
        values.put("description", event.getDescription());
        values.put("date", event.getDate());

        db.insert("events", null, values);
        db.close();
    }

    public List<Event> getEventsForDay(long dateInMillis) {
        List<Event> events = new ArrayList<>();
        // convert dateInMillis to a format that can be compared with the date in your database

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("events", null, "date = ?", new String[] { String.valueOf(dateInMillis) }, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                long date = cursor.getLong(cursor.getColumnIndex("date"));

                Event event = new Event(id, title, description, date);
                events.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return events;
    }
}
