package com.nathenwatters.bloquery.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nathenwatters.bloquery.BloQueryApplication;
import com.nathenwatters.bloquery.R;
import com.nathenwatters.bloquery.api.model.parseobjects.BloQueryUser;
import com.parse.ParseException;
import com.parse.SignUpCallback;

/**
 * Activity that gives our users the ability to register
 * for a new account on BloQuery
 */
public class SignUpActivity extends AppCompatActivity {

    // Setting up references to this activity's views
    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mEtPasswordConfirm;
    private EditText mEtEmail;
    private Button mBtnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // initialize views
        mEtUsername = (EditText) findViewById(R.id.et_signup_username);
        mEtPassword = (EditText) findViewById(R.id.et_signup_password);
        mEtPasswordConfirm = (EditText) findViewById(R.id.et_signup_password_confirm);
        mEtEmail = (EditText) findViewById(R.id.et_signup_email);
        mBtnCreateAccount = (Button) findViewById(R.id.btn_signup);

        mBtnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBloQueryAccount();
            }
        });
    }

    private void createBloQueryAccount() {
        if (areAllFieldsComplete()) {
            String username = mEtUsername.getText().toString();
            String password = mEtPassword.getText().toString();
            String passwordConfirm = mEtPasswordConfirm.getText().toString();
            String email = mEtEmail.getText().toString();

            if (arePasswordsEqual(password, passwordConfirm)) {
                final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                progressDialog.setMessage(getString(R.string.wait_account_creation));
                progressDialog.show();

                BloQueryUser user = new BloQueryUser();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (null == e) {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Successfully created account", Toast.LENGTH_SHORT).show();
                            SignUpActivity.this.finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    /**
     * Helper method for checking if the password and confirm password
     * fields match.
     * @param password
     * @param confirmPassword
     * @return
     */
    private boolean arePasswordsEqual(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, R.string.error_passwords_dont_match, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Helper method for checking that all input fields have been filled out.
     * @return
     */
    private boolean areAllFieldsComplete() {

        // first check if there are any empty or non-existant fields
        if (null == mEtUsername.getText() || mEtUsername.getText().toString().isEmpty() ||
                null == mEtPassword.getText() || mEtPassword.getText().toString().isEmpty() ||
                null == mEtPasswordConfirm || mEtPasswordConfirm.getText().toString().isEmpty() ||
                null == mEtEmail || mEtEmail.getText().toString().isEmpty()) {
            Toast.makeText(SignUpActivity.this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return false;
        }

        // then check that a valid email address had been entered
        if (!BloQueryApplication.isValidEmail(mEtEmail.getText().toString())) {
            Toast.makeText(SignUpActivity.this, R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
