package com.yuriydev.seeu;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class NIOSeeuMessage implements Serializable
{
	public static byte HELLO_MESSAGE = 0x01;
	private byte[] byteMessage;
	private byte type;
	private StringBuilder parameter;
	
	NIOSeeuMessage(ByteBuffer byteBuffer)
	{
		byteBuffer.flip();
		this.byteMessage = new byte[byteBuffer.remaining()];
		
		byteBuffer.get(this.byteMessage, 0, this.byteMessage.length);
		this.type = this.byteMessage[0];
		this.parameter = new StringBuilder(this.byteMessage.length-1);
		for (int i = 1; i < this.byteMessage.length; i++)
		{
			parameter.append((char)this.byteMessage[i]);
		}
		byteBuffer.clear();
	}
	
	NIOSeeuMessage()
	{
		this.type = 0x00;
		this.parameter = new StringBuilder();
	}
	
	public byte[] create(byte type, String message)
	{
		this.type = type;
		this.byteMessage = new byte [message.length()+1];
		this.byteMessage[0] = type;
		for (int i = 1; i < this.byteMessage.length; i++) {
			this.byteMessage[i] = (byte) message.charAt(i-1);
		}
		return this.byteMessage;
	}
	
	public int getType() {
		return type;
	}
	
	public byte[] getByteMessage() {
		return byteMessage;
	}

	public StringBuilder getParameter() {
		return parameter;
	}
}
