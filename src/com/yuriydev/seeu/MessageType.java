package com.yuriydev.seeu;

public class MessageType
{
	public static final byte EXCEPTION_MESSAGE = 0x01;
	public static final byte LOCATION_REQUEST = 0x02;
	public static final byte LOCATION_RESPONSE = 0x03;
	public static final byte USER_OFFLINE_MESSAGE = 0x04;
	//public static final byte CLOSE_CONNECTION = 0x05;
	public static final byte ID_MESSAGE = 0x06;
	//public static final byte DENIED_CONTACT = 0x07;
	//public static final byte DENIED_WATCHING = 0x08;
	public static final byte DENIED = 0x07;
	public static final byte COULD_NOT_DETERMINE_LOCATION = 0x08;
}