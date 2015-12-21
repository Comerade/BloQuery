package com.nathenwatters.bloquery.api.model.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.io.Serializable;

@ParseClassName("Question")
public class Question extends ParseObject implements Serializable {

    public static final String QUESTION_TEXT = "question_text";
    public static final String QUESTION_USER = "username";

    public void setQuestionText(String questionText) {
        put(QUESTION_TEXT, questionText);
    }

    public String getQuestionText() {
        return getString(QUESTION_TEXT);
    }

    public void setUserWhoAsked(String userWhoAsked) {
        put(QUESTION_USER, userWhoAsked);
    }

    public String getUserWhoAsked() {
        return getString(QUESTION_USER);
    }
}