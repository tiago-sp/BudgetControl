package com.tiagopereirabr.budgetcontrol;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

public class AppDialog extends AppCompatDialogFragment {


    public static final String DIALOG_ID = "id";
    public static final String DIALOG_MESSAGE = "message";
    public static final String DIALOG_POSITIVE_RID = "positive_rid";
    public static final String DIALOG_NEGATIVE_RID = "negative_rid";

    /**
     * The dialogue's callbacks interfaces to notify of users selected results (deletion confirmed, etc..)
     */
    interface DialogEvents {
        void onPositiveDialogResult(int dialogID, Bundle args);

        void onNegativeDialogResult(int dialogID, Bundle args);

        void onDialogCancelled(int dialogID);
    }

    private DialogEvents mDialogEvents;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //Activities containing this fragment must implement its callbacks.

        if (!(context instanceof DialogEvents)) {
            throw new ClassCastException(context.toString() + "must implement AppDialog.DialogEvents interface");
        }

        mDialogEvents = (DialogEvents) context;
    }

    @Override
    public void onDetach() {
          super.onDetach();

        // Reset the active callbacks interface, because we don't have an activity any longer.
        mDialogEvents = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
         AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final Bundle arguments = getArguments();
        final int dialogId;
        String messageString;
        int positiveStringId;
        int negativeStringId;

        if (arguments != null) {
            dialogId = arguments.getInt(DIALOG_ID);
            messageString = arguments.getString(DIALOG_MESSAGE);

            if(dialogId == 0 || messageString == null){
                throw new IllegalArgumentException("DIALOG_ID and DIALOG_MESSAGE not present in the bundle");
            }
            positiveStringId = arguments.getInt(DIALOG_POSITIVE_RID);
            if (positiveStringId == 0) {
                positiveStringId = R.string.ok;
            }

            negativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID);
            if (negativeStringId == 0) {
                negativeStringId = R.string.cancel;
            }
        } else {
            throw new IllegalArgumentException("Must pass DIALOG_ID and DIALOG_MESSAGE in the bundle");
        }

        builder.setMessage(messageString)
                .setPositiveButton(positiveStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // callback positive result method
                        if(mDialogEvents != null) {
                            mDialogEvents.onPositiveDialogResult(dialogId, arguments);
                        }
                    }
                })
                .setNegativeButton(negativeStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // callback negative result method
                        if(mDialogEvents != null) {
                            mDialogEvents.onNegativeDialogResult(dialogId, arguments);
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {

        if(mDialogEvents != null) {
            try {
                int dialogId = getArguments().getInt(DIALOG_ID);
                mDialogEvents.onDialogCancelled(dialogId);
            }catch(NullPointerException e){
            }

        }
    }
}