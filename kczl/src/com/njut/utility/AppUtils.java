package com.njut.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.util.Log;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class AppUtils {

	public static String getRGBByScore(int score) {
		score=score>92?92:score;//改变颜色太艳
		int red, green;
		if (score <= 60) {
			red = 255;
			green = 84;
			//return hsvToRgb(20, 100, 80);
		} else if (score >= 100) {
			red=84;
			green=255;
			//return hsvToRgb(100, 100, 80);
		} else {

			red = 255 - (int) (((score - 60) / 40.0) * 171); green = 255 -
					(int) (((100 - score) / 40.0) * 171);

			//return hsvToRgb(100 - (int) (((100 - score) / 40) * 80), 100, 80);

		}

		String redStr = Integer.toHexString(red).length() < 2 ? "0" +
				Integer.toHexString(red) : Integer.toHexString(red); String greenStr
				= Integer.toHexString(green).length() < 2 ? "0" +
						Integer.toHexString(green) : Integer.toHexString(green); 
						return "#" +redStr + greenStr + "00";


	}

	public static String getRGBByScore1(int score) {
		score=score>92?92:score;
		int red, green;
		if (score <= 60) {
			red = 255;
			green = 0;
		} else if (score >= 100) {
			red = 0;
			green = 255;
		} else {
			red = 255 - (int) (((score - 60) / 40.0) * 255) - 9;
			green = 255 - (int) (((100 - score) / 40.0) * 255) - 9;
		}
		if (red < 0)
			red = 0;
		if (green < 0)
			green = 0;
		String redStr = Integer.toHexString(red).length() < 2 ? "0"
				+ Integer.toHexString(red) : Integer.toHexString(red);
				String greenStr = Integer.toHexString(green).length() < 2 ? "0"
						+ Integer.toHexString(green) : Integer.toHexString(green);
						return "#" + redStr + greenStr + "00";
	}

	public static boolean areDatesSame(Date date1, Date date2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

		return sdf.format(date1).equals(sdf.format(date2));
	}

	public static SlidingMenu menu;

	public static String hsvToRgb(float hue, float saturation, float value) {

		int h = (int) (hue * 6);
		float f = hue * 6 - h;
		float p = value * (1 - saturation);
		float q = value * (1 - f * saturation);
		float t = value * (1 - (1 - f) * saturation);

		switch (h) {
		case 0:
			return rgbToString(value, t, p);
		case 1:
			return rgbToString(q, value, p);
		case 2:
			return rgbToString(p, value, t);
		case 3:
			return rgbToString(p, q, value);
		case 4:
			return rgbToString(t, p, value);
		case 5:
			return rgbToString(value, p, q);
		default: {
			//throw new RuntimeException(
			//		"Something went wrong when converting from HSV to RGB. Input was "
			//				+ hue + ", " + saturation + ", " + value);
			Log.e("COLOR",
					"Something went wrong when converting from HSV to RGB. Input was "
							+ hue + ", " + saturation + ", " + value);
			return "#000000";
		}
		}
	}

	public static String rgbToString(float r, float g, float b) {
		String rs = Integer.toHexString((int) (r * 256)).length() < 2 ? "0"
				+ Integer.toHexString((int) (r * 256)) : Integer
				.toHexString((int) (r * 256));
				String gs = Integer.toHexString((int) (g * 256)).length() < 2 ? "0"
						+ Integer.toHexString((int) (g * 256)) : Integer
						.toHexString((int) (g * 256));
						String bs = Integer.toHexString((int) (b * 256)).length() < 2 ? "0"
								+ Integer.toHexString((int) (b * 256)) : Integer
								.toHexString((int) (b * 256));
								return "#" + rs + gs + bs;
	}

	public static boolean isOpenNetwork(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	public static String getVersionName(Context context)
	{
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(packInfo != null)
			return packInfo.versionName;
		else
			return null;
	}

}
