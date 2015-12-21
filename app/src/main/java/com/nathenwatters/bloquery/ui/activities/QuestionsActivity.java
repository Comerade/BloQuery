package com.nathenwatters.bloquery.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.nathenwatters.bloquery.R;
import com.nathenwatters.bloquery.api.model.parseobjects.Question;
import com.nathenwatters.bloquery.ui.adapters.QuestionsAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private QuestionsAdapter mQuestionsAdapter;

    private List<Question> mQuestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blo_query);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list_of_questions);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mQuestionsAdapter = new QuestionsAdapter(QuestionsActivity.this, mQuestions);
        mRecyclerView.setAdapter(mQuestionsAdapter);

        ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> questions, ParseException e) {
                if (e == null) {
                    mQuestions.clear();
                    for (Question question : questions) {
                        mQuestions.add(question);
                    }
                    mQuestionsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(QuestionsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }
        });
    }
}
