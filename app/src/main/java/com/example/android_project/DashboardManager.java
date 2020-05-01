package com.example.android_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardManager implements PedometerManager.ScoreFeedbackListener {
    Context context;
    PedometerManager pedometerManager;
    ScoreDbManager scoreDbManager;
    DashboardFeedbackListener dashboardFeedbackListener;
    FirebaseDatabase db;
    DatabaseReference usersTbl;
    DatabaseReference usersToUserRelationTbl;
    DatabaseReference scoreTbl;
    ProgressDialog progressDialog;
    private boolean visibleProgress = false;

    public DashboardManager(Context context, boolean visibleProgress, DashboardFeedbackListener dashboardFeedbackListener) {
        this.context = context;
        this.dashboardFeedbackListener = dashboardFeedbackListener;
        pedometerManager = new PedometerManager(context, true, this);
        scoreDbManager = new ScoreDbManager(context);
        this.visibleProgress = visibleProgress;
        db = FirebaseDatabase.getInstance();
        usersTbl = db.getReference(StaticAccess.USERS_TABLE);
        usersToUserRelationTbl = db.getReference(StaticAccess.USERS_TO_USER_RELATION_TABLE);
        scoreTbl = db.getReference(StaticAccess.SCORES_TABLE);
        progressDialog = new ProgressDialog(context);

    }

    //@get user information by userID
    User detectUser = null;

    public void getUserByID(final long userID) {
        if (visibleProgress) {
            showProgress();
        }
        usersTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue(User.class).getUserID() == userID) {
                        detectUser = ds.getValue(User.class);
                        dashboardFeedbackListener.getUser(detectUser);
                        hideProgress();
                        break;
                    }
                }
                if (detectUser == null) {
                    dashboardFeedbackListener.noUserFound();
                    hideProgress();
                }
                // hideProgress();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgress();
            }
        });
    }

    public void getUserRelationByUserID(final long userID_1) {
        final List<UserToUserRelation> relationList = new ArrayList<>();
        usersToUserRelationTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getValue(UserToUserRelation.class).getUserID_1() == userID_1) {
                            relationList.add(ds.getValue(UserToUserRelation.class));
                        }
                    }
                    if (relationList.size() > 0) {
                        dashboardFeedbackListener.getRelationList(relationList);
                    }
                } else {
                    dashboardFeedbackListener.noRelationListFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgress();
            }
        });
    }

    public void relationListWiseAllUsers(List<UserToUserRelation> relationList) {
        for (UserToUserRelation relation : relationList) {
            getUserInfoByID(relation.getUserID_2());
        }
    }

    void getUserInfoByID(final long userID) {
        usersTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue(User.class).getUserID() == userID) {
                        dashboardFeedbackListener.getRelationUserInfo(ds.getValue(User.class));
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //hideProgress();
            }
        });
    }

    public void getScoreByID(final User user) {
        final List<Scores> scoresList = new ArrayList<>();
        scoreTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getValue(Scores.class).getUserID() == user.getUserID()) {
                            scoresList.add(ds.getValue(Scores.class));
                        }
                    }
                    if (scoresList.size()>0) {
                        Collections.sort(scoresList);
                        Collections.sort(scoresList, Collections.reverseOrder());
                        dashboardFeedbackListener.scoreFound(true, scoresList.get(0), user);
                        dashboardFeedbackListener.topUsersAvailable();
                        Log.e("####", "TopUsers: " + scoresList.get(0).toString());
                    }else {
                        dashboardFeedbackListener.scoreFound(false, null, user);
                    }

                } else {
                    dashboardFeedbackListener.scoreFound(false, null, user);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //hideProgress();
                dashboardFeedbackListener.scoreFound(false, null, user);
            }
        });


    }

    @Override
    public void scoreUpdateUpSuccess(Scores scores) {
        scoreDbManager.deleteScore(scores);
    }

    @Override
    public void scoreUpdateFailed(Scores scores) {
        Log.e("firebase:", "score update failed");
    }

    //update current day all score saved in sqlite
    public void updateUserCurrentDayAllData() {
        List<Scores> scoresList = scoreDbManager.getAllScores();
        if (scoresList.size() > 0) {
            for (Scores score : scoresList) {
                pedometerManager.savedUserScore(score);
            }
            dashboardFeedbackListener.userCurrentDayDataUpdated();
        } else {
            Toast.makeText(context, "No more score to update", Toast.LENGTH_SHORT).show();
        }
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


    public interface DashboardFeedbackListener {

        void getUser(User user);

        void noUserFound();

        void userCurrentDayDataUpdated();

        void noRelationListFound();

        void getRelationList(List<UserToUserRelation> relationList);

        void getRelationUserInfo(User user);

        void scoreFound(boolean scoreFound, Scores score, User user);

        void topUsersAvailable();
    }

}
