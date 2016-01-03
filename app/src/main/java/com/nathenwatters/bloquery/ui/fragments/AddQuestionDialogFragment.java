package com.nathenwatters.bloquery.ui.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.nathenwatters.bloquery.R;
import com.nathenwatters.bloquery.api.model.parseobjects.Question;
import com.nathenwatters.bloquery.ui.activities.QuestionsActivity;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AddQuestionDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final QuestionsActivity activity = (QuestionsActivity) getActivity();

        builder.setView(inflater.inflate(R.layout.fragment_add_question, null))
               .setPositiveButton(getString(R.string.submit_question), new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       EditText etQuestion = (EditText) (getDialog()).findViewById(R.id.et_add_question_text);
                       String questionText = etQuestion.getText().toString();

                       final Question question = new Question();
                       question.setUserWhoAsked(ParseUser.getCurrentUser().getUsername());
                       question.setQuestionText(questionText);

                       final ProgressDialog progressDialog = new ProgressDialog(AddQuestionDialogFragment.this.getContext());
                       progressDialog.setMessage(getString(R.string.creating_question));
                       progressDialog.show();

                       question.saveInBackground(new SaveCallback() {
                           @Override
                           public void done(ParseException e) {
                               progressDialog.dismiss();
                               if (e == null) {
                                   activity.addQuestionToAdapter(question);
                               } else {
                                   Toast.makeText(AddQuestionDialogFragment.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                               }
                           }
                       });
                   }
               })
               .setNegativeButton(getString(R.string.cancel_dialog_fragment), new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       AddQuestionDialogFragment.this.getDialog().cancel();
                   }
               });
        
        return builder.create();
    }

}