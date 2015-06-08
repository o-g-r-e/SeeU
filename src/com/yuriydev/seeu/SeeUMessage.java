package com.yuriydev.seeu;


import java.io.Serializable;

public class SeeUMessage implements Serializable
{
	private byte type = 0x00;
	private String senderId;
	private String recipientId;
	private String message;
	private double latitude = 0.0;
	private double longitude = 0.0;
	private int precision = 0;
	private SeeUTimestamp locationTimestamp;
	
	public SeeUMessage(byte type, String senderId, String recipientId)
	{
		this.type = type;
		this.message = "";
		this.senderId = senderId;
		this.recipientId = recipientId;
		this.latitude = 0.0;
		this.longitude = 0.0;
	}
	
	public SeeUMessage(byte type, String mess, String senderId, String recipientId)
	{
		this.type = type;
		this.message = mess;
		this.senderId = senderId;
		this.recipientId = recipientId;
		this.latitude = 0.0;
		this.longitude = 0.0;
	}
	
	public SeeUMessage(byte type, String mess, String senderId, String recipientId, double latitude, double longitude, int precision, SeeUTimestamp locationTimestamp)
	{
		this.type = type;
		this.message = mess;
		this.senderId = senderId;
		this.recipientId = recipientId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.precision = precision;
		this.locationTimestamp = locationTimestamp;
	}

	public SeeUMessage(byte type, String messsage)
	{
		this.type = type;
		this.message = messsage;
		this.senderId = "";
		this.recipientId = "";
		this.latitude = 0.0;
		this.longitude = 0.0;
	}
	
	public SeeUMessage()
	{
		this.type = 0x00;
		this.message = "";
		this.senderId = "";
		this.recipientId = "";
		this.latitude = 0.0;
		this.longitude = 0.0;
	}
	
	public String getSenderId() {
		return senderId;
	}

	public String getRecipientId() {
		return recipientId;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getMess() {
		return message;
	}

	public void setMess(String mess) {
		this.message = mess;
	}
	
	public int getPrecision() {
		return precision;
	}
	
	public SeeUTimestamp getLocationTimestamp() {
		return locationTimestamp;
	}
}