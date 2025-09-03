package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditNote extends AppCompatActivity {

    EditText editTitle, editDescription;
    Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTitle = findViewById(R.id.edit_edit_title);
        editDescription = findViewById(R.id.edit_edit_description);
        Intent intent = getIntent();
        editTitle.setText(intent.getStringExtra("title"));
        editDescription.setText(intent.getStringExtra("description"));
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = new Note(editTitle.getText().toString(), editDescription.getText().toString());
                note.setId(getIntent().getIntExtra("id", 1));
                if(new NoteHandler(EditNote.this).update(note)) {
                    Toast.makeText(EditNote.this, "Note Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditNote.this, "Failed to update", Toast.LENGTH_SHORT).show();
                }
                setResult(RESULT_OK);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

}
