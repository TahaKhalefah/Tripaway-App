package com.tahadroid.tripaway.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "mUserId",
        childColumns = "mUserCreatorTripId",
        onDelete = CASCADE))
public class Trip implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int mTripId;
    public String mTripTitle;
    public String mTripDescription;
    public String mTripDate;
    public String mTripTime;
    public String mStartAddress;
    public String mEndAddress;
    public double mTripStartLatitude;
    public double mTripStartLongitude;
    public double mTripEndLatitude;
    public double mTripEndLongitude;
    public boolean mIsAsync;
    public boolean mIsDone;
    public boolean mIsRound;
    public boolean mStatus;
    public int mUserCreatorTripId;

    public Trip() {
    }

    public Trip(String mTripTitle, String mTripDescription, String mTripDate, String mTripTime, String mStartAddress,
                String mEndAddress, double mTripStartLatitude, double mTripStartLongitude, double mTripEndLatitude,
                double mTripEndLongitude, boolean mIsAsync, boolean mIsDone, boolean mIsRound, boolean mStatus, int mUserCreatorTripId) {
        this.mTripTitle = mTripTitle;
        this.mTripDescription = mTripDescription;
        this.mTripDate = mTripDate;
        this.mTripTime = mTripTime;
        this.mStartAddress = mStartAddress;
        this.mEndAddress = mEndAddress;
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

    public String getmStartAddress() {
        return mStartAddress;
    }

    public void setmStartAddress(String mStartAddress) {
        this.mStartAddress = mStartAddress;
    }

    public String getmEndAddress() {
        return mEndAddress;
    }

    public void setmEndAddress(String mEndAddress) {
        this.mEndAddress = mEndAddress;
    }

    public boolean ismIsDone() {
        return mIsDone;
    }

    public void setmIsDone(boolean mIsDone) {
        this.mIsDone = mIsDone;
    }

    public boolean ismIsRound() {
        return mIsRound;
    }

    public void setmIsRound(boolean mIsRound) {
        this.mIsRound = mIsRound;
    }

    public boolean ismStatus() {
        return mStatus;
    }

    public void setmStatus(boolean mStatus) {
        this.mStatus = mStatus;
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
