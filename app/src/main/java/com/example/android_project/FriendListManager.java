package com.example.android_project;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendListManager {
    Context context;
    FriendListFeedbackListener friendListFeedbackListener;
    FirebaseDatabase db;
    DatabaseReference usersTbl;
    DatabaseReference usersToUserRelationTbl;
    ProgressDialog progressDialog;
    private boolean visibleProgress = false;

    public FriendListManager(Context context, boolean visibleProgress, FriendListFeedbackListener friendListFeedbackListener) {
        this.context = context;
        this.friendListFeedbackListener = friendListFeedbackListener;
        this.visibleProgress = visibleProgress;
        db = FirebaseDatabase.getInstance();
        usersTbl = db.getReference(StaticAccess.USERS_TABLE);
        usersToUserRelationTbl = db.getReference(StaticAccess.USERS_TO_USER_RELATION_TABLE);
        progressDialog = new ProgressDialog(context);

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
                        friendListFeedbackListener.getRelationList(relationList);
                    }
                } else {
                    friendListFeedbackListener.noRelationListFound();
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
                        friendListFeedbackListener.getRelationUserInfo(ds.getValue(User.class));
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

    User detectUser = null;

    /// @search a friend in online
    public void searchFriend(final long userID, final String keyword, final List<UserToUserRelation> relationList) {
        final List<User> userList = new ArrayList<>();

        if (visibleProgress) {
            showProgress();
        }

        usersTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    detectUser = ds.getValue(User.class);
                    if (detectUser.getUserID() != userID && !checkIfAlreadyFriend(relationList, detectUser.getUserID())) {
                        if (detectUser.getFirstName().contains(keyword) ||
                                detectUser.getLastName().contains(keyword) ||
                                detectUser.getEmail().contains(keyword) ||
                                detectUser.getAddress().contains(keyword)) {
                            userList.add(detectUser);
                        }
                        /*usersToUserRelationTbl.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (ds.getValue(UserToUserRelation.class).getUserID_1() == userID) {
                                        detectUserToUserRelation = ds.getValue(UserToUserRelation.class);
                                        if (detectUserToUserRelation.getUserID_2() != detectUser.getUserID()) {
                                            userList.add(detectUser);
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //hideProgress();
                            }
                        });*/
                    }
                }
                if (userList.size() > 0) {
                    friendListFeedbackListener.friendListFound(userList);
                    hideProgress();
                } else {
                    friendListFeedbackListener.noFriendFound();
                    hideProgress();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                friendListFeedbackListener.noFriendFound();
                hideProgress();
            }

        });

        // hideProgress();

    }

    private boolean checkIfAlreadyFriend(List<UserToUserRelation> relationList, long userID) {
        boolean isFriend = false;
        for (UserToUserRelation relation : relationList) {
            if (relation.getUserID_2() == userID) {
                isFriend = true;
                break;
            }
        }
        return isFriend;
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

    //@add a friend
    public void addFriend(final long userID, final User user) {
        if (visibleProgress) {
            showProgress();
        }
        usersToUserRelationTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    UserToUserRelation userToUserRelation = new UserToUserRelation();
                    userToUserRelation.setUserID_1(userID);
                    userToUserRelation.setUserID_2(user.getUserID());
                    usersToUserRelationTbl.push().setValue(userToUserRelation, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                System.out.println("Data could not be saved " + databaseError.getMessage());
                                hideProgress();
                                friendListFeedbackListener.addFriendFailed();

                            } else {
                                System.out.println("Data saved successfully.");
                                hideProgress();
                                friendListFeedbackListener.addFriendSuccess();
                            }

                        }
                    });
                } else {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getValue(UserToUserRelation.class).getUserID_1() != userID && ds.getValue(UserToUserRelation.class).getUserID_2() != user.getUserID()) {
                            UserToUserRelation userToUserRelation = new UserToUserRelation();
                            userToUserRelation.setUserID_1(userID);
                            userToUserRelation.setUserID_2(user.getUserID());
                            usersToUserRelationTbl.push().setValue(userToUserRelation, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        System.out.println("Data could not be saved " + databaseError.getMessage());
                                        hideProgress();
                                        friendListFeedbackListener.addFriendFailed();

                                    } else {
                                        System.out.println("Data saved successfully.");
                                        hideProgress();
                                        friendListFeedbackListener.addFriendSuccess();
                                    }

                                }
                            });
                        } else {
                            hideProgress();
                            friendListFeedbackListener.alreadyFriend();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgress();
            }
        });


    }


    public interface FriendListFeedbackListener {
        void friendListFound(List<User> userList);

        void noFriendFound();

        void addFriendSuccess();

        void addFriendFailed();

        void alreadyFriend();

        void noRelationListFound();

        void getRelationList(List<UserToUserRelation> relationList);

        void getRelationUserInfo(User user);
    }
}