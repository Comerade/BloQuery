package com.nathenwatters.bloquery;

import android.app.Application;

import com.parse.Parse;

public class BloQueryApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Setting up Parse SDK configurations
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.PARSE_APP_KEY), getString(R.string.PARSE_CLIENT_KEY));
    }

}
