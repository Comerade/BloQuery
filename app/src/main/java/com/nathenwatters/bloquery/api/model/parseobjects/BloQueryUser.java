package com.nathenwatters.bloquery.api.model.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseUser;

/**
 * Custom ParseUser object extended to support a profile
 * image URL as well as a profile description.
 */
@ParseClassName("_BloQueryUser")
public class BloQueryUser extends ParseUser {

    public static final String USER_IMAGE_URL = "img_url";
    public static final String USER_DESCRIPTION = "description";

    /**
     * Default constructor
     */
    public BloQueryUser() {}

    /**
     * Setter for URL of the user's profile image
     * @param url
     */
    public void setImageURL(String url) {
        put(USER_IMAGE_URL, url);
    }

    /**
     * Getter for the URL of the user's profile image
     * @return
     */
    public String getImageURL() {
        return getString(USER_IMAGE_URL);
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