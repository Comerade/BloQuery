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
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class MyProfileActivity extends BaseActivity {

    private TextView mTvProfilePicFilename;
    private EditText mEtProfileDesc;

    private ParseImageView mImageView;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private static String filepath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        setTitle(ParseUser.getCurrentUser().getUsername() + getString(R.string.profile_view_title));
        mImageView = (ParseImageView) findViewById(R.id.iv_profile_pic);
        mTvProfilePicFilename = (TextView) findViewById(R.id.tv_profile_pic_filename);
        mEtProfileDesc = (EditText) findViewById(R.id.et_profile_description);
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

        ParseFile parseFile = LoginActivity.getBloQueryUser().getPhotoFile();

        if (parseFile != null) {
            mImageView.setParseFile(parseFile);
            mImageView.loadInBackground();
        }

        String description = LoginActivity.getBloQueryUser().getDescription();
        if (description != null && !description.isEmpty()){
            mEtProfileDesc.setText(description);
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
                    filepath = cursor.getString(columnIndex);
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
                    Toast.makeText(MyProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
