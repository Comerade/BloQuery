package com.nathenwatters.bloquery.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nathenwatters.bloquery.R;
import com.nathenwatters.bloquery.api.model.parseobjects.Question;
import com.nathenwatters.bloquery.ui.activities.SingleQuestionActivity;

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
    public void onBindViewHolder(QuestionsAdapter.ViewHolder holder, int position) {
        Question question = (Question) mQuestions.get(position);
        holder.mQuestionText.setText(question.getQuestionText());
        holder.mQuestionUsername.setText(question.getUserWhoAsked());
    }

    @Override
    public int getItemCount() {
        return mQuestions != null ? mQuestions.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView mQuestionText;
        protected TextView mQuestionUsername;

        public ViewHolder(View itemView) {
            super(itemView);
            mQuestionText = (TextView) itemView.findViewById(R.id.tv_question_text);
            mQuestionUsername = (TextView) itemView.findViewById(R.id.tv_question_username);
            itemView.setOnClickListener(this);
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