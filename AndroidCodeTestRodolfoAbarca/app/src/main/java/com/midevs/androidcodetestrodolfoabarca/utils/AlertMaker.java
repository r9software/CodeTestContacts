package com.midevs.androidcodetestrodolfoabarca.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;


public class AlertMaker {
    public static void showOkAlert(Context context,
                                   @NonNull
                                           InfoMessage infoMessage,
                                   DialogInterface.OnClickListener listener) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(infoMessage.getTitle())
                .setMessage(infoMessage.getMessage())
                .setNegativeButton("OK", listener)
                .show();
    }


    public static void showConfirmAlert(Context context,
                                        @NonNull
                                                InfoMessage errorMessage,
                                        DialogInterface.OnClickListener onYes,
                                        DialogInterface.OnClickListener onNo) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(errorMessage.getTitle())
                .setMessage(errorMessage.getMessage())
                .setIcon(errorMessage.getIcon())
                .setPositiveButton("YES", onYes)
                .setNegativeButton("No", onNo)
                .show();
    }

    public static void showConfirmAlert(Context context,
                                        String title,
                                        String message,
                                        String negativeButtonName,
                                        String positiveButtonName,
                                        DialogInterface.OnClickListener onNo,
                                        DialogInterface.OnClickListener onYes) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonName, onYes)
                .setNegativeButton(negativeButtonName, onNo)
                .show();
    }

    public static void showConfirmAlert(Context context,
                                        String title,
                                        String message,
                                        String positiveButtonName,
                                        DialogInterface.OnClickListener listener) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonName, listener)
                .show();
    }

    public static void showConfirmAlert(Context context,
                                        InfoMessage infoMessage,
                                        DialogInterface.OnClickListener listener) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(infoMessage.getTitle())
                .setMessage(infoMessage.getMessage())
                .setNegativeButton("OK", listener)
                .show();
    }
}
