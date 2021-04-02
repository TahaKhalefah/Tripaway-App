package com.tahadroid.tripaway.repository;

import androidx.lifecycle.LiveData;

import com.tahadroid.tripaway.data.local.TripDatabase;
import com.tahadroid.tripaway.data.local.relations.TripWithNotes;
import com.tahadroid.tripaway.data.local.relations.UserWithTrips;
import com.tahadroid.tripaway.models.Note;
import com.tahadroid.tripaway.models.Trip;
import com.tahadroid.tripaway.models.User;

import java.util.List;

public class TripRepository {
    private TripDatabase db;

    public TripRepository(TripDatabase db) {
        this.db = db;
    }

    public void insertUser(User user) {
        db.tripDao().insertUser(user);
    }

    public void insertNote(Note note) {
        db.tripDao().insertNote(note);
    }

    public void insertTrip(Trip trip) {
        db.tripDao().insertTrip(trip);
    }





    public void updateTrip(Trip trip) {
        db.tripDao().updateTrip(trip);
    }

    public void deleteTrip(Trip trip) {
        db.tripDao().deleteTrip(trip);
    }


    public LiveData<List<UserWithTrips>> getUsersWithTrips() {
        return db.tripDao().getUsersWithTrips();
    }


    public LiveData<List<TripWithNotes>> getTripsWithNotes() {
        return db.tripDao().getTripsWithNotes();
    }
}
