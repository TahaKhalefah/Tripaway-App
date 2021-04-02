package com.tahadroid.tripaway.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Trip.class,
        parentColumns = "mTripId",
        childColumns = "TripHaveNoteId",
        onDelete = CASCADE))
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true) public int mNoteId;
    public String mNoteTitle;
    public String mNoteDescription;
    public int TripHaveNoteId;

    public Note() {
    }

    public Note(String mNoteTitle, String mNoteDescription, int tripHaveNoteId) {
        this.mNoteTitle = mNoteTitle;
        this.mNoteDescription = mNoteDescription;
        this.TripHaveNoteId = tripHaveNoteId;
    }

    public int getmNoteId() {
        return mNoteId;
    }

    public void setmNoteId(int mNoteId) {
        this.mNoteId = mNoteId;
    }

    public String getmNoteTitle() {
        return mNoteTitle;
    }

    public void setmNoteTitle(String mNoteTitle) {
        this.mNoteTitle = mNoteTitle;
    }

    public String getmNoteDescription() {
        return mNoteDescription;
    }

    public void setmNoteDescription(String mNoteDescription) {
        this.mNoteDescription = mNoteDescription;
    }

    public int getTripHaveNoteId() {
        return TripHaveNoteId;
    }

    public void setTripHaveNoteId(int tripHaveNoteId) {
        TripHaveNoteId = tripHaveNoteId;
    }

}
