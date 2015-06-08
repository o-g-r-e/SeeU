package com.yuriydev.seeu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import com.yuriydev.seeu.R;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class SeeUGlobalContext extends Application
{
	private boolean clearSettings = false;
	private ApplicationState applicationState;
	
	private MainActivity mainActivity;
	private SeeUConnectionManager connectionManager;
	private SeeUConnectionManagerSSL connectionManagerSSL;
	private SeeULocationManager androidLocationManager;
	private GlobalObservableNode globalObservableNode;
	
	public GlobalObservableNode getGlobalObservableNode() {
		return globalObservableNode;
	}
	
	public int sendMessage(SeeUMessage message) {
		return connectionManager.getClientThread().sendMessage(message);
	}
	
	public boolean initAndroidLocationManager()
	{
		if((androidLocationManager == null)||(!androidLocationManager.isAlive()))
		{
			androidLocationManager = new SeeULocationManager(this, mainActivity/*, googleMap*/);
			return true;
		}
		return false;
	}
	
	public SeeULocationManager getAndroidLocationManager()
	{
		return androidLocationManager;
	}
	
	public void initConnectionManager()
	{
		if((connectionManager == null)||(!connectionManager.getClientThread().isAlive()))
			connectionManager = new SeeUConnectionManager(this);
	}

	public SeeUConnectionManager getConnectionManager()
	{
		return connectionManager;
	}
	
	public SeeUConnectionManagerSSL getSSLConnectionManager()
	{
		return connectionManagerSSL;
	}

	public MainActivity getMainActivity()
	{
        return mainActivity;
	}
	
	public void setMainActivity(MainActivity mainActivity)
	{
		this.mainActivity = mainActivity;
	}
	
	public void deleteAppSettingsFile()
	{
		deleteFile(getString(R.string.app_states));
	}
	
	public String getStackTrace(Exception e)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	public void saveAppStateToFile()
	{
		FileOutputStream fileOutStream = null;
		ObjectOutputStream objectOutStream = null;
		try {
			fileOutStream = openFileOutput(getString(R.string.app_states), Context.MODE_PRIVATE);
			objectOutStream = new ObjectOutputStream(fileOutStream);
	        objectOutStream.writeObject(applicationState);
	        //mainActivity.displayToast("save app", Toast.LENGTH_SHORT);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			//mainActivity.displayToast("saveAppSTateToFile FileNotFoundException", Toast.LENGTH_SHORT);
			connectionManager.getClientThread().sendMessage(new SeeUMessage(MessageType.EXCEPTION_MESSAGE, getStackTrace(e)));
		} catch (IOException e) {
			e.printStackTrace();
			//mainActivity.displayToast("saveAppSTateToFile IOException", Toast.LENGTH_SHORT);
			connectionManager.getClientThread().sendMessage(new SeeUMessage(MessageType.EXCEPTION_MESSAGE, getStackTrace(e)));
		} finally {
			try {
				if(objectOutStream != null) objectOutStream.close();
				if(fileOutStream != null) fileOutStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadAppStateFromFile()
	{
		FileInputStream fileInStream = null;
		ObjectInputStream objectInStream = null;
		try {
			fileInStream = openFileInput(getString(R.string.app_states));
			objectInStream = new ObjectInputStream(fileInStream);
			applicationState = (ApplicationState) objectInStream.readObject();
			//mainActivity.displayToast("load app", Toast.LENGTH_SHORT);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(applicationState == null) applicationState = new ApplicationState();
			
			/*ArrayList<Contact> contacts = new ArrayList<Contact>();
			
			Contact contact1 = new Contact();
			contact1.setName("Contact1");
			contact1.setNumber("+79999999999");
			
			Contact contact2 = new Contact();
			contact2.setName("Contact2");
			contact2.setNumber("+78888888888");
			
			Contact contact3 = new Contact();
			contact3.setName("Contact3");
			contact3.setNumber("+77777777777");
			
			contacts.add(contact1);
			contacts.add(contact2);
			contacts.add(contact3);
			
			applicationState.setContacts(contacts);*/
			
			try {
				if(objectInStream != null) objectInStream.close();
				if(fileInStream != null) fileInStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onCreate()
	{
		if(clearSettings)
			deleteAppSettingsFile();
		loadAppStateFromFile();
		
		globalObservableNode = new GlobalObservableNode();
		
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	public ApplicationState getApplicationState() {
		return applicationState;
	}
	
	public boolean isClearSettings() {
		return clearSettings;
	}
}