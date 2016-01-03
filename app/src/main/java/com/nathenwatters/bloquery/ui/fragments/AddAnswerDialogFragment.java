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
import com.nathenwatters.bloquery.api.model.parseobjects.Answer;
import com.nathenwatters.bloquery.api.model.parseobjects.Question;
import com.nathenwatters.bloquery.ui.activities.SingleQuestionActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AddAnswerDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final SingleQuestionActivity activity = (SingleQuestionActivity)getActivity();

        final String questionId = getArguments().getString("question");

        builder.setView(inflater.inflate(R.layout.fragment_add_answer, null))
               .setPositiveButton(getString(R.string.btn_submit_answer), new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       String username = ParseUser.getCurrentUser().getUsername();

                       Dialog dia = AddAnswerDialogFragment.this.getDialog();
                       EditText etAnswer = (EditText) dia.findViewById(R.id.et_answer_text);

                       String answerText = etAnswer.getText().toString();

                       final Answer answer = new Answer();
                       answer.setAnswerUser(username);
                       answer.setAnswerText(answerText);
                       answer.setQuestion((Question)ParseObject.createWithoutData("Question", questionId));

                       final ProgressDialog progressDialog = new ProgressDialog(AddAnswerDialogFragment.this.getContext());
                       progressDialog.setMessage(getString(R.string.saving_answer));
                       progressDialog.show();

                       answer.saveInBackground(new SaveCallback() {
                           @Override
                           public void done(ParseException e) {
                               progressDialog.dismiss();
                               if (e == null) {
                                   activity.updateAnswersAdapter(answer);
                               } else {
                                   Toast.makeText(AddAnswerDialogFragment.this.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
                   }
               })
               .setNegativeButton(getString(R.string.cancel_dialog_fragment), new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                        AddAnswerDialogFragment.this.getDialog().cancel();
                   }
               });

        return builder.create();
    }
}