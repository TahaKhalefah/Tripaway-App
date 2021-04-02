package com.tahadroid.tripaway.data.local.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.tahadroid.tripaway.models.Trip;
import com.tahadroid.tripaway.models.User;

import java.util.List;

public class UserWithTrips {
    @Embedded public User user;
    @Relation(
            parentColumn = "mUserId",
            entityColumn = "mUserCreatorTripId"
    )
   public List<Trip> tripList;
}
