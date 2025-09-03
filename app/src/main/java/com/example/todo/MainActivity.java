package com.example.todo;

import static android.app.ProgressDialog.show;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageButton imageButton;
    ArrayList<Note> notes ;
    RecyclerView recyclerView ;
    NoteAdapter noteAdapter ;
    private ActivityResultLauncher<Intent> editNoteLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            loadNotes();
                        }
                    });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageButton = findViewById(R.id.add_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.note_input, null, false);

                EditText editTitle = view.findViewById(R.id.edit_title);
                EditText editDescription = view.findViewById(R.id.edit_description);
                Button btnSave = view.findViewById(R.id.btn_save);
                Button btnCancel = view.findViewById(R.id.btn_cancel);

                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialog)
                        .setView(view)
                        .create();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = editTitle.getText().toString().trim();
                        String description = editDescription.getText().toString().trim();

                        if (title.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please enter a title", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Note note = new Note(title, description);
                        boolean isInserted = new NoteHandler(MainActivity.this).create(note);

                        if (isInserted) {
                            Toast.makeText(MainActivity.this, "Note Added", Toast.LENGTH_SHORT).show();
                            loadNotes();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Note Not Added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {


                new NoteHandler(MainActivity.this).delete(notes.get(viewHolder.getAdapterPosition()).getId());
                notes.remove(viewHolder.getAdapterPosition());
                noteAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        loadNotes();

    }

    public ArrayList<Note> readNotes() {
        ArrayList<Note> notes = new NoteHandler(this).readNotes();
        return notes ;
    }

    public void loadNotes(){
        notes = readNotes();
        noteAdapter = new NoteAdapter(notes, this, new NoteAdapter.ItemClicked(){
            @Override
            public void onClick(int position, View view) {
                editNote(notes.get(position).getId(), view);

            }
        });
        recyclerView.setAdapter(noteAdapter);
    }

    private void editNote(int noteId, View view) {
        NoteHandler noteHandler = new NoteHandler(this);
        Note note = noteHandler.readSingleNote(noteId);
        Intent intent = new Intent(this, EditNote.class);
        intent.putExtra("title", note.getTitle());
        intent.putExtra("description", note.getDescription());
        intent.putExtra("id", note.getId());

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, ViewCompat.getTransitionName(view));
        editNoteLauncher.launch(intent);
    }
}