package com.nathenwatters.bloquery.api.model.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.io.Serializable;

@ParseClassName("Answer")
public class Answer extends ParseObject implements Serializable {

    public static final String ANSWER_TEXT = "answer";
    public static final String ANSWER_USER = "username";
    public static final String QUESTION_ANSWERED = "question";

    public void setAnswerText(String answerText) {
        put(ANSWER_TEXT, answerText);
    }

    public String getAnswerText() {
        return getString(ANSWER_TEXT);
    }

    public void setAnswerUser(String answerUser) {
        put(ANSWER_USER, answerUser);
    }

    public String getAnswerUser() {
        return getString(ANSWER_USER);
    }

    public void setQuestion(Question question) {
        put(QUESTION_ANSWERED, question);
    }

    public Question getQuestion() {
        return (Question)get(QUESTION_ANSWERED);
    }
}