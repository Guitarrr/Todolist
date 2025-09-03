package com.example.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class NoteHandler extends DatabaseHelper {

    public NoteHandler(Context context) {
        super(context);

    }

    public boolean create(Note note) {
        ContentValues values = new ContentValues();

        values.put("title", note.getTitle());
        values.put("description", note.getDescription());

        SQLiteDatabase db = this.getWritableDatabase();

        boolean isSuccessful = db.insert("Note", null, values) > 0 ;
        db.close();
        return isSuccessful;
    }

    public ArrayList<Note> readNotes() {

        ArrayList<Note> notes = new ArrayList<>();
        String sqlQuery = "SELECT * FROM Note ORDER BY id ASC";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlQuery, null);

        int id = 0 ;
        String title = "" ;
        String description = "" ;
        if(cursor.moveToFirst()) {
            do {

                int columnIndex = cursor.getColumnIndex("id");
                if(columnIndex != -1) {
                    id = Integer.parseInt(cursor.getString(columnIndex));
                }

                int columnIndexTitle = cursor.getColumnIndex("title");
                if(columnIndexTitle != -1) {
                    title = cursor.getString(columnIndexTitle);
                }

                int columnIndexDescription = cursor.getColumnIndex("description");
                if(columnIndexDescription != -1) {
                    description = cursor.getString(columnIndexDescription);
                }

                Note note = new Note(title, description);
                note.setId(id);
                notes.add(note);

            }while (cursor.moveToNext());

            cursor.close();
            db.close();
        }

        return notes ;
    }

    public Note readSingleNote(int id) {

        Note note = null ;
        String sqlQuery = "SELECT * FROM Note WHERE id = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        int noteId = 0 ;
        String title = "" ;
        String description = "" ;
        if(cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("id");
            if(columnIndex != -1) {
                noteId = Integer.parseInt(cursor.getString(columnIndex));
            }
            int columnIndexTitle = cursor.getColumnIndex("title");
            if(columnIndexTitle != -1) {
                title = cursor.getString(columnIndexTitle);
            }

            int columnIndexDescription = cursor.getColumnIndex("description");
            if(columnIndexDescription != -1) {
                description = cursor.getString(columnIndexDescription);
            }

            note = new Note(title, description);
            note.setId(noteId);
        }
        cursor.close();
        db.close();

        return note ;
    }

    public boolean update(Note note) {
        ContentValues values = new ContentValues();
        values.put("title", note.getTitle());
        values.put("description", note.getDescription());
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isSuccessful = db.update("Note", values, "id = " + note.getId(), null) > 0 ;
        db.close();
        return isSuccessful;

    }

    public boolean delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isDeleted = db.delete("Note", "id = " + id, null) > 0 ;
        db.close();
        return isDeleted;
    }

}
