package com.nathenwatters.bloquery.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nathenwatters.bloquery.R;
import com.nathenwatters.bloquery.api.model.parseobjects.Answer;

import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder> {

    private Context mContext;
    private List<Answer> mAnswers;

    public AnswersAdapter(Context context, List<Answer> answers) {
        mContext = context;
        mAnswers = answers;
    }

    @Override
    public AnswersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answers_list_single_item, parent, false);
        return new AnswersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AnswersAdapter.ViewHolder holder, int position) {
        Answer answer = (Answer) mAnswers.get(position);
        holder.mAnswerText.setText(answer.getAnswerText());
        holder.mAnswerUsername.setText(answer.getAnswerUser());
    }

    @Override
    public int getItemCount() {
        return mAnswers != null ? mAnswers.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView mAnswerText;
        protected TextView mAnswerUsername;

        public ViewHolder(View itemView) {
            super(itemView);
            mAnswerText = (TextView) itemView.findViewById(R.id.tv_answer_text);
            mAnswerUsername = (TextView) itemView.findViewById(R.id.tv_answer_username);
        }
    }
}
