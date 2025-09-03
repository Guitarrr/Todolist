package com.example.todo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageButton imageButton;
    ArrayList<Note> notes;
    RecyclerView recyclerView;
    NoteAdapter noteAdapter;
    private ActivityResultLauncher<Intent> editNoteLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadNotes();
                    }
                });

        imageButton = findViewById(R.id.add_button);
        imageButton.setOnClickListener(v -> showAddNoteDialog());

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                new NoteHandler(MainActivity.this)
                        .delete(notes.get(viewHolder.getAdapterPosition()).getId());
                notes.remove(viewHolder.getAdapterPosition());
                noteAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        loadNotes();
    }

    private void showAddNoteDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.note_input, null, false);

        EditText editTitle = view.findViewById(R.id.edit_title);
        EditText editDescription = view.findViewById(R.id.edit_description);
        Button btnPickDate = view.findViewById(R.id.btn_pick_date);
        Button btnToday = view.findViewById(R.id.btn_today);
        Button btnTomorrow = view.findViewById(R.id.btn_tomorrow);
        Button btnSave = view.findViewById(R.id.btn_save);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        final String[] selectedDate = {""};

        btnPickDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view1, year, month, dayOfMonth) -> {
                        selectedDate[0] = dayOfMonth + "/" + (month + 1) + "/" + year;
                        btnPickDate.setText(selectedDate[0]);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        btnToday.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            selectedDate[0] = c.get(Calendar.DAY_OF_MONTH) + "/" +
                    (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
            btnPickDate.setText(selectedDate[0]);
        });

        btnTomorrow.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, 1);
            selectedDate[0] = c.get(Calendar.DAY_OF_MONTH) + "/" +
                    (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
            btnPickDate.setText(selectedDate[0]);
        });

        // Create AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialog)
                .setView(view)
                .create();

        // Make dialog background transparent so CardView corners show properly
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String description = editDescription.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }

            Note note = new Note(title, description, selectedDate[0]);
            boolean isInserted = new NoteHandler(MainActivity.this).create(note);

            if (isInserted) {
                Toast.makeText(MainActivity.this, "Note Added", Toast.LENGTH_SHORT).show();
                loadNotes();
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Note Not Added", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    public void loadNotes() {
        notes = new NoteHandler(this).readNotes();

// Sort by nearest date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Collections.sort(notes, new Comparator<Note>() {
            @Override
            public int compare(Note n1, Note n2) {
                try {
                    Date date1 = n1.getDate().isEmpty() ? new Date(Long.MAX_VALUE) : sdf.parse(n1.getDate());
                    Date date2 = n2.getDate().isEmpty() ? new Date(Long.MAX_VALUE) : sdf.parse(n2.getDate());
                    return date1.compareTo(date2); // nearest date first
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        noteAdapter = new NoteAdapter(notes, this, (position, view) -> editNote(notes.get(position).getId(), view));
        recyclerView.setAdapter(noteAdapter);
    }

    private void editNote(int noteId, View view) {
        Note note = new NoteHandler(this).readSingleNote(noteId);
        Intent intent = new Intent(this, EditNote.class);
        intent.putExtra("title", note.getTitle());
        intent.putExtra("description", note.getDescription());
        intent.putExtra("date", note.getDate());
        intent.putExtra("id", note.getId());

        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, ViewCompat.getTransitionName(view));
        editNoteLauncher.launch(intent);
    }
}
