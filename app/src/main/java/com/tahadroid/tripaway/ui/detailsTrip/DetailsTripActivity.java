package com.tahadroid.tripaway.ui.detailsTrip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tahadroid.tripaway.R;
import com.tahadroid.tripaway.data.local.TripDatabase;
import com.tahadroid.tripaway.data.local.relations.TripWithNotes;
import com.tahadroid.tripaway.models.Note;
import com.tahadroid.tripaway.models.Trip;
import com.tahadroid.tripaway.repository.TripRepository;
import com.tahadroid.tripaway.ui.TripViewModel;
import com.tahadroid.tripaway.ui.TripViewModelFactory;
import com.tahadroid.tripaway.ui.map.NotesAdapter;

import java.util.ArrayList;
import java.util.List;

public class DetailsTripActivity extends AppCompatActivity {
    private TripViewModel tripViewModel;
    private TripRepository tripRepository;
    private TripViewModelFactory tripViewModelFactory;
    private Trip trip;
    private TextView TripDesc;
    private TextView StartPoint;
    private TextView EndPoint;
    private TextView Date;
    private TextView Time;
    private RecyclerView recyclerView;
    private TextView statue;
    private TextView done, textViewNoNotesDetails;
    private NotesAdapter notesAdapter;

    private List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_trip);

        tripRepository = new TripRepository(TripDatabase.getINSTANCE(this));
        tripViewModelFactory = new TripViewModelFactory(tripRepository);
        tripViewModel = new ViewModelProvider(this, tripViewModelFactory).get(TripViewModel.class);
        noteList = new ArrayList<>();
        trip = (Trip) getIntent().getSerializableExtra("tripKey");

        setTitle(trip.mTripTitle + " " + "(Report)");

        TripDesc = findViewById(R.id.tripNameDetails);
        StartPoint = findViewById(R.id.startPlaceDetails);
        EndPoint = findViewById(R.id.endPlaceDetails);
        Date = findViewById(R.id.calender_text);
        textViewNoNotesDetails = findViewById(R.id.textViewNoNotesDetails);
        Time = findViewById(R.id.timer_text);
        recyclerView = findViewById(R.id.notesDetailsRV);
        statue = findViewById(R.id.statusDetails);
        done = findViewById(R.id.doneDetails);

        TripDesc.setText(trip.mTripDescription);
        Date.setText(trip.mTripDate);
        Time.setText(trip.mTripTime);
        StartPoint.setText(trip.mStartAddress);
        EndPoint.setText(trip.mEndAddress);
        notesAdapter = new NotesAdapter();

        tripViewModel.getTripsWithNotes().observe(this, tripWithNotes -> {
            noteList.clear();
            for (TripWithNotes tripWithNotes1 : tripWithNotes) {
                if (tripWithNotes1.trip.mTripId == trip.mTripId) {
                    noteList = tripWithNotes1.noteList;
                }
            }
            if (noteList.size() < 0) {
                recyclerView.setVisibility(View.GONE);
                textViewNoNotesDetails.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                textViewNoNotesDetails.setVisibility(View.GONE);
                notesAdapter.setList(noteList);
                recyclerView.setAdapter(notesAdapter);
            }

        });
        if (trip.mStatus) {
            statue.setText("Canceled");
        } else {
            statue.setText("Completed");
        }
        if (trip.mIsDone) {
            done.setText("Done");
        } else {
            done.setText("Continue");
        }
    }
}