package com.example.android_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener, DashboardManager.DashboardFeedbackListener{

    private RecyclerView rvTopUserList;
    private Button btnStartCounter, tvUpdateDashboard;
    private TextView tvFullName, tvMenuFullName, tvSteps, tvMenuSteps, tvStepLog, tvTrace, tvFriends, tvSearchFriends, tvShare, tvSetting;
    private ImageButton ibtnLogout, ibtnMenuClose, ibtnMenu;
    private DashboardActivity activity;
    private LinearLayout lnMenu;
    private View vBlankMenu;
    private DashboardManager dashboardManager;
    TopUsersListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initUI();
    }

    /// initializing the elements for the ui
    private void initUI() {
        activity = this;
        dashboardManager = new DashboardManager(activity, true, this);
        topUsers = new ArrayList<>();
        ibtnLogout = findViewById(R.id.ibtnLogout);
        rvTopUserList = findViewById(R.id.rvTopUserList);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        rvTopUserList.setLayoutManager(manager);
        rvTopUserList.setHasFixedSize(true);
        tvSteps = findViewById(R.id.tvSteps);
        tvMenuSteps = findViewById(R.id.tvMenuSteps);
        tvFullName = findViewById(R.id.tvFullName);
        tvUpdateDashboard = findViewById(R.id.tvUpdateDashboard);
        tvMenuFullName = findViewById(R.id.tvMenuFullName);
        ibtnMenu = findViewById(R.id.ibtnMenu);
        ibtnMenuClose = findViewById(R.id.ibtnMenuClose);
        lnMenu = findViewById(R.id.lnMenu);
        tvStepLog = findViewById(R.id.tvStepLog);
        tvTrace = findViewById(R.id.tvTrace);
        tvFriends = findViewById(R.id.tvFriends);
        tvSearchFriends = findViewById(R.id.tvSearchFriends);
        tvShare = findViewById(R.id.tvShare);
        tvSetting = findViewById(R.id.tvSetting);
        vBlankMenu = findViewById(R.id.vBlankMenu);
        btnStartCounter = findViewById(R.id.btnStartCounter);
        ibtnLogout.setOnClickListener(this);
        ibtnMenu.setOnClickListener(this);
        tvUpdateDashboard.setOnClickListener(this);
        tvStepLog.setOnClickListener(this);
        tvTrace.setOnClickListener(this);
        tvFriends.setOnClickListener(this);
        tvSearchFriends.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        ibtnMenuClose.setOnClickListener(this);
        vBlankMenu.setOnClickListener(this);
        btnStartCounter.setOnClickListener(this);
        if (ConnectionChecker.isOnline(activity)) {
            dashboardManager.getUserByID(SharedPreferenceValue.getLoggedinUser(activity));
        } else {
            Toast.makeText(activity, "No internet connection available", Toast.LENGTH_SHORT).show();
        }
        if (SharedPreferenceValue.getLastScore(activity) != null && SharedPreferenceValue.getLastScore(activity).getUserID() == SharedPreferenceValue.getLoggedinUser(activity)) {
            tvSteps.setText("Steps: " + SharedPreferenceValue.getLastScore(activity).getSteps());
            tvMenuSteps.setText("Steps: " + SharedPreferenceValue.getLastScore(activity).getSteps());
        }
//        dashboardManager.getTopUser(SharedPreferenceValue.getLoggedinUser(activity));
        dashboardManager.getUserRelationByUserID(SharedPreferenceValue.getLoggedinUser(activity));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtnLogout:
                logOutUser();
                break;
            case R.id.ibtnMenu:
                showMenu();
                break;
            case R.id.tvUpdateDashboard:
                if (ConnectionChecker.isOnline(activity)) {
                    dashboardManager.updateUserCurrentDayAllData();
                } else {
                    Toast.makeText(activity, "No internet connection available", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ibtnMenuClose:
                hideMenu();
                break;
            case R.id.vBlankMenu:
                hideMenu();
                break;
            case R.id.btnStartCounter:
                startPedoMeter();
                break;
            case R.id.tvTrace:
                startTraceActivity();
                break;
            case R.id.tvStepLog:
                startStepsLogActivity();
                break;
            case R.id.tvFriends:
                startFriendsActivity(1);
                break;
            case R.id.tvSearchFriends:
                startFriendsActivity(2);
                break;
            case R.id.tvShare:
                shareData();
                break;
            case R.id.tvSetting:
                startSettingActivity();
                break;
        }
    }

    /// 1 refers to friend list, 2 refers to search friend list
    private void startFriendsActivity(int from) {
        Intent friIntent = new Intent(activity, FriendsActivity.class);
        friIntent.putExtra(StaticAccess.KEY_FRIENDS_LIST_ACTIVITY, from);
        startActivity(friIntent);
        finish();
    }

    // start log screen
    private void startStepsLogActivity() {
        startActivity(new Intent(activity, LogActivity.class));
        finish();
    }

    /// start counter screen
    private void startPedoMeter() {
        startActivity(new Intent(activity, Pedometer.class));
        finish();
    }

    //start trace map screen
    private void startTraceActivity() {
        startActivity(new Intent(getApplicationContext(), Trace.class));
    }

    /// start setting screen
    private void startSettingActivity() {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }

    /// share data method
    private void shareData() {
        if (SharedPreferenceValue.getLastScore(activity) != null) {
            Scores score = SharedPreferenceValue.getLastScore(activity);
            Intent mSharingIntent = new Intent(Intent.ACTION_SEND);
            String mShareBody = new StringBuilder()
                    .append("My Steps Proj data\n")
                    .append("Steps: ").append(score.getSteps()).append("\n")
                    .append("Calories: ").append(score.getCalories()).append(" kJ\n")
                    .append("Distance: ").append(score.getDistance()).append(" km\n")
                    .append("Current Speed: ").append(score.getSpeed()).append(" km/h\n")
                    .append("Pace: ").append(score.getPace()).append(" steps/min\n")
                    .toString();
            mSharingIntent.setType("text/plain");
            mSharingIntent.putExtra(
                    android.content.Intent.EXTRA_SUBJECT, tvFullName.getText().toString() + " Steps Records");
            mSharingIntent.putExtra(
                    android.content.Intent.EXTRA_TEXT, mShareBody);
            startActivity(Intent.createChooser(mSharingIntent, "Share via"));
        } else {
            Toast.makeText(activity, "No score found to share", Toast.LENGTH_SHORT).show();
        }
    }

    /// logout user method
    private void logOutUser() {
        SharedPreferenceValue.clearLoggedInuserData(activity);
        // SharedPreferenceValue.clearLastScore(activity);
        if (SharedPreferenceValue.getLoggedinUser(activity) == -1) {
            Toast.makeText(activity, "Logout Successful", Toast.LENGTH_SHORT).show();
            startLoginActivity();
        }
    }

    ///start login screen
    private void startLoginActivity() {
        startActivity(new Intent(activity, LoginActivity.class));
        finish();
    }

    // show menu
    void showMenu() {
        lnMenu.setVisibility(View.VISIBLE);
        lnMenu.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.left_in));
    }

    void hideMenu() {
        lnMenu.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.left_out));
        lnMenu.setVisibility(View.GONE);
    }
    /// login user information
    @Override
    public void getUser(User user) {
        tvFullName.setText(user.getFirstName() + " " + user.getLastName());
        tvMenuFullName.setText(user.getFirstName() + " " + user.getLastName());
    }

    /// this method is for calling error message when user is not found in firebase
    @Override
    public void noUserFound() {

    }

    /// this method will be called for updating current day  of score to firebase database
    @Override
    public void userCurrentDayDataUpdated() {
        Toast.makeText(activity, "Current Day score Updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void noRelationListFound() {
        Toast.makeText(activity, "No top user available", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getRelationList(List<UserToUserRelation> relationList) {
        dashboardManager.relationListWiseAllUsers(relationList);
    }

    List<TopUserScoreModel> topUsers;
    @Override
    public void getRelationUserInfo(User user) {
        dashboardManager.getScoreByID(user);
    }

    @Override
    public void scoreFound(boolean scoreFound, Scores score, User user) {
        if (scoreFound){
            topUsers.add(new TopUserScoreModel(user,score));
        }
    }

    int count=0;
    @Override
    public void topUsersAvailable() {
        if (topUsers.size()>0 && count==0){
            adapter=new TopUsersListAdapter(activity,topUsers);
            rvTopUserList.setAdapter(adapter);
            count++;
        }else if (topUsers.size()>0 && count!=0){
            adapter.updateData(topUsers);
        }

    }
}
