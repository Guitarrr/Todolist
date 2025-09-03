package com.example.todo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EditNote extends AppCompatActivity {

    EditText editTitle, editDescription;
    Button btnDate, btnSave, btnCancel;
    String noteDate = "";
    int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        editTitle = findViewById(R.id.edit_edit_title);
        editDescription = findViewById(R.id.edit_edit_description);
        btnDate = findViewById(R.id.btn_edit_date);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        // Get data from intent
        Intent intent = getIntent();
        noteId = intent.getIntExtra("id", -1);
        editTitle.setText(intent.getStringExtra("title"));
        editDescription.setText(intent.getStringExtra("description"));
        noteDate = intent.getStringExtra("date");
        btnDate.setText(noteDate.isEmpty() ? "Pick Date" : noteDate);

        btnDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(EditNote.this,
                    (view, year, month, dayOfMonth) -> {
                        noteDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        btnDate.setText(noteDate);
                    },
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        btnSave.setOnClickListener(v -> saveNote());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveNote() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        Note note = new Note(noteId, title, description, noteDate);
        boolean updated = new NoteHandler(this).update(note);
        if (updated) {
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }
}
