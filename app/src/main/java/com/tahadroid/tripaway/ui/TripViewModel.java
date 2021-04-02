package com.tahadroid.tripaway.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tahadroid.tripaway.data.local.relations.TripWithNotes;
import com.tahadroid.tripaway.data.local.relations.UserWithTrips;
import com.tahadroid.tripaway.models.Note;
import com.tahadroid.tripaway.models.Trip;
import com.tahadroid.tripaway.models.User;
import com.tahadroid.tripaway.repository.TripRepository;

import java.util.List;

public class TripViewModel extends ViewModel {
    private TripRepository tripRepository;

    public TripViewModel(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public void insertUser(User user) {
          tripRepository.insertUser(user);
    }

    public void insertNote(Note note) {
         tripRepository.insertNote(note);
    }

    public void insertTrip(Trip trip) {
         tripRepository.insertTrip(trip);
    }


    public void updateTrip(Trip trip) {
        tripRepository.updateTrip(trip);
    }

    public void deleteTrip(Trip trip) {
        tripRepository.deleteTrip(trip);
    }



    public LiveData<List<UserWithTrips>> getUsersWithTrips() {
        return tripRepository.getUsersWithTrips();
    }

    public LiveData<List<TripWithNotes>> getTripsWithNotes() {
        return tripRepository.getTripsWithNotes();
    }
}
