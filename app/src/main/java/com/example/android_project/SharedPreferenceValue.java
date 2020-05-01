package com.example.android_project;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class SharedPreferenceValue {


    public static void setLoggedInUser(Context context, long userID) {
        SharedPreferences settings = context.getSharedPreferences("loggedUser", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("userID", userID);
        editor.commit();
    }

    public static long getLoggedinUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences("loggedUser", 0);
        long userID = settings.getLong("userID", -1);
        return userID;
    }

    public static void clearLoggedInuserData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("loggedUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public static void setLastScore(Context context, Scores scores) {
        SharedPreferences settings = context.getSharedPreferences("score", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("step", new Gson().toJson(scores));
        editor.commit();
    }

    public static Scores getLastScore(Context context) {
        SharedPreferences settings = context.getSharedPreferences("score", 0);
        String scoreJson = settings.getString("step", "");
        try {
            Scores scores = new Gson().fromJson(scoreJson, Scores.class);
            return scores;
        } catch (Exception e) {
            return null;
        }

    }

    public static void clearLastScore(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("score", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

}