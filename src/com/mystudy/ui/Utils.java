/**
 * 
 */
package com.mystudy.ui;

import android.content.Context;
import android.widget.Toast;

/**
 * @author Administrator
 * 
 */
public class Utils {

	public static Toast myToast;

	public static void makeToast(Context mContext, String msg) {
		if (myToast == null) {
			myToast = Toast.makeText(mContext, "", 0);
		}
		myToast.setText(msg);
		myToast.show();
	}
}
