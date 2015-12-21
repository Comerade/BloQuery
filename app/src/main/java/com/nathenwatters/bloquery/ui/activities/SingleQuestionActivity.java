package com.nathenwatters.bloquery.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.nathenwatters.bloquery.R;
import com.nathenwatters.bloquery.api.model.parseobjects.Question;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class SingleQuestionActivity extends AppCompatActivity {

    private TextView mQuestionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_question);

        mQuestionText = (TextView) findViewById(R.id.tv_single_question_text);

        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            String questionObjectId = extras.getString("question");
            final ProgressDialog progressDialog = new ProgressDialog(SingleQuestionActivity.this);
            progressDialog.setMessage(getString(R.string.wait_for_answers));
            progressDialog.show();

            ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
            query.getInBackground(questionObjectId, new GetCallback<Question>() {
                @Override
                public void done(Question object, ParseException e) {
                    if (null == e) {
                        progressDialog.dismiss();
                        mQuestionText.setText(object.getQuestionText());
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SingleQuestionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}
