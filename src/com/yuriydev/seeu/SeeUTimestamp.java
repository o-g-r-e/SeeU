package com.yuriydev.seeu;

import java.io.Serializable;
import java.util.Calendar;

public class SeeUTimestamp implements Serializable
{
	private int day;
	private int month;
	private int year;
	private int shortYear;
	private int hour;
	private int minutes;
	private long millisecondsTime;
	private String dateTime;
	
	public SeeUTimestamp(Calendar calendar)
	{
		this.day = calendar.get(Calendar.DAY_OF_MONTH);
		this.month = calendar.get(Calendar.MONTH);
		if(this.month == 0)
			this.month = 1;
		this.year = calendar.get(Calendar.YEAR);
		this.shortYear = this.year - 2000;
		this.hour = calendar.get(Calendar.HOUR_OF_DAY);
		this.minutes = calendar.get(Calendar.MINUTE);
		this.millisecondsTime = this.hour*3600000+this.minutes*60000;
		String minutesS = String.valueOf(this.minutes);
		if(this.minutes < 10)
			minutesS = "0"+minutesS;
		this.dateTime = this.day+"."+this.month+"."+this.shortYear+" "+this.hour+" : "+minutesS;
	}
	
	public String getDateTime() {
		return dateTime;
	}

	public long getMillisecondsTime() {
		return millisecondsTime;
	}

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public int getShortYear() {
		return shortYear;
	}

	public int getHour() {
		return hour;
	}

	public int getMinutes() {
		return minutes;
	}
}
