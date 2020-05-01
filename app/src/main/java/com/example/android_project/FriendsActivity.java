package com.example.android_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity implements View.OnClickListener, FriendListAdapter.FriendButtonClickedListener, FriendListManager.FriendListFeedbackListener {

    int form = -1;
    private ImageButton ibtnBack;
    private ImageButton ibtnSearch;
    private RecyclerView rvFriendList;
    private FriendsActivity activity;
    private FriendListManager friendListManager;
    FriendListAdapter adapter;
    private TextView tvActTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        receiveIntent();
        initUI();
    }

    ///receiving comming for which operation to see list from=1 and to search online friends from=2
    private void receiveIntent() {
        if (getIntent() != null) {
            form = getIntent().getIntExtra(StaticAccess.KEY_FRIENDS_LIST_ACTIVITY, -1);
        }
    }

    /// initialization of UI Elements
    private void initUI() {
        activity = this;
        friendListManager = new FriendListManager(activity, true, this);
        userList = new ArrayList<>();
        relationList = new ArrayList<>();
        ibtnBack = findViewById(R.id.ibtnBack);
        ibtnSearch = findViewById(R.id.ibtnSearch);
        rvFriendList = findViewById(R.id.rvFriendList);
        tvActTitle = findViewById(R.id.tvActTitle);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        rvFriendList.setLayoutManager(manager);
        rvFriendList.setHasFixedSize(true);
        ibtnBack.setOnClickListener(this);
        ibtnSearch.setOnClickListener(this);
        if (form == 1) {
            friendListManager.getUserRelationByUserID(SharedPreferenceValue.getLoggedinUser(activity));
            ibtnSearch.setVisibility(View.GONE);
            tvActTitle.setText("FRIENDS");
        } else {
            friendListManager.getUserRelationByUserID(SharedPreferenceValue.getLoggedinUser(activity));
            ibtnSearch.setVisibility(View.VISIBLE);
            tvActTitle.setText("SEARCH FRIENDS");

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtnBack:
                startDashboard();
                break;
            case R.id.ibtnSearch:
                searchFriend();
                break;
        }
    }

    //// Search friends by their names
    private void searchFriend() {
        final EditText searchEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Search Friends")
                .setMessage("Enter Friends name or email or address")
                .setView(searchEditText)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String keyword = String.valueOf(searchEditText.getText());
                        friendListManager.searchFriend(SharedPreferenceValue.getLoggedinUser(activity), keyword,relationList);
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    //go to dashboard method
    private void startDashboard() {
        startActivity(new Intent(activity, DashboardActivity.class));
        finish();
    }

    /// called when the friend list found
    @Override
    public void friendListFound(List<User> userList) {
        adapter = new FriendListAdapter(activity, userList, this, form);
        rvFriendList.setAdapter(adapter);
    }

    @Override
    public void noFriendFound() {
        Toast.makeText(activity, "No Friends Found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addFriendSuccess() {
        Toast.makeText(activity, "Friend added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addFriendFailed() {
        Toast.makeText(activity, "Friend added Failed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAddFriendButtonClicked(User user) {
        friendListManager.addFriend(SharedPreferenceValue.getLoggedinUser(activity), user);
    }

    @Override
    public void alreadyFriend() {
        Toast.makeText(activity, "Friend already added", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void noRelationListFound() {
        Toast.makeText(activity, "No Friends Found", Toast.LENGTH_SHORT).show();
    }
    List<UserToUserRelation> relationList;

    @Override
    public void getRelationList(List<UserToUserRelation> relationList) {
        if (form == 1) {
            friendListManager.relationListWiseAllUsers(relationList);
        }else {
            this.relationList=relationList;
        }
    }

    int count = 0;
    List<User> userList;

    @Override
    public void getRelationUserInfo(User user) {
        userList.add(user);
        if (userList.size() > 0 && count == 0) {
            adapter = new FriendListAdapter(activity, userList, this, form);
            rvFriendList.setAdapter(adapter);
            count++;
        } else if (userList.size() > 0 && count != 0) {
            adapter.updateData(userList);
        }

    }

}
