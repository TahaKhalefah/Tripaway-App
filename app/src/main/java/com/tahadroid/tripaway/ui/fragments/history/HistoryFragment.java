package com.tahadroid.tripaway.ui.fragments.history;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.tahadroid.tripaway.R;
import com.tahadroid.tripaway.data.local.TripDatabase;
import com.tahadroid.tripaway.models.Trip;
import com.tahadroid.tripaway.repository.TripRepository;
import com.tahadroid.tripaway.ui.TripViewModel;
import com.tahadroid.tripaway.ui.TripViewModelFactory;
import com.tahadroid.tripaway.ui.detailsTrip.DetailsTripActivity;
import com.tahadroid.tripaway.ui.map.AllTripsMapActivity;
import com.tahadroid.tripaway.ui.map.SingleTripMapActivity;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements HistoryAdapter.HistoryViewClickListener {

    private TripViewModel tripViewModel;
    private TripRepository tripRepository;
    private TripViewModelFactory tripViewModelFactory;
    private RecyclerView tripHistoryRecyclerView;
    private HistoryAdapter historyAdapter;
    private List<Trip> trips;

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tripHistoryRecyclerView = view.findViewById(R.id.tripsHistoryRecyclerView);
        tripRepository = new TripRepository(TripDatabase.getINSTANCE(getContext()));
        tripViewModelFactory = new TripViewModelFactory(tripRepository);
        tripViewModel = new ViewModelProvider(this, tripViewModelFactory).get(TripViewModel.class);
        trips = new ArrayList<>();
        historyAdapter = new HistoryAdapter(getContext(), this);

        tripViewModel.getUsersWithTrips().observe(getViewLifecycleOwner(), userWithTrips -> {
            trips.clear();
            for (Trip trip : userWithTrips.get(0).tripList) {
                if (trip.mIsDone || trip.mStatus)
                    trips.add(trip);
            }
            historyAdapter.setList(trips);
            tripHistoryRecyclerView.setAdapter(historyAdapter);
        });


    }

    @Override
    public void historyViewListClicked(View v, Trip trip, int position) {
        switch (v.getId()) {
            case R.id.deleteTripHistoryIV: {
                tripViewModel.deleteTrip(trip);
                historyAdapter.removeAt(position);
            }
            break;
            case R.id.showDetailsHistoryBtn: {
                Intent toDetailsActivity = new Intent(getContext(), DetailsTripActivity.class);
                toDetailsActivity.putExtra("tripKey", trip);
                startActivity(toDetailsActivity);
            }
            break;
            case R.id.showOnMapBtn: {
                Intent intent = new Intent(getContext(), SingleTripMapActivity.class);
                intent.putExtra("long1", trip.mTripStartLongitude);
                intent.putExtra("lat1", trip.mTripStartLatitude);
                intent.putExtra("long2", trip.mTripEndLongitude);
                intent.putExtra("lat2", trip.mTripEndLatitude);
                startActivity(intent);
            }
            break;
        }
    }
}