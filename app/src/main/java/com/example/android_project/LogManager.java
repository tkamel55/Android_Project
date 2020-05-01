package com.example.android_project;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LogManager {
    FirebaseDatabase db;
    DatabaseReference scoresTbl;
    ProgressDialog progressDialog;
    Context mContext;
    private boolean visibleProgress = false;
    private ScoreLogFeedbackListener scoreLogFeedbackListener;

    public LogManager(Context mContext, boolean visibleProgress, ScoreLogFeedbackListener scoreLogFeedbackListener) {
        this.mContext = mContext;
        this.visibleProgress = visibleProgress;
        this.scoreLogFeedbackListener = scoreLogFeedbackListener;
        db = FirebaseDatabase.getInstance();
        scoresTbl = db.getReference(StaticAccess.SCORES_TABLE);
        progressDialog = new ProgressDialog(mContext);
    }
    //@get all scores for a logged-in user
    public void getAllScoreLogByUserID(final long userID) {
        final List<Scores> scoresList = new ArrayList<>();
        if (visibleProgress) {
            showProgress();
        }
        scoresTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Scores detectScore = ds.getValue(Scores.class);
                        if (detectScore.getUserID() == userID) {
                            scoresList.add(detectScore);
                        }
                    }
                    if (scoresList.size() > 0) {
                        scoreLogFeedbackListener.logOfScores(scoresList);
                        hideProgress();
                    } else {
                        scoreLogFeedbackListener.noScoreLogFound();
                        hideProgress();
                    }
                } else {
                    scoreLogFeedbackListener.noScoreLogFound();
                    hideProgress();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                scoreLogFeedbackListener.noScoreLogFound();
                hideProgress();
            }

        });

        // hideProgress();

    }

    public void showProgress() {
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    //@get current month score for the logged in user
    public List<Scores> sortScoreByCurrentMonth(List<Scores> scoresList) {
        List<Scores> currentMonthScores = new ArrayList<>();
        for (Scores score : scoresList) {
            if (compareMonth(score.getCreatedAt())) {
                currentMonthScores.add(score);
            }
        }
        Collections.reverse(currentMonthScores);
        return currentMonthScores;
    }
    //@compare month
    public boolean compareMonth(String createdAt) {
        //Create 2 intances of calendar
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        //set the given date in one of the instance and current date in another
        try {
            cal1.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(createdAt));
            cal2.setTime(new Date());
            //now compare the dates using functions
            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) {
                    // the date falls in current month
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }

    }

    public interface ScoreLogFeedbackListener {
        void logOfScores(List<Scores> scoresList);

        void noScoreLogFound();

    }

}