package com.nathenwatters.bloquery.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.nathenwatters.bloquery.R;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_logout: {
                final ProgressDialog progressDialog = new ProgressDialog(BaseActivity.this);
                progressDialog.setMessage(getString(R.string.logging_out));
                progressDialog.show();

                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            progressDialog.dismiss();

                            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                            startActivity(intent);
                            BaseActivity.this.finish();
                        } else {
                            progressDialog.dismiss();

                            Toast.makeText(BaseActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            BaseActivity.this.finish();
                        }
                    }
                });
                break;
            }
            case R.id.menu_item_myprofile: {
                Intent intent = new Intent(BaseActivity.this, MyProfileActivity.class);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
