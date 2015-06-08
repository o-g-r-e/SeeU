package com.yuriydev.seeu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;

import android.util.Log;
import android.widget.Toast;

public class SeeUClientThread extends Thread
{
	private Socket socket;
	private ObjectInputStream inputObjectStream;
	private ObjectOutputStream outputObjectStream;
	private boolean connectingState;
	private SeeUGlobalContext globalContext;
	private MainActivity activityContext;
	private ArrayList<String> waitingForLocationResponseIds;
	private ApplicationState appState;
	private InetSocketAddress serverInetAddress;
	
	
	SeeUClientThread(SeeUGlobalContext globalContext, MainActivity activityContext)
	{
    	this.globalContext = globalContext;
    	this.appState = globalContext.getApplicationState();
    	this.activityContext = activityContext;
    	this.waitingForLocationResponseIds = new ArrayList<String>();
    	this.socket = new Socket();
    	this.serverInetAddress = new InetSocketAddress(appState.getServerAddress(), appState.getServerPort());
    	this.start();
	}
	
	public void tryDisconnect()
	{
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int sendMessage(SeeUMessage message)
	{
		if((globalContext.getApplicationState().getClientId() == null)||(globalContext.getApplicationState().getClientId().length() == 0))
		{
			return -2;
		}
		
		if(socket.isConnected())
		{
			try {
				outputObjectStream.writeObject(message);
				return 1;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return -1;
	}
	
	private void messageProcessing(SeeUMessage message)
	{
		Contact contact = appState.getContactById(message.getSenderId());
		
		switch (message.getType())
		{
		case MessageType.ID_MESSAGE:
			
			String newId = message.getMess();
			if(newId != null)
			{
				appState.setClientId(message.getMess());
				appState.addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.SERVER_CONNECTION_OBTAINED_ID, (byte)0, "Получен ID: "+message.getMess()));
				activityContext.displayToast("Теперь у вас есть персональный ID", Toast.LENGTH_LONG);
				globalContext.saveAppStateToFile();
			}
			
			break;
			
		case MessageType.LOCATION_REQUEST:
			
			if(contact != null)
			{
				if(!contact.isWatching())
				{
					sendMessage(new SeeUMessage(MessageType.DENIED, appState.getClientId(), message.getSenderId()));
					appState.addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.OUTGOING_MESSAGE, MessageType.DENIED, "Пришёл запрос от "+contact.getName()+": В запросе отказано"));
				}
				else
				{
					//activityContext.displayToast("Принят запрос от "+contact.getName(), Toast.LENGTH_SHORT);
					if((appState.getUserLocationMarker() == null)||((System.currentTimeMillis() - appState.getUserLocationMarker().getTimestamp().getMillisecondsTime()) > appState.getLocationUpdateInterval()))
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
						SeeUMarker userLocationMarker = appState.getUserLocationMarker();
						sendMessage(new SeeUMessage(MessageType.LOCATION_RESPONSE, "",
													appState.getClientId(),
								                    message.getSenderId(),
								                    userLocationMarker.getLatitude(),
								                    userLocationMarker.getLongitude(),
								                    userLocationMarker.getPrecision(),
								                    userLocationMarker.getTimestamp()));
					}
					
					appState.addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.INCOMING_MESSAGE, MessageType.LOCATION_REQUEST, "Пришёл запрос местоположения от "+contact.getName()));
				}
			}
			
			break;
			
		case MessageType.LOCATION_RESPONSE:
			
			if(contact != null)
			{
				SeeUMarker newMarker = new SeeUMarker(message.getLocationTimestamp(), message.getLatitude(), message.getLongitude(), message.getPrecision(), message.getLocationTimestamp().getDateTime()+"\n"+contact.getName(), "шир: "+message.getLatitude()+" дол: "+message.getLongitude(), 0);
				
				if(appState.isSavePreviousLocationMarkers())
				{
					contact.addMarker(newMarker);
				}
				else
				{
					contact.substituteLastMarker(newMarker);
				}
				globalContext.getGlobalObservableNode().notifyAboutNewMarker(newMarker);
				appState.addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.INCOMING_MESSAGE, MessageType.LOCATION_RESPONSE, "Получено местоположене от "+contact.getName()));
			}
			
			break;
			
		case MessageType.DENIED:
			
			if(contact != null)
			{
				appState.addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.INCOMING_MESSAGE, MessageType.DENIED, contact.getName()+" Запретил доступ"));
			}
			
			break;
	
		case MessageType.COULD_NOT_DETERMINE_LOCATION:
			
			if(contact != null)
			{
				appState.addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.INCOMING_MESSAGE, MessageType.COULD_NOT_DETERMINE_LOCATION, contact.getName()+"Не смог определить своё местоположение"));
			}
			
			break;
	
		case MessageType.USER_OFFLINE_MESSAGE:
			
			if(contact != null)
			{
				globalContext.getMainActivity().displayToast(contact.getName()+" не в сети", Toast.LENGTH_SHORT);
			}
			
			break;
		}
	}

	public void run()
	{
		try {
			
			connectingState = true;
			appState.addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.SERVER_CONNECTION_CONNETING, (byte)0, "Подключение к серверу..."));
			socket.connect(serverInetAddress);
			appState.addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.SERVER_CONNECTION_CONNECTED, (byte)0, "Соединение с сервером установлено"));
			connectingState = false;
			outputObjectStream = new ObjectOutputStream(socket.getOutputStream());
			inputObjectStream = new ObjectInputStream(socket.getInputStream());
			
			String clientId = appState.getClientId();
			
			if((clientId == null)||(appState.getClientId().length() == 0))
				appState.addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.SERVER_CONNECTION_OBTAINING_ID, (byte)0, "Получение ID..."));
			
			outputObjectStream.writeObject(new SeeUMessage(MessageType.ID_MESSAGE, appState.getClientId()));
			
	    	while(true)
	    	{
	    		SeeUMessage message = new SeeUMessage();
	    		message = (SeeUMessage) inputObjectStream.readObject();
				messageProcessing(message);
	    	}
		} catch (Exception e) {
			
			connectingState = false;
			
		} finally {
			
			try {
	    		appState.addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.SERVER_CONNECTION_DISCONNECT, (byte)0, "Соединение с сервером разорвано"));
	    		if(inputObjectStream  != null) inputObjectStream.close();
	    		if(outputObjectStream != null) outputObjectStream.close();
	    		if(socket             != null) socket.close();
			} catch (IOException e) {
				
			}
			
		}
    	
    	
	}
	public boolean isConnectingState() {
		return connectingState;
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
	public ArrayList<String> getWaitingsContactIDs() {
		return waitingForLocationResponseIds;
	}
}