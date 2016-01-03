package com.nathenwatters.bloquery.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.nathenwatters.bloquery.R;
import com.nathenwatters.bloquery.api.model.parseobjects.Answer;
import com.nathenwatters.bloquery.api.model.parseobjects.Question;
import com.nathenwatters.bloquery.ui.adapters.AnswersAdapter;
import com.nathenwatters.bloquery.ui.fragments.AddAnswerDialogFragment;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class SingleQuestionActivity extends BaseActivity {

    private TextView mQuestionText;
    private RecyclerView mRvAnswers;

    private AnswersAdapter mAnswersAdapter;

    private List<Answer> mAnswers = new ArrayList<>();

    private static String QUESTION_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_question);

        mQuestionText = (TextView) findViewById(R.id.tv_single_question_text);
        mRvAnswers = (RecyclerView) findViewById(R.id.rv_list_of_answers);
        mRvAnswers.setLayoutManager(new LinearLayoutManager(this));

        mAnswersAdapter = new AnswersAdapter(SingleQuestionActivity.this, mAnswers);
        mRvAnswers.setAdapter(mAnswersAdapter);

        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            String questionObjectId = extras.getString("question");
            QUESTION_ID = questionObjectId;
            final ProgressDialog progressDialog = new ProgressDialog(SingleQuestionActivity.this);
            progressDialog.setMessage(getString(R.string.wait_for_answers));
            progressDialog.show();

            ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
            query.getInBackground(questionObjectId, new GetCallback<Question>() {
                @Override
                public void done(Question object, ParseException e) {
                    if (null == e) {
                        progressDialog.dismiss();

                        SingleQuestionActivity.this.setTitle(object.getUserWhoAsked() + getString(R.string.user_asks));

                        mQuestionText.setText(object.getQuestionText());

                        ParseQuery<Answer> answersQuery = ParseQuery.getQuery(Answer.class);
                        answersQuery.whereEqualTo(Answer.QUESTION_ANSWERED, object);
                        answersQuery.findInBackground(new FindCallback<Answer>() {
                            @Override
                            public void done(List<Answer> answers, ParseException e) {
                                if (e == null) {
                                    mAnswers.clear();
                                    for (Answer answer : answers) {
                                        mAnswers.add(answer);
                                    }
                                    mAnswersAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(SingleQuestionActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SingleQuestionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void updateAnswersAdapter(Answer answer) {
        mAnswers.add(answer);
        mAnswersAdapter.notifyDataSetChanged();
    }

    // menu items logic--------------------------------------------
    private static final int NEW_MENU_ID = Menu.FIRST;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(Menu.NONE, NEW_MENU_ID, Menu.NONE, R.string.add_answer);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case NEW_MENU_ID: {
                DialogFragment dialog = new AddAnswerDialogFragment();
                Bundle args = new Bundle();
                args.putString("question", QUESTION_ID);
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), getString(R.string.add_answer));
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
