package com.example.android_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ScoreDbManager extends SQLiteOpenHelper {

    // All Static variables
// Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ScoreManager";

    // score table name
    private static final String TABLE_SCORES = "Scores";

    // score Table Columns names
    private static final String KEY_SCOREID = "scoreID";
    private static final String KEY_USERID = "userID";
    private static final String KEY_STARTTIME = "startTime";
    private static final String KEY_ENDTIME = "endTime";
    private static final String KEY_STEPS = "steps";
    private static final String KEY_CALORIES = "calories";
    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_SPEED = "speed";
    private static final String KEY_PACE = "pace";
    private static final String KEY_CREATED_AT = "createdAt";
    private static final String KEY_UPDATED_AT = "updatedAt";
    private static final String KEY_STATUS = "status";

    public ScoreDbManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SCORE_TABLE = "CREATE TABLE " + TABLE_SCORES + "("
                + KEY_SCOREID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_USERID + " TEXT,"
                + KEY_STARTTIME + " TEXT," + KEY_ENDTIME + " TEXT,"
                + KEY_STEPS + " TEXT," + KEY_CALORIES + " TEXT,"
                + KEY_DISTANCE + " TEXT," + KEY_SPEED + " TEXT,"
                + KEY_PACE + " TEXT," + KEY_CREATED_AT + " TEXT,"
                + KEY_UPDATED_AT + " TEXT," + KEY_STATUS + " INTEGER" + ")";
        db.execSQL(CREATE_SCORE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

// Adding new score
    public long addScore(Scores score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, score.getUserID());
        values.put(KEY_STARTTIME, score.getStartTime());
        values.put(KEY_ENDTIME, score.getEndTime());
        values.put(KEY_STEPS, score.getSteps());
        values.put(KEY_CALORIES, score.getCalories());
        values.put(KEY_DISTANCE, score.getDistance());
        values.put(KEY_SPEED, score.getSpeed());
        values.put(KEY_PACE, score.getPace());
        values.put(KEY_CREATED_AT, score.getCreatedAt());
        values.put(KEY_UPDATED_AT, score.getUpdatedAt());
        values.put(KEY_STATUS, 1);

        // Inserting Row
        long inserted = db.insert(TABLE_SCORES, null, values);
        db.close(); // Closing database connection
        return inserted;
    }

    // Getting single score
    Scores getScore(int scoreID) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SCORES, new String[]{KEY_SCOREID,
                        KEY_USERID, KEY_STARTTIME, KEY_ENDTIME, KEY_STEPS,
                        KEY_CALORIES, KEY_DISTANCE, KEY_SPEED, KEY_PACE,
                        KEY_CREATED_AT, KEY_UPDATED_AT, KEY_STATUS}, KEY_SCOREID + "=?",
                new String[]{String.valueOf(scoreID)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Scores score = new Scores(Long.parseLong(cursor.getString(0)),
                Long.parseLong(cursor.getString(1)),
                cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(5),
                cursor.getString(6), cursor.getString(7),
                cursor.getString(8), cursor.getString(9),
                cursor.getString(10));
        // return score
        return score;
    }

    // Getting All scores
    public List<Scores> getAllScores() {
        List<Scores> scoresList = new ArrayList<Scores>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SCORES + " WHERE status=1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Scores score = new Scores();
                    score.setScoreID(Long.parseLong(cursor.getString(0)));
                    score.setUserID(Long.parseLong(cursor.getString(1)));
                    score.setStartTime(cursor.getString(2));
                    score.setEndTime(cursor.getString(3));
                    score.setSteps(cursor.getString(4));
                    score.setCalories(cursor.getString(5));
                    score.setDistance(cursor.getString(6));
                    score.setSpeed(cursor.getString(7));
                    score.setPace(cursor.getString(8));
                    score.setCreatedAt(cursor.getString(9));
                    score.setUpdatedAt(cursor.getString(10));
                    // Adding score to list
                    scoresList.add(score);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {

        }

        // return score list
        return scoresList;
    }

    // Getting last scores
    public Scores getTheLastScores(long userID) {
        Scores score = null;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SCORES + " WHERE userID=" + userID + " order by scoreID DESC LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            score = new Scores(Long.parseLong(cursor.getString(0)),
                    Long.parseLong(cursor.getString(1)),
                    cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5),
                    cursor.getString(6), cursor.getString(7),
                    cursor.getString(8), cursor.getString(9),
                    cursor.getString(10));
        }
        // return score
        return score;
    }


    // Deleting single score
    public void deleteScore(Scores scores) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCORES, KEY_SCOREID + " = ?",
                new String[]{String.valueOf(scores.getScoreID())});
        db.close();
    }   // Deleting single score

    public void updateScoreStatus(Scores scores) {

        ContentValues data=new ContentValues();
        data.put(KEY_STATUS, 0);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_SCORES, data, "scoreID=" + scores.getScoreID(), null);
        db.close();
    }

    // Getting score Count
    public int getScoreCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SCORES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }

}