package com.example.android_project;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PedometerManager {
    FirebaseDatabase db;
    DatabaseReference scoresTbl;
    ProgressDialog progressDialog;
    Context mContext;
    private boolean visibleProgress = false;
    private ScoreFeedbackListener scoreFeedbackListener;

    public PedometerManager(Context mContext, boolean visibleProgress, ScoreFeedbackListener scoreFeedbackListener) {
        this.mContext = mContext;
        this.visibleProgress = visibleProgress;
        this.scoreFeedbackListener = scoreFeedbackListener;
        db = FirebaseDatabase.getInstance();
        scoresTbl = db.getReference(StaticAccess.SCORES_TABLE);
        progressDialog = new ProgressDialog(mContext);
    }
    ///@save a score to firebase database
    public void savedUserScore(final Scores scores) {
        if (visibleProgress) {
            showProgress();
        }
        scoresTbl.push().setValue(scores, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                    hideProgress();
                    scoreFeedbackListener.scoreUpdateFailed(scores);
                } else {
                    System.out.println("Data saved successfully.");
                    hideProgress();
                    scoreFeedbackListener.scoreUpdateUpSuccess(scores);

                }

            }
        });

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

    public interface ScoreFeedbackListener {
        void scoreUpdateUpSuccess(Scores scores);

        void scoreUpdateFailed(Scores scores);

    }

}
