package com.example.android_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

public class LogActivity extends AppCompatActivity implements View.OnClickListener, LogManager.ScoreLogFeedbackListener {
    private ImageButton ibtnBack;
    private RecyclerView rvLogList;
    private LogActivity activity;
    RecyclerView.Adapter adapter;
    private LogManager logManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        initUI();

    }
    private void initUI() {
        activity = this;
        logManager = new LogManager(activity, true, this);
        ibtnBack = findViewById(R.id.ibtnBack);
        rvLogList = findViewById(R.id.rvLogList);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        rvLogList.setLayoutManager(manager);
        rvLogList.setHasFixedSize(true);
        ibtnBack.setOnClickListener(this);
        logManager.getAllScoreLogByUserID(SharedPreferenceValue.getLoggedinUser(activity));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtnBack:
                startDashboard();
                break;

        }
    }

    private void startDashboard() {
        startActivity(new Intent(activity, DashboardActivity.class));
        finish();
    }

    @Override
    public void logOfScores(List<Scores> scoresList) {
        List<Scores> currentMonthScoresList = logManager.sortScoreByCurrentMonth(scoresList);
        if (currentMonthScoresList.size() > 0) {
            adapter = new LogListAdapter(activity, currentMonthScoresList);
            rvLogList.setAdapter(adapter);
        }
    }

    @Override
    public void noScoreLogFound() {
        Toast.makeText(activity, "No score log found", Toast.LENGTH_SHORT).show();
    }
}

