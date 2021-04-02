package com.tahadroid.tripaway.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    public int mUserId;
    public String mUserName;
    public String mUserEmail;
    public String mUserPassword;
    public String mUserProfilePicture;

    public User() {
    }

    public User(String mUserName, String mUserEmail, String mUserPassword, String mUserProfilePicture) {
        this.mUserName = mUserName;
        this.mUserEmail = mUserEmail;
        this.mUserPassword = mUserPassword;
        this.mUserProfilePicture = mUserProfilePicture;
    }

    public int getmUserId() {
        return mUserId;
    }

    public void setmUserId(int mUserId) {
        this.mUserId = mUserId;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmUserEmail() {
        return mUserEmail;
    }

    public void setmUserEmail(String mUserEmail) {
        this.mUserEmail = mUserEmail;
    }

    public String getmUserPassword() {
        return mUserPassword;
    }

    public void setmUserPassword(String mUserPassword) {
        this.mUserPassword = mUserPassword;
    }

    public String getmUserProfilePicture() {
        return mUserProfilePicture;
    }

    public void setmUserProfilePicture(String mUserProfilePicture) {
        this.mUserProfilePicture = mUserProfilePicture;
    }
}
