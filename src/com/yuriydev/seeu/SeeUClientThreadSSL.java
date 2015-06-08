package com.yuriydev.seeu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.yuriydev.seeu.R;

import android.content.res.Resources.NotFoundException;
import android.util.Log;

public class SeeUClientThreadSSL extends Thread
{
	private SSLSocket socket = null;
	private ObjectInputStream inputObjectStream = null;
	private ObjectOutputStream outputObjectStream = null;
	private boolean exit = true;
	private boolean connectingState = false;
	private SeeUGlobalContext globalContext = null;
	private MainActivity mainActivity;
	private ArrayList<String> waitingForLocationResponseIds = new ArrayList<String>();
	
	
	SeeUClientThreadSSL(SeeUGlobalContext globalContext, MainActivity activityContext)
	{
    	this.globalContext = globalContext;
    	this.exit = true;
    	this.mainActivity = activityContext;
    	//this.socket = new SSLSocket();
    	this.start();
	}
	
	public void tryDisconnect()
	{
		try {
			if((socket != null)||(socket.isConnected()))
			{
				//outputObjectStream.writeObject(new SeeUMessage(MessageType.CLOSE_CONNECTION, globalContext.getApplicationState().getClientId()));
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(SeeUMessage message)
	{
		if(socket.isConnected())
		{
			try {
				outputObjectStream.writeObject(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void run()
	{
		exit = false;
		try {
			connectingState = true;
			globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.SERVER_CONNECTION_CONNETING, (byte)0, "Подключение к серверу..."));
			
			char[] passphrase = "android".toCharArray();
			KeyStore ksTrust;
			try {
				ksTrust = KeyStore.getInstance("BKS");
			
				ksTrust.load(globalContext.getResources().openRawResource(R.raw.sslcert), passphrase);
				TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				tmf.init(ksTrust);
				 
				// Create a SSLContext with the certificate
				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
			
			
			 
				// Create a HTTPS connection
				//URL url = new URL("https", "10.0.2.2", 8443, "/ssltest");
				//HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				 
				/* Uncomment the following line of code if you want to skip SSL */
				/* hostname verification.  But it should only be done for testing. */
				/* See http://randomizedsort.blogspot.com/2010/09/programmatically-disabling-java-ssl.html */
				/* conn.setHostnameVerifier(new NullVerifier()); */
				 
				//conn.setSSLSocketFactory(sslContext.getSocketFactory());
				
				//SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
				SSLSocketFactory sslsocketfactory = sslContext.getSocketFactory();
				socket = (SSLSocket) sslsocketfactory.createSocket("192.168.0.101",4444);
			
			} catch (KeyStoreException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (CertificateException e) {
				e.printStackTrace();
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			}
			globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.SERVER_CONNECTION_CONNECTED, (byte)0, "Соединение с сервером установлено"));
			connectingState = false;
			outputObjectStream = new ObjectOutputStream(socket.getOutputStream());
			inputObjectStream = new ObjectInputStream(socket.getInputStream());
			
			String clientId = globalContext.getApplicationState().getClientId();
			
			if((clientId == null)||(globalContext.getApplicationState().getClientId().length() == 0))
				globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.SERVER_CONNECTION_OBTAINING_ID, (byte)0, "Получение ID..."));
			
			//outputObjectStream.writeObject(new SeeUMessage(MessageType.ID_MESSAGE, clientId));
		} catch (UnknownHostException e) {
			Log.i("DATA","Socket UnknownHostException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.i("DATA","Socket IOException");
			exit = true;
			connectingState = false;
			e.printStackTrace();
		}
		
    	SeeUMessage message = null;
    	//int readyBytes = 0;
    	while(!exit)
    	{
    		message = new SeeUMessage();
    		//Log.i("DATA","while");
    		/*readyBytes = 0;
			try {
				readyBytes = inputObjectStream.available();
			} catch (IOException e1) {
				Log.i("DATA","available IOException");
				exit = true;
				e1.printStackTrace();
			}*/
			
			//if(readyBytes > 0)
			//{
				try {
    				Log.i("DATA","read");
    				message = (SeeUMessage) inputObjectStream.readObject();
    				Log.i("DATA","read ok");
				} catch (OptionalDataException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					exit = true;
				}
    		
				if(message.getType() == MessageType.ID_MESSAGE)
				{
					String newId = message.getMess();
					if(newId != null)
					{
						globalContext.getApplicationState().setClientId(message.getMess());
						globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.SERVER_CONNECTION_OBTAINED_ID, (byte)0, "Получен ID: "+message.getMess()));
						globalContext.saveAppStateToFile();
					}
				}
				
				if(message.getType() == MessageType.LOCATION_REQUEST)
				{
					Contact contact = globalContext.getApplicationState().getContactById(message.getSenderId());
					
					if(contact != null)
					{
						if(!contact.isWatching())
						{
							sendMessage(new SeeUMessage(MessageType.DENIED,
				                    globalContext.getApplicationState().getClientId(),
				                    message.getSenderId()));
		
							globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.OUTGOING_MESSAGE, MessageType.DENIED, "Пришёл запрос от "+contact.getName()+": В запросе отказано"));
						}
						else
						{
							if((globalContext.getApplicationState().getUserLocationMarker() == null)||((System.currentTimeMillis() - globalContext.getApplicationState().getUserLocationMarker().getTimestamp().getMillisecondsTime()) > globalContext.getApplicationState().getLocationUpdateInterval()))
							{
								//Если прошло достаточно много времени с момента последнего обновления по GPS
								synchronized (waitingForLocationResponseIds) {
									waitingForLocationResponseIds.add(message.getSenderId());
								}
								
								globalContext.initAndroidLocationManager();
							}
							else
							{
								//Если нет
								SeeUMarker userLocationMarker = globalContext.getApplicationState().getUserLocationMarker();
								sendMessage(new SeeUMessage(MessageType.LOCATION_RESPONSE, "",
										                    globalContext.getApplicationState().getClientId(),
										                    message.getSenderId(),
										                    userLocationMarker.getLatitude(),
										                    userLocationMarker.getLongitude(),
										                    userLocationMarker.getPrecision(),
										                    userLocationMarker.getTimestamp()));
							}
							
							globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.INCOMING_MESSAGE, MessageType.LOCATION_REQUEST, "Пришёл запрос местоположения от "+contact.getName()));
						}
					}
				}
				
				
				
				if(message.getType() == MessageType.DENIED)
				{
					Contact contact = globalContext.getApplicationState().getContactById(message.getSenderId());
					if(contact != null) globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.INCOMING_MESSAGE, MessageType.DENIED, contact.getName()+" Запретил доступ"));
				}
				
				if(message.getType() == MessageType.COULD_NOT_DETERMINE_LOCATION)
				{
					Contact contact = globalContext.getApplicationState().getContactById(message.getSenderId());
					if(contact != null) globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.INCOMING_MESSAGE, MessageType.COULD_NOT_DETERMINE_LOCATION, contact.getName()+"Не смог определить своё местоположение"));
				}
			//}
    	}
    	
    	try {
    		globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.SERVER_CONNECTION_DISCONNECT, (byte)0, "Соединение с сервером разорвано"));
    		Log.i("DATA","inputObjectStream closing...");
    		if(inputObjectStream  != null) inputObjectStream.close();
    		Log.i("DATA","outputObjectStream closing...");
    		if(outputObjectStream != null) outputObjectStream.close();
    		Log.i("DATA","socket closing...");
    		if(socket             != null) socket.close();
    		Log.i("DATA","socket closed");
    		//activityContext.displayConectionState(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean isConnectingState() {
		return connectingState;
	}
	public boolean isExit() {
		return exit;
	}
	public void setExit(boolean exit) {
		this.exit = exit;
	}
	public Socket getSocket() {
		return socket;
	}
	public ObjectInputStream getInputObjectStream() {
		return inputObjectStream;
	}
	public ObjectOutputStream getOutputObjectStream() {
		return outputObjectStream;
	}
	public boolean isSocketConnected(){
		return socket.isConnected();
	}
	public ArrayList<String> getWaitings() {
		return waitingForLocationResponseIds;
	}
}