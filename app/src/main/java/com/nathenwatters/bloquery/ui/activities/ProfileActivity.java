package com.nathenwatters.bloquery.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nathenwatters.bloquery.R;
import com.nathenwatters.bloquery.api.model.parseobjects.BloQueryUser;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends BaseActivity {

    private TextView mTvProfilePicFilename;
    private EditText mEtProfileDesc;

    private TextView mTvUserProfileDescription;

    private ParseImageView mImageView;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    public static final String USER = "user";
    private boolean isCurrentUser = false;

    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String username = null;

        if (intent != null) {
            username = intent.getStringExtra(USER);
        }

        if (username != null && !username.isEmpty()) {
            setContentView(R.layout.user_profile);
            isCurrentUser = false;
            mUsername = username;
            setTitle(username + "'s Profile");
            mTvUserProfileDescription = (TextView) findViewById(R.id.tv_profile_description);

        } else {
            setContentView(R.layout.activity_my_profile);
            isCurrentUser = true;
            setTitle(ParseUser.getCurrentUser().getUsername() + "'s Profile");
            mTvProfilePicFilename = (TextView) findViewById(R.id.tv_profile_pic_filename);
            mEtProfileDesc = (EditText) findViewById(R.id.et_profile_description);
        }

        mImageView = (ParseImageView) findViewById(R.id.iv_profile_pic);

    }

    /**
     * User is attempting to change their profile photo.
     * @param view
     */
    public void loadPhoto(View view) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    /**
     * User wants to take a photo to use
     * @param view
     */
    public void takePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isCurrentUser) {
            ParseFile parseFile = LoginActivity.getBloQueryUser().getPhotoFile();

            if (parseFile != null) {
                mImageView.setParseFile(parseFile);
                mImageView.loadInBackground();
            }

            String description = LoginActivity.getBloQueryUser().getDescription();
            if (description != null && !description.isEmpty()){
                mEtProfileDesc.setText(description);
            }
        } else {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", mUsername);
            query.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if (e == null) {

                        BloQueryUser user = (BloQueryUser) object;
                        ParseFile file = user.getPhotoFile();
                        if (file != null) {
                            mImageView.setParseFile(file);
                            mImageView.loadInBackground();
                        }

                        String description = user.getDescription();
                        if (description != null && !description.isEmpty()) {
                            mTvUserProfileDescription.setText(user.getDescription());
                        } else {
                            mTvUserProfileDescription.setText(R.string.no_user_description);
                        }

                    } else {
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        if (resultCode == RESULT_OK && data != null) {

            if (requestCode == RESULT_LOAD_IMAGE) {
                Uri selectedImage = data.getData();
                String[] filepathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filepathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filepathColumn[0]);
                    String filepath = cursor.getString(columnIndex);
                    cursor.close();
                    mBitmap = BitmapFactory.decodeFile(filepath);
                    mImageView.setImageBitmap(mBitmap);
                    String filename = "..." + filepath.substring(filepath.lastIndexOf("/") + 1);
                    mTvProfilePicFilename.setText(filename);
                }
            }

            else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                mBitmap = (Bitmap) extras.get("data");
                mImageView.setImageBitmap(mBitmap);

            }
        }
    }

    private Bitmap mBitmap;

    /**
     * User clicked the save button. Update the ParseUser profile image
     * description for this user.
     * @param view
     */
    public void updateProfile(View view) {
        final BloQueryUser user = LoginActivity.getBloQueryUser();
        if (mBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200, false);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            final ParseFile file = new ParseFile("img.png", stream.toByteArray());

            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        user.setPhotoFile(file);

                    }
                }
            });
        }

        user.setDescription(mEtProfileDesc.getText().toString());
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                } else {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


        this.finish();
    }

    /**
     * User choose to cancel updating their profile, end this Activity
     * and return to the calling Activity.
     * @param view
     */
    public void cancelProfileEditor(View view) {
        this.finish();
    }
}
