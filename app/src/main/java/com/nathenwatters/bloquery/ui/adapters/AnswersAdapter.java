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
import com.nathenwatters.bloquery.api.model.parseobjects.Answer;
import com.nathenwatters.bloquery.api.model.parseobjects.BloQueryUser;
import com.nathenwatters.bloquery.ui.activities.ProfileActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    public void onBindViewHolder(final AnswersAdapter.ViewHolder holder, int position) {
        Answer answer = (Answer) mAnswers.get(position);
        holder.mAnswerText.setText(answer.getAnswerText());
        holder.mAnswerUsername.setText(answer.getAnswerUser());

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", answer.getAnswerUser());
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
        return mAnswers != null ? mAnswers.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView mAnswerText;
        protected TextView mAnswerUsername;
        protected LinearLayout mLinearLayout;
        protected ParseImageView mParseImageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            mAnswerText = (TextView) itemView.findViewById(R.id.tv_answer_text);
            mAnswerUsername = (TextView) itemView.findViewById(R.id.tv_answer_username);

            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.ll_answer_user);
            mParseImageView = (ParseImageView) mLinearLayout.findViewById(R.id.iv_image_preview);

            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), ProfileActivity.class);
                    Answer a = mAnswers.get(getAdapterPosition());
                    intent.putExtra(ProfileActivity.USER, a.getAnswerUser());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
