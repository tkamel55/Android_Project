package com.example.android_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupManager {
    FirebaseDatabase db;
    DatabaseReference usersTbl;
    ProgressDialog progressDialog;
    Context mContext;
    private boolean visibleProgress = false;
    private SignUpFeedbackListener signUpFeedbackListener;

    public SignupManager(Context mContext, boolean visibleProgress, SignUpFeedbackListener signUpFeedbackListener) {
        this.mContext = mContext;
        this.visibleProgress = visibleProgress;
        this.signUpFeedbackListener = signUpFeedbackListener;
        db = FirebaseDatabase.getInstance();
        usersTbl = db.getReference(StaticAccess.USERS_TABLE);
        progressDialog = new ProgressDialog(mContext);
    }
    //@signup a user
    public void signUpUser(final String firstName, String lastName, String email, String password, String cfPassword, String address) {

        boolean isValid = validateAllField(firstName, lastName, email, password, cfPassword, address);
        if (isValid) {
            User aUser = new User();
            aUser.setUserID(AESCrypt.getID());
            aUser.setFirstName(firstName);
            aUser.setLastName(lastName);
            aUser.setEmail(email);
            try {
                aUser.setPassword(AESCrypt.encrypt(cfPassword));
                aUser.setAddress(address);
                aUser.setActive(true);
                aUser.setCreatedAt(StaticAccess.getDateTimeNow());
                aUser.setUpdatedAt(StaticAccess.getDateTimeNow());
                if (visibleProgress) {
                    showProgress();
                }
                usersTbl.push().setValue(aUser, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            System.out.println("Data could not be saved " + databaseError.getMessage());
                            hideProgress();
                            signUpFeedbackListener.signUpFailed();

                        } else {
                            System.out.println("Data saved successfully.");
                            hideProgress();
                            signUpFeedbackListener.signUpSuccess();
                        }

                    }
                });
            } catch (Exception e) {
                signUpFeedbackListener.signUpFailed();
            }

        } else {
            signUpFeedbackListener.validationError();
        }
    }
    //validate signup user information
    private boolean validateAllField(String firstName, String lastName, String email, String password, String cfPassword, String address) {
        boolean isFirstNameOK = false;
        boolean isLastNameOK = false;
        boolean isEmailOK = false;
        boolean isPasswordOK = false;
        boolean isAddressOK = false;
        if (!TextUtils.isEmpty(firstName)) {
            isFirstNameOK = true;
        } else {
            isFirstNameOK = false;
            signUpFeedbackListener.firstNameError();
        }
        if (!TextUtils.isEmpty(lastName)) {
            isLastNameOK = true;
        } else {
            isLastNameOK = false;
            signUpFeedbackListener.lastNameError();

        }
        if (emailValidator(email)) {
            isEmailOK = true;
        } else {
            isEmailOK = false;
            signUpFeedbackListener.emailError();

        }
        if (passwordValidator(password, cfPassword)) {
            isPasswordOK = true;
        } else {
            isPasswordOK = false;
            signUpFeedbackListener.passwordError();

        }
        if (!TextUtils.isEmpty(address)) {
            isAddressOK = true;
        } else {
            isAddressOK = false;
            signUpFeedbackListener.addressError();
        }
        if (isFirstNameOK && isLastNameOK && isEmailOK && isPasswordOK && isAddressOK) {
            return true;
        } else {
            return false;
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

    public boolean emailValidator(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            Pattern pattern;
            Matcher matcher;
            final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(email);
            return matcher.matches();
        }
    }

    public boolean passwordValidator(String password, String cfPassword) {
        boolean isEmptyCheckOK = false;
        boolean isLengthOK = false;
        boolean isBothSame = false;
        if (!TextUtils.isEmpty(password)) {
            isEmptyCheckOK = true;
            if (password.length() > 5 && cfPassword.length() > 5) {
                isLengthOK = true;
                if (password.equalsIgnoreCase(cfPassword)) {
                    isBothSame = true;
                } else {
                    isBothSame = false;
                }
            } else {
                isLengthOK = false;
            }
        } else {
            isEmptyCheckOK = false;
        }

        if (isEmptyCheckOK && isLengthOK && isBothSame) {
            return true;
        } else {
            return false;
        }
    }

    public interface SignUpFeedbackListener {
        void signUpSuccess();

        void signUpFailed();

        void firstNameError();

        void lastNameError();

        void emailError();

        void passwordError();

        void addressError();

        void validationError();
    }


}
