package com.nathenwatters.bloquery;

import android.app.Application;

import android.text.TextUtils;

import com.nathenwatters.bloquery.api.model.parseobjects.Answer;
import com.nathenwatters.bloquery.api.model.parseobjects.BloQueryUser;
import com.nathenwatters.bloquery.api.model.parseobjects.Question;
import com.parse.Parse;
import com.parse.ParseObject;

public class BloQueryApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initializing Parse
        // ...
        Parse.enableLocalDatastore(this);

        // Registering ParseObject subclasses
        ParseObject.registerSubclass(BloQueryUser.class);
        ParseObject.registerSubclass(Question.class);
        ParseObject.registerSubclass(Answer.class);

        Parse.initialize(this, getString(R.string.PARSE_APP_KEY), getString(R.string.PARSE_CLIENT_KEY));
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}

