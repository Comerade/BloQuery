package com.nathenwatters.bloquery.ui.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nathenwatters.bloquery.R;
import com.nathenwatters.bloquery.api.model.parseobjects.Question;
import com.nathenwatters.bloquery.ui.adapters.QuestionsAdapter;
import com.nathenwatters.bloquery.ui.fragments.AddQuestionDialogFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends BaseActivity {

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

    public void addQuestionToAdapter(Question question) {
        // since we are sorting questions by createdAt date, we might as
        // well add each new Question to the top of the mQuestions List...
        mQuestions.add(0, question);
        mQuestionsAdapter.notifyDataSetChanged();
    }

    private static final int NEW_MENU_ID = Menu.FIRST;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(Menu.NONE, NEW_MENU_ID, Menu.NONE, R.string.new_question);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case NEW_MENU_ID: {
                DialogFragment dialog = new AddQuestionDialogFragment();
                dialog.show(getSupportFragmentManager(), getString(R.string.add_new_question));
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
