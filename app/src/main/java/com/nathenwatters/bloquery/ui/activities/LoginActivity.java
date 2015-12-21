package com.nathenwatters.bloquery.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nathenwatters.bloquery.BloQueryApplication;
import com.nathenwatters.bloquery.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

/**
 * Activity for controlling the login and signup behavior.
 * This is the launcher activity for BloQuery and all users
 * must be logged in and registered before they can proceed
 * with the app's functionality.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // Setting up references to this activity's views
    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private Button mBtnSignup;
    private TextView mTvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize views
        mEtUsername = (EditText) findViewById(R.id.et_login_username);
        mEtPassword = (EditText) findViewById(R.id.et_login_password);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnSignup = (Button) findViewById(R.id.btn_signup);
        mTvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);

        // attach click listeners
        mBtnLogin.setOnClickListener(this);
        mBtnSignup.setOnClickListener(this);

        setUpForgotPassword();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        // when login button is clicked, attempt user login
        if (id == mBtnLogin.getId()) {
            if (areLoginFieldsFilledOut()) {
                login();
            }
        }

        // when signup button is clicked, launch SignUpActivity
        else if (id == mBtnSignup.getId()) {
            signup();
        }
    }

    /**
     * Attempt to login with the Parse SDK
     */
    public void login() {
        String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();

        // display progress to the user
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getString(R.string.wait_to_login));
        progressDialog.show();

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (null != user) {
                    boolean emailVerified = user.getBoolean("emailVerified");

                    if (!emailVerified) {
                        Toast.makeText(LoginActivity.this, R.string.error_verify_email, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        ParseUser.logOut();
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.attn_login_success, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, QuestionsActivity.class);
                        startActivity(intent);

                        progressDialog.dismiss();
                        LoginActivity.this.finish();
                    }
                }

                // error logging in to Parse
                else {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                    mTvForgotPassword.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * When user clicks signup, launch the SignUpActivity
     * to register a new account
     */
    public void signup() {
        Intent signupIntent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(signupIntent);
    }

    /**
     * Helper method for setting up the forgot password TextView
     */
    private void setUpForgotPassword() {
        final SpannableStringBuilder spanBuilder = new SpannableStringBuilder(getString(R.string.login_password_reset_help));
        final ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(Color.rgb(255, 0, 0));

        spanBuilder.setSpan(foregroundSpan,
                getString(R.string.login_password_reset_help).indexOf(getString(R.string.here)),
                getString(R.string.login_password_reset_help).length() - 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        mTvForgotPassword.setText(spanBuilder);
        mTvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    /**
     * Using the Parse SDK, attempt to reset the password provided from the
     * email address provided by the user.
     */
    private void resetPassword() {
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.reset_password, null);
        final EditText email = (EditText) layout.findViewById(R.id.et_email_reset);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this)
                .setView(layout)
                .setTitle(getString(R.string.reset_password))
                .setPositiveButton(getString(R.string.confirm_password_reset), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!BloQueryApplication.isValidEmail(email.getText().toString())) {
                            Toast.makeText(LoginActivity.this, R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
                            resetPassword();
                        }

                        // user entered a valid email address, send the password reset request to Parse
                        else {
                            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                            progressDialog.setMessage(getString(R.string.attn_password_reset_instructions) + email.getText().toString());
                            progressDialog.show();

                            ParseUser.requestPasswordResetInBackground(email.getText().toString(), new RequestPasswordResetCallback() {
                                @Override
                                public void done(ParseException e) {
                                    // successfully sent reset instructions
                                    if (null == e) {
                                        Toast.makeText(LoginActivity.this, R.string.attn_instructions_sent, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }

                                    // error sending reset instructions to provided email address
                                    else {
                                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel_password_reset), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.show();
    }

    /**
     * Helper method for determining if the user filled out
     * the username and password fields.
     */
    private boolean areLoginFieldsFilledOut() {

        if (null == mEtUsername.getText() || null == mEtPassword.getText()
                || mEtUsername.getText().toString().isEmpty() || mEtPassword.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
