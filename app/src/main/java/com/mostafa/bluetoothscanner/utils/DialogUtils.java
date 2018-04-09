package com.mostafa.bluetoothscanner.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Window;
import android.widget.TextView;

import com.mostafa.bluetoothscanner.R;

/**
 * Created by Mostafa on 11/15/2017.
 */

@SuppressWarnings("WeakerAccess")
public class DialogUtils {

    public static Dialog showProgressDialog(@NonNull Context context, @StringRes int message, boolean showImmediately) {
        Dialog progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.dialog_progress);
        changeProgressDialogMessage(progressDialog, message);
        //noinspection ConstantConditions
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (showImmediately) {
            progressDialog.show();
        }
        return progressDialog;
    }

    public static void changeProgressDialogMessage(@NonNull Dialog progressDialog, @StringRes int message) {
        TextView messageTextView = progressDialog.findViewById(R.id.message_text_view);
        messageTextView.setText(message);
    }

}
