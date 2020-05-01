package com.example.android_project;

public class UserToUserRelation {
    private long userID_1;
    private long userID_2;

    public UserToUserRelation(long userID_1, long userID_2) {
        this.userID_1 = userID_1;
        this.userID_2 = userID_2;
    }

    public UserToUserRelation() {
    }

    public long getUserID_1() {
        return userID_1;
    }

    public void setUserID_1(long userID_1) {
        this.userID_1 = userID_1;
    }

    public long getUserID_2() {
        return userID_2;
    }

    public void setUserID_2(long userID_2) {
        this.userID_2 = userID_2;
    }
}
