package com.example.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class NoteHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NoteDatabase";
    private static final int DATABASE_VERSION = 2; // Incremented for 'date' column
    private static final String TABLE_NAME = "notes";

    public NoteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table with all columns including 'date'
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "description TEXT," +
                "date TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade safely: only add 'date' column if the table exists and version < 2
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME + "'", null);
        boolean tableExists = cursor.getCount() > 0;
        cursor.close();

        if (tableExists && oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN date TEXT");
        }
    }

    // -----------------------
    // CRUD OPERATIONS
    // -----------------------

    // Create a new note
    public boolean create(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", note.getTitle());
        values.put("description", note.getDescription());
        values.put("date", note.getDate());

        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }

    // Read all notes
    public ArrayList<Note> readNotes() {
        ArrayList<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                notes.add(new Note(id, title, description, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }

    // Read a single note by ID
    public Note readSingleNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?", new String[]{String.valueOf(id)});
        Note note = null;
        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            note = new Note(id, title, description, date);
        }
        cursor.close();
        db.close();
        return note;
    }

    // Update a note
    public boolean update(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", note.getTitle());
        values.put("description", note.getDescription());
        values.put("date", note.getDate());

        int rows = db.update(TABLE_NAME, values, "id=?", new String[]{String.valueOf(note.getId())});
        db.close();
        return rows > 0;
    }

    // Delete a note
    public boolean delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }
}
