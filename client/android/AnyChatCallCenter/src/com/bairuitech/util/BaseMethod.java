package com.bairuitech.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class BaseMethod {

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static void showToast(String strContent, Activity context) {
		Toast mToast = new Toast(context);
		View view = context.getLayoutInflater().inflate(
				com.bairuitech.callcenter.R.layout.common_toastview, null);
		TextView textView = (TextView) view
				.findViewById(com.bairuitech.callcenter.R.id.txt_toast);
		textView.setText(strContent);
		mToast.setView(view);
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.show();
	}

	public static String getVersion(Context context) {
		try {
			return "Version:"
					+ context.getPackageManager().getPackageInfo(
							"com.bairuitech.icloundsoft", 0).versionName;

		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "";

		}
	}

	/***
	 * 发送广播
	 * 
	 * @param context
	 *            上下文
	 * @param strAction
	 *            动作
	 * @param bundle
	 *            数据
	 */
	public static void sendBroadCast(Context context, String strAction,
			Bundle bundle) {
		Intent intent = new Intent();
		if (bundle != null)
			intent.putExtras(bundle);
		intent.setAction(strAction);
		context.sendBroadcast(intent);

	}

	public static String getStrTime(final Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String strTime = "";
		try {
			strTime = sdf.format(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strTime;

	}

	public static Date getDateTime(final String strTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = sdf.parse(strTime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;

	}
}
