package com.progresee.app.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DateUtils {
	
	public static String formatDate() {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String dateString = format.format(Calendar.getInstance().getTime());
		return dateString;
	}


}
