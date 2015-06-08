package com.yuriydev.seeu;

import java.io.Serializable;

public class SeeUEvent implements Serializable
{
	public byte getEventType() {
		return eventType;
	}

	public byte getEventMessageType() {
		return eventMessageType;
	}

	public final static byte INCOMING_MESSAGE = 0x01;
	public final static byte OUTGOING_MESSAGE = 0x02;
	public final static byte GPS_UPDATING_START = 0x03;
	public final static byte GPS_LOCATION_CHANGED = 0x04;
	public final static byte GPS_UPDATING_REMOVE = 0x05;
	public final static byte GPS_PROVIDER_ENABLE = 0x06;
	public final static byte GPS_PROVIDER_DISABLE = 0x07;
	public final static byte YANDEX_LOCATION_SERVICE_START = 0x08;
	public final static byte YANDEX_LOCATION_SERVICE_LOCATION_CHANGED = 0x09;
	public final static byte YANDEX_LOCATION_SERVICE_PHONE_PARAMETERS_READY = 0x10;
	public final static byte SERVER_CONNECTION_CONNETING = 0x11;
	public final static byte SERVER_CONNECTION_CONNECTED = 0x12;
	public final static byte SERVER_CONNECTION_DISCONNECT = 0x13;
	public final static byte SERVER_CONNECTION_OBTAINING_ID = 0x14;
	public final static byte SERVER_CONNECTION_OBTAINED_ID = 0x15;
	public final static byte TRYING_ENABLE_WIFI_PROVAIDER = 0x16;
	public final static byte WIFI_PROVAIDER_DISABLED = 0x17;
	public final static byte TRYING_ENABLE_MOBILE_DATA = 0x18;
	public final static byte MOBILE_DATA_DISABLED = 0x19;
	public final static byte NET_CONNECTION_FAIL = 0x20;
	public final static byte NET_CONNECTION_SUCCESS = 0x21;

	private byte eventType;
	private byte eventMessageType;
	private String eventMessage;
	//private String contactId;
	//private Contact contactProvenance;
	private SeeUTimestamp timestamp;
	
	public SeeUEvent(SeeUTimestamp timestamp, byte eventType, byte eventMessageType, /*String contactId,*/ String eventMessage/*, String messageAddition, Contact contactProvenance*/)
	{
		this.eventType = eventType;
		this.eventMessageType = eventMessageType;
		//this.contactId = contactId;
		
		this.timestamp = timestamp;
		
		//String dateTime = "["+this.timestamp.getDateTime()+"]: ";
		
		if(eventMessage == null)
		{
			/*switch (this.eventType)
			{
			case INCOMING_MESSAGE:
				switch (eventMessageType)
				{
				
				case MessageType.LOCATION_REQUEST:
					this.eventMessage = dateTime+"������ ������ �������������� ��";
					break;
					
				case MessageType.LOCATION_RESPONSE:
					this.eventMessage = dateTime+"�������� ������������� ��";
					break;
					
				case MessageType.DENIED:
					this.eventMessage = dateTime+"������ �������";
					break;
					
				case MessageType.USER_OFFLINE_MESSAGE:
					this.eventMessage = dateTime+"�� � ����";
					break;
					
				case MessageType.COULD_NOT_DETERMINE_LOCATION:
					this.eventMessage = dateTime+"�� ����� ���������� ��������������";
					break;

				default:
					break;
				}
				break;
				
			case OUTGOING_MESSAGE:
				switch (eventMessageType)
				{
				
				case MessageType.LOCATION_REQUEST:
					this.eventMessage = dateTime+"��������� ������ ��������������";
					break;
					
				case MessageType.LOCATION_RESPONSE:
					this.eventMessage = dateTime+"�������� � ��������������";
					break;
					
				case MessageType.DENIED:
					this.eventMessage = dateTime+"��������";
					break;
					
				case MessageType.COULD_NOT_DETERMINE_LOCATION:
					this.eventMessage = dateTime+"�� ���������� ���������� ��������������";
					break;

				default:
					break;
				}
				break;
				
			case GPS_PROVIDER_ENABLE:
				this.eventMessage = dateTime+"GPS �������";
				break;
				
			case GPS_UPDATING_START:
				this.eventMessage = dateTime+"����������� ��������� �� GPS...";
				break;
				
			case GPS_LOCATION_CHANGED:
				this.eventMessage = dateTime+"���������� ���������� �� GPS"+messageAddition;
				break;
				
			case GPS_PROVIDER_DISABLE:
				this.eventMessage = dateTime+"GPS ��������";
				break;
				
			case YANDEX_LOCATION_SERVICE_START:
				this.eventMessage = dateTime+"����������� ��������� �� \"������.�������\"...";
				break;
				
			case YANDEX_LOCATION_SERVICE_LOCATION_CHANGED:
				this.eventMessage = dateTime+"���������� ���������� �� \"������.�������\""+messageAddition;
				break;
				
			case YANDEX_LOCATION_SERVICE_PHONE_PARAMETERS_READY:
				this.eventMessage = dateTime+"���������� ��������� ���������:"+messageAddition;
				break;
				
			case SERVER_CONNECTION_CONNETING:
				this.eventMessage = dateTime+"����������� � �������...";
				break;
				
			case SERVER_CONNECTION_CONNECTED:
				this.eventMessage = dateTime+"���������� � �������� �����������";
				break;
				
			case SERVER_CONNECTION_DISCONNECT:
				this.eventMessage = dateTime+"���������� � �������� ���������";
				break;
				
			case SERVER_CONNECTION_OBTAINING_ID:
				this.eventMessage = dateTime+"��������� ID...";
				break;
				
			case SERVER_CONNECTION_OBTAINED_ID:
				this.eventMessage = dateTime+"������� ID: "+messageAddition;
				break;
				
			case WIFI_PROVAIDER_ENABLE:
				this.eventMessage = dateTime+"WiFi �������";
				break;
				
			case WIFI_PROVAIDER_DISABLE:
				this.eventMessage = dateTime+"WiFi ��������";
				break;
				
			case MOBILE_DATA_ENABLE:
				this.eventMessage = dateTime+"3G �������";
				break;
				
			case MOBILE_DATA_DISABLE:
				this.eventMessage = dateTime+"3G ��������";
				break;
	
			default:
				break;
			}*/
		}
		else
		{
			this.eventMessage = /*dateTime+""+*/eventMessage;
		}
	}

	/*public Contact getContactProvenance() {
		return contactProvenance;
	}*/

	public SeeUTimestamp getTimestamp() {
		return timestamp;
	}

	public String getEventMessage() {
		return eventMessage;
	}
	
	public void setEventMessage(String eventMessage) {
		this.eventMessage = eventMessage;
	}
	
	/*public SeeUEvent(SeeUTimestamp timestamp, byte eventType, byte eventMessageType, String eventMessage, String provenance)
	{
		this.eventType = eventType;
		this.eventMessageType = eventMessageType;
		this.eventMessage = eventMessage;
		this.provenance = provenance;
		this.timestamp = timestamp;
	}
	
	public SeeUEvent(SeeUTimestamp timestamp, byte eventType, String eventMessage, String provenance)
	{
		this.eventType = eventType;
		this.eventMessageType = 0x00;
		this.eventMessage = eventMessage;
		this.provenance = provenance;
		this.timestamp = timestamp;
	}*/
}