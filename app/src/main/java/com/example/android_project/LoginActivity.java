package com.example.android_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginManager.LoginFeedbackListener {

    private EditText etCreEmail;
    private EditText etCrePass;
    private Button btnLogin;
    private LoginManager loginManager;
    private LoginActivity activity;

    private TextView GosignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GosignUp = findViewById(R.id.goSignup);

        GosignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // starting background task to update product
                Intent intent=new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(intent);
            }
        });


        initUI();
    }

    private void initUI() {
        activity = this;
        etCreEmail = findViewById(R.id.etCreEmail);
        etCrePass = findViewById(R.id.etCrePass);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        loginManager = new LoginManager(activity, true, this);
        checkAlreadyLoginOrNot();
    }

    private void checkAlreadyLoginOrNot() {
        if (SharedPreferenceValue.getLoggedinUser(activity)!=-1){
            startDashboard();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnLogin:
                performLogin();
                break;
        }
    }

    private void performLogin() {
        loginManager.loginUser(etCreEmail.getText().toString(),
                etCrePass.getText().toString());
    }

    private void startLoginActivity() {
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        finish();
    }

    @Override
    public void getLoggedinUser(User user) {
        if (user != null) {
            SharedPreferenceValue.clearLoggedInuserData(activity);
            SharedPreferenceValue.setLoggedInUser(activity, user.getUserID());
            startDashboard();
        }
    }

    private void startDashboard() {
        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
        finish();
    }

    @Override
    public void noUserFound() {
        Toast.makeText(activity, "Wrong Credential.Login failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void emailError() {
        etCreEmail.setError(getString(R.string.email_required_text));
    }

    @Override
    public void passwordError() {
        etCrePass.setError("Please provide valid password that minimum 6 character");
    }
}
