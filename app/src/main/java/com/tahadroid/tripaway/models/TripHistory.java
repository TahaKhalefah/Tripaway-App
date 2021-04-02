package com.tahadroid.tripaway.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

@Entity
public class TripHistory implements Serializable {
    @PrimaryKey(autoGenerate = true) public int mTripId;
    public String mTripTitle;
    public String mTripDescription;
    public String mTripDate;
    public String mTripTime;
    public double mTripStartLatitude;
    public double mTripStartLongitude;
    public double mTripEndLatitude;
    public double mTripEndLongitude;
    public boolean mIsAsync;
    public boolean mIsDone;
    public boolean mIsRound;
    public boolean mStatus;
    public int mUserCreatorTripId;

    public TripHistory() {
    }



    public TripHistory(String mTripTitle, String mTripDescription, String mTripDate,
                       String mTripTime, double mTripStartLatitude, double mTripStartLongitude, double mTripEndLatitude,
                       double mTripEndLongitude, boolean mIsAsync, boolean mIsDone, boolean mIsRound,boolean mStatus, int mUserCreatorTripId) {
        this.mTripTitle = mTripTitle;
        this.mTripDescription = mTripDescription;
        this.mTripDate = mTripDate;
        this.mTripTime = mTripTime;
        this.mTripStartLatitude = mTripStartLatitude;
        this.mTripStartLongitude = mTripStartLongitude;
        this.mTripEndLatitude = mTripEndLatitude;
        this.mTripEndLongitude = mTripEndLongitude;
        this.mIsAsync = mIsAsync;
        this.mIsDone = mIsDone;
        this.mIsRound = mIsRound;
        this.mStatus = mStatus;
        this.mUserCreatorTripId = mUserCreatorTripId;
    }

    public int getmTripId() {
        return mTripId;
    }

    public void setmTripId(int mTripId) {
        this.mTripId = mTripId;
    }

    public String getmTripTitle() {
        return mTripTitle;
    }

    public void setmTripTitle(String mTripTitle) {
        this.mTripTitle = mTripTitle;
    }

    public String getmTripDescription() {
        return mTripDescription;
    }

    public void setmTripDescription(String mTripDescription) {
        this.mTripDescription = mTripDescription;
    }

    public String getmTripDate() {
        return mTripDate;
    }

    public void setmTripDate(String mTripDate) {
        this.mTripDate = mTripDate;
    }

    public String getmTripTime() {
        return mTripTime;
    }

    public void setmTripTime(String mTripTime) {
        this.mTripTime = mTripTime;
    }

    public double getmTripStartLatitude() {
        return mTripStartLatitude;
    }

    public void setmTripStartLatitude(double mTripStartLatitude) {
        this.mTripStartLatitude = mTripStartLatitude;
    }

    public double getmTripStartLongitude() {
        return mTripStartLongitude;
    }

    public void setmTripStartLongitude(double mTripStartLongitude) {
        this.mTripStartLongitude = mTripStartLongitude;
    }

    public double getmTripEndLatitude() {
        return mTripEndLatitude;
    }

    public void setmTripEndLatitude(double mTripEndLatitude) {
        this.mTripEndLatitude = mTripEndLatitude;
    }

    public double getmTripEndLongitude() {
        return mTripEndLongitude;
    }

    public void setmTripEndLongitude(double mTripEndLongitude) {
        this.mTripEndLongitude = mTripEndLongitude;
    }

    public boolean ismIsAsync() {
        return mIsAsync;
    }

    public void setmIsAsync(boolean mIsAsync) {
        this.mIsAsync = mIsAsync;
    }

    public int getmUserCreatorTripId() {
        return mUserCreatorTripId;
    }

    public void setmUserCreatorTripId(int mUserCreatorTripId) {
        this.mUserCreatorTripId = mUserCreatorTripId;
    }
}
