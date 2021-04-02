package com.tahadroid.tripaway.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.tahadroid.tripaway.models.Note;
import com.tahadroid.tripaway.models.Trip;
import com.tahadroid.tripaway.models.User;

import static com.tahadroid.tripaway.utils.Constants.DATABASE_NAME;

@Database(
        entities = {
                User.class,
                Note.class,
                Trip.class
        }, version = 1
)
public abstract class TripDatabase extends RoomDatabase {
    //will use it to get data
    public abstract TripDao tripDao();

    //to make singlton pattern and not allow other threads make new instanse from it
    private static TripDatabase INSTANCE = null;

    // we make it static to use to call by class
    // synchronized to save from other thread use in one time
    public static synchronized TripDatabase getINSTANCE(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext()
                    , TripDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}
