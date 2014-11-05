package com.njut.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/*
 * @author Hsiao(cx@njut.edu.cn)
 */

public class CalendarHelper {

	public static String getPassedDays(Date currentDate,String beginDate)
	{
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date mydate= myFormatter.parse(beginDate);
			int day=(int)((currentDate.getTime()-mydate.getTime())/(24*60*60*1000));
			return day+"";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} 
	}

	public int getDay(String currentDate,String beginDate) {
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date;
		try {
			date = myFormatter.parse(currentDate);
			java.util.Date mydate= myFormatter.parse(beginDate);
			int day=(int)((date.getTime()-mydate.getTime())/(24*60*60*1000));
			return (day%7+1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 8;
		} 
	}

	public int getWeek(String currentDate,String beginDate) {
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date;
		try {
			date = myFormatter.parse(currentDate);
			java.util.Date mydate= myFormatter.parse(beginDate);
			int day=(int)((date.getTime()-mydate.getTime())/(24*60*60*1000));
			return (int)day/7+1;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} 
	}
}
