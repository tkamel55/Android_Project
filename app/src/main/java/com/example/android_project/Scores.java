package com.example.android_project;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class Scores implements Comparable<Scores> {
    private long scoreID;
    private long userID;
    private String startTime;
    private String endTime;
    private String steps;
    private String calories;
    private String distance;
    private String speed;
    private String pace;
    private String createdAt;
    private String updatedAt;

    public Scores(long scoreID, long userID, String startTime, String endTime, String steps, String calories, String distance, String speed, String pace, String createdAt, String updatedAt) {
        this.scoreID = scoreID;
        this.userID = userID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.steps = steps;
        this.calories = calories;
        this.distance = distance;
        this.speed = speed;
        this.pace = pace;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Scores() {
    }

    public long getScoreID() {
        return scoreID;
    }

    public void setScoreID(long scoreID) {
        this.scoreID = scoreID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String toString() {
        return String.valueOf("ScoreID: " + scoreID) + String.valueOf("UserID: " + userID) + "Steps: " + steps;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int compareTo(@NonNull Scores o) {
        return Long.compare(Long.parseLong(this.getSteps()), Long.parseLong(o.getSteps()));
    }


}
