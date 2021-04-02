package com.tahadroid.tripaway.ui.fragments.trips;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tahadroid.tripaway.R;
import com.tahadroid.tripaway.data.local.TripDatabase;
import com.tahadroid.tripaway.data.local.relations.TripWithNotes;
import com.tahadroid.tripaway.models.Note;
import com.tahadroid.tripaway.models.Trip;
import com.tahadroid.tripaway.models.User;
import com.tahadroid.tripaway.repository.TripRepository;
import com.tahadroid.tripaway.ui.TripViewModel;
import com.tahadroid.tripaway.ui.TripViewModelFactory;
import com.tahadroid.tripaway.ui.addTrip.AddTripDetailsActivity;
import com.tahadroid.tripaway.ui.map.FloatWidgetService;
import com.tahadroid.tripaway.ui.map.Maps;

import java.util.ArrayList;
import java.util.List;

public class TripsFragment extends Fragment implements TripAdapter.RecyclerViewClickListener {
    private TripViewModel tripViewModel;
    private TripRepository tripRepository;
    private TripViewModelFactory tripViewModelFactory;
    private RecyclerView tripRecyclerView;
    private TripAdapter tripAdapter;

    private User user;
    private List<Trip> trips;
    private List<Note> notes;

    private Maps maps;
    private Dialog addNoteDialog;
    private Dialog deleteDialog;

    public TripsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tripRecyclerView = view.findViewById(R.id.tripsRecyclerView);
        tripRepository = new TripRepository(TripDatabase.getINSTANCE(getContext()));
        tripViewModelFactory = new TripViewModelFactory(tripRepository);
        tripViewModel = new ViewModelProvider(this, tripViewModelFactory).get(TripViewModel.class);
        trips = new ArrayList<>();
        notes = new ArrayList<>();
        notes.clear();
        tripAdapter = new TripAdapter(getContext(), this);
        tripViewModel.getUsersWithTrips().observe(getViewLifecycleOwner(), userWithTrips -> {
            trips.clear();
            user = userWithTrips.get(0).user;
            for (Trip trip : userWithTrips.get(0).tripList)
                if (!trip.mIsDone && !trip.mStatus)
                    trips.add(trip);
            tripAdapter.setList(trips);
            tripRecyclerView.setAdapter(tripAdapter);

        });
        maps = new Maps(getContext());

        addNoteDialog = new Dialog(getContext());
        deleteDialog = new Dialog(getContext());

    }



    @Override
    public void recyclerViewListClicked(View v, Trip trip, int position) {
        if (v.getId() == R.id.startUpcomeBtn) {
            maps.openMapsDirectedToDestination(trip.mTripEndLatitude, trip.mTripEndLongitude);
            tripViewModel.getTripsWithNotes().observe(getViewLifecycleOwner(), tripWithNotes -> {
                notes.clear();
                for (TripWithNotes tripWithNotes1 : tripWithNotes) {
                    if (tripWithNotes1.trip.mTripId == trip.mTripId)
                        notes.addAll(tripWithNotes1.noteList);
                    // Check for Permissions then start The Widget Service
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext())) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getActivity().getPackageName()));
                        startActivityForResult(intent, 106);
                    } else {
                        Intent startIntent = new Intent(getContext(), FloatWidgetService.class);
                        startIntent.putExtra("notes", (ArrayList<Note>) notes);
                        getActivity().startService(startIntent);
                    }
                }
            });
        } else if (v.getId() == R.id.addNoteUpcomeBtn) {
            showAddNoteDialog(trip);
        } else if (v.getId() == R.id.doneUpcomeBtn) {
            trip.mIsDone = true;
            tripViewModel.updateTrip(trip);
            tripAdapter.removeAt(position);

        } else if (v.getId() == R.id.deleteUpcomeIV) {
            showDeleteDialog(trip, position);
        } else if (v.getId() == R.id.updateUpcomeTripIV) {
            Intent toaddActivity =new Intent(getContext(), AddTripDetailsActivity.class);
            toaddActivity.putExtra("tripKey",trip);
            startActivity(toaddActivity);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showAddNoteDialog(Trip trip) {
        addNoteDialog.setContentView(R.layout.dialog_add_note);
        addNoteDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.dialog_shape));
        Button AddNoteDialogBtn = addNoteDialog.findViewById(R.id.AddNoteDialogBtn);
        Button cancelNoteDialogBtn = addNoteDialog.findViewById(R.id.cancelNoteDialogBtn);
        EditText noteTitleET = addNoteDialog.findViewById(R.id.noteTitleET);
        EditText noteDescET = addNoteDialog.findViewById(R.id.noteDescET);
        AddNoteDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteName = noteTitleET.getText().toString();
                String noteDescription = noteDescET.getText().toString();
                tripViewModel.insertNote(new Note(noteName, noteDescription, trip.mTripId));
                addNoteDialog.cancel();
            }
        });
        cancelNoteDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNoteDialog.cancel();
                Toast.makeText(getContext(), "Note Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        addNoteDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showDeleteDialog(Trip trip, int position) {
        deleteDialog.setContentView(R.layout.dialog_delete_trip);
        deleteDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.dialog_shape));
        Button yesDeleteBtn = deleteDialog.findViewById(R.id.yesDeleteBtn);
        Button noDeleteBtn = deleteDialog.findViewById(R.id.noDeleteBtn);
        yesDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trip.mStatus = true;
                tripViewModel.updateTrip(trip);
                tripAdapter.removeAt(position);
                deleteDialog.cancel();

            }
        });
        noDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.cancel();
                Toast.makeText(getContext(), "Delete Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        deleteDialog.show();
    }
}