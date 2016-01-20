package com.nathenwatters.bloquery.api.model.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 * Custom ParseUser object extended to support a profile
 * image URL as well as a profile description.
 */
@ParseClassName("_User")
public class BloQueryUser extends ParseUser {

    public static final String USER_IMAGE = "img";
    public static final String USER_DESCRIPTION = "description";

    /**
     * Default constructor
     */
    public BloQueryUser() {}

    public ParseFile getPhotoFile() {
        return getParseFile(USER_IMAGE);
    }

    public void setPhotoFile(ParseFile parseFile) {
        put(USER_IMAGE, parseFile);
    }

    /**
     * Setter for the user's description
     * @param description
     */
    public void setDescription(String description) {
        put(USER_DESCRIPTION, description);
    }

    /**
     * Getter for the user's description
     * @return
     */
    public String getDescription() {
        return getString(USER_DESCRIPTION);
    }

}