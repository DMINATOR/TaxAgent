package com.dmitrikuznetsov.dklib.ui.base;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

import com.dmitrikuznetsov.dklib.tools.localization.Translate;


/**
 * Used for showing different types of dialogs
 * @author dmitrikuznetsov
 *
 */
public class DKDialog 
{
	
	public static void ShowMessage(
			DKActivityBase activity, 
			Translate 	title, 
			Translate 	message,
			Translate	okText,
			Translate	cancelText
			)
	{
		Builder dialog = new AlertDialog.Builder(activity)
         .setIcon(android.R.drawable.ic_dialog_alert)
         .setTitle(title.translate(activity))
         .setMessage(message.translate(activity))
         .setPositiveButton(okText.translate(activity), new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int whichButton) {

                 /* User clicked OK so do some stuff */
             }
         })
         .setNegativeButton(cancelText.translate(activity), new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int whichButton) {

                 /* User clicked Cancel so do some stuff */
             }
         });
		
         dialog.create().show();
	}
}
