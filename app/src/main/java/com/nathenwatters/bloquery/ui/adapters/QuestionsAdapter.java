package com.nathenwatters.bloquery.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nathenwatters.bloquery.R;
import com.nathenwatters.bloquery.api.model.parseobjects.BloQueryUser;
import com.nathenwatters.bloquery.api.model.parseobjects.Question;
import com.nathenwatters.bloquery.ui.activities.ProfileActivity;
import com.nathenwatters.bloquery.ui.activities.SingleQuestionActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private List<Question> mQuestions;
    private Context mContext;

    public QuestionsAdapter(Context context, List<Question> questions) {
        mQuestions = questions;
        mContext = context;
    }

    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.questions_list_single_item, parent, false);
        return new QuestionsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final QuestionsAdapter.ViewHolder holder, int position) {
        Question question = (Question) mQuestions.get(position);
        holder.mQuestionText.setText(question.getQuestionText());
        holder.mQuestionUsername.setText(question.getUserWhoAsked());

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", question.getUserWhoAsked());
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    BloQueryUser user = (BloQueryUser)object;
                    ParseFile file = user.getPhotoFile();
                    if (file != null) {
                        holder.mParseImageView.setParseFile(file);
                        holder.mParseImageView.loadInBackground();
                    }
                } else {
                    Toast.makeText(holder.itemView.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mQuestions != null ? mQuestions.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView mQuestionText;
        protected TextView mQuestionUsername;
        protected LinearLayout mLinearLayout;
        protected ParseImageView mParseImageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            mQuestionText = (TextView) itemView.findViewById(R.id.tv_question_text);
            mQuestionUsername = (TextView) itemView.findViewById(R.id.tv_question_username);
            itemView.setOnClickListener(this);

            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.ll_question_user);
            mParseImageView = (ParseImageView) mLinearLayout.findViewById(R.id.iv_image_preview);
            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), ProfileActivity.class);
                    Question q = mQuestions.get(getAdapterPosition());
                    intent.putExtra(ProfileActivity.USER, q.getUserWhoAsked());
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(itemView.getContext(), SingleQuestionActivity.class);
            Question question = mQuestions.get(getAdapterPosition());
            intent.putExtra("question", question.getObjectId());
            itemView.getContext().startActivity(intent);
        }
    }
}