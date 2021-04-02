package com.tahadroid.tripaway.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.tahadroid.tripaway.data.local.relations.TripWithNotes;
import com.tahadroid.tripaway.data.local.relations.UserWithTrips;
import com.tahadroid.tripaway.models.Note;
import com.tahadroid.tripaway.models.Trip;
import com.tahadroid.tripaway.models.User;


import java.util.List;

@Dao
public interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrip(Trip trip);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTrip(Trip trip);

    @Delete
    void deleteTrip(Trip trip);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    @Transaction
    @Query("SELECT * FROM User")
    LiveData<List<UserWithTrips>> getUsersWithTrips(); //we can here get data by user id ;)

    @Transaction
    @Query("SELECT * FROM Trip")
        //notify table name will come back****??????
    LiveData<List<TripWithNotes>> getTripsWithNotes(); //we can here get data by trip id ;)
}
