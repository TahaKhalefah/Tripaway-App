package com.tahadroid.tripaway.data.local.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.tahadroid.tripaway.models.Note;
import com.tahadroid.tripaway.models.Trip;

import java.util.List;

public class TripWithNotes {
    @Embedded
    public Trip trip;
    @Relation(
            parentColumn = "mTripId",
            entityColumn = "TripHaveNoteId"
    )
    public List<Note> noteList;
}
