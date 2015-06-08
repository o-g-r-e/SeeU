package com.yuriydev.seeu;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Observable;

import com.yuriydev.seeu.R;

public class ApplicationState implements Serializable
{
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	
	private boolean falsificaeGeoCoordinates = false;
	private boolean savePreviousLocationMarkers = true;
	
	private String clientId;
	
	private double mapCameraLat = 55.0;
	private double mapCameraLng = 37.0;
	
	private float mapCameraZoom = 10.0f;
	private float mapCameraTilt = 0.0f;
	
	private long locationUpdateInterval = 0;
	
	private SeeUMarker userLocationMarker = null;
	private ArrayList<SeeUEvent> eventLog = new ArrayList<SeeUEvent>();
	
	private String prefferedDataCommunication = PREFFERED_WIFI;
	private String currentLocationService = LOCATION_SERVICE_GPS;
	private boolean alternativeLocationServise = false;
	
	public static String LOCATION_SERVICE_GPS = "location_service_gps";
	public static String LOCATION_SERVICE_NETMONITORING = "location_service_netmonitoring";
	
	public static String PREFFERED_WIFI = "wifi";
	public static String PREFFERED_MOBILE = "mobile";
	
	private long gpsMinTime = 5000;
	private float gpsMinDistance = 20.0f;
	
	private long gpsSecondsWaitingTime = 30;
	
	private boolean eventLogListMode = true;
	
	private boolean sslMode = false;
	
	private boolean firstStart = true;
	
	private String serverAddress = "82.146.34.133";
	private int serverPort = 4444;
	
	private int currentTab = 0;
	
	private boolean service = false;
	
	public void setService(boolean service) {
		this.service = service;
	}

	public String getServerAddress() {
		return serverAddress;
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public long getGpsSecondsWaitingTime() {
		return gpsSecondsWaitingTime;
	}
	
	public long getGpsMinTime() {
		return gpsMinTime;
	}
	
	public float getGpsMinDistance() {
		return gpsMinDistance;
	}
	
	public Contact getContactById(String id)
	{
		Contact foundContact = null;
		for (int i = 0; i < contacts.size(); i++)
		{
			Contact contact = contacts.get(i);
			if((contact.getId() != null)&&(contact.getId().equals(id)))
			{
				foundContact = contact;
				break;
			}
		}
		return foundContact;
	}
	
	public ArrayList<Contact> getValidContacts()
	{
		ArrayList<Contact> idsContacts = new ArrayList<Contact>();
		int size = contacts.size();
		
		for (int i = 0; i < size; i++)
		{
			Contact contact = contacts.get(i);
			
			if(contact.isValid())
				idsContacts.add(contact);
		}
		return idsContacts;
	}
	
	public Contact getContactByName(String name)
	{
		for (int i = 0; i < contacts.size(); i++)
		{
			if(contacts.get(i).getName().equals(name))
			{
				return contacts.get(i);
			}
		}
		
		return null;
	}
	
	public ArrayList<SeeUEvent> getEventLog(){
		return eventLog;
	}
	
	public SeeUMarker getUserLocationMarker() {
		return userLocationMarker;
	}
	
	public long getLocationUpdateInterval() {
		return locationUpdateInterval;
	}
	
	public ArrayList<Contact> getContacts() {
		return contacts;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public double getMapCameraLat() {
		return mapCameraLat;
	}
	
	public double getMapCameraLng() {
		return mapCameraLng;
	}
	
	public float getCamZoom() {
		return mapCameraZoom;
	}
	
	public float getCamTilt() {
		return mapCameraTilt;
	}
	
	public String getPrefferedDataCommunication() {
		return prefferedDataCommunication;
	}
	
	public String getCurrentLocationService() {
		return currentLocationService;
	}

	public int getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	public void setEventLogListMode(boolean eventLogListMode) {
		this.eventLogListMode = eventLogListMode;
	}
	
	public void setGpsSecondsWaitingTime(long gpsWaitingTime) {
		this.gpsSecondsWaitingTime = gpsWaitingTime;
	}

	public void setGpsMinTime(long gpsMinTime) {
		this.gpsMinTime = gpsMinTime;
	}

	public void setGpsMinDistance(float gpsMinDistance) {
		this.gpsMinDistance = gpsMinDistance;
	}
	
	public void setUserLocationMarker(SeeUMarker marker)
	{
		userLocationMarker = marker;
	}

	public void setLocationUpdateInterval(long locationUpdateInterval) {
		this.locationUpdateInterval = locationUpdateInterval;
	}

	public void setCameraPosition(double lat, double lng, float zoom, float tilt)
	{
		mapCameraLat = lat;
		mapCameraLng = lng;
		mapCameraZoom = zoom;
		mapCameraTilt = tilt;
	}
	
	public void setContacts(ArrayList<Contact> contacts) {
		this.contacts = contacts;
	}
	
	public void setFalsificaeGeoCoordinates(boolean falsificaeGeoCoordinates) {
		this.falsificaeGeoCoordinates = falsificaeGeoCoordinates;
	}
	
	public void setSavePreviousLocationMarkers(boolean savePreviousLocationMarkers) {
		this.savePreviousLocationMarkers = savePreviousLocationMarkers;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public void setPrefferedDataCommunication(String prefferedDataCommunication) {
		this.prefferedDataCommunication = prefferedDataCommunication;
	}

	public void setCurrentLocationService(String currentLocationService) {
		this.currentLocationService = currentLocationService;
	}
	
	public void setAlternativeLocationServise(boolean alternativeLocationServise) {
		this.alternativeLocationServise = alternativeLocationServise;
	}
	
	public void setFirstStart(boolean firstStart) {
		this.firstStart = firstStart;
	}

	public boolean isSslMode() {
		return sslMode;
	}

	public boolean isEventLogListMode() {
		return eventLogListMode;
	}
	
	public boolean isFalsificaeGeoCoordinates() {
		return falsificaeGeoCoordinates;
	}
	
	public boolean isSavePreviousLocationMarkers() {
		return savePreviousLocationMarkers;
	}
	
	public boolean isAlternativeLocationServise() {
		return alternativeLocationServise;
	}
	
	public boolean isFirstStart() {
		return firstStart;
	}
	
	public boolean isService() {
		return service;
	}
	
	public void addEvent(SeeUEvent event) {
		eventLog.add(event);
	}
	
	public void clearContacts()
	{
		contacts.clear();
		contacts.trimToSize();
	}
	
	public void clearEventLog() {
		eventLog.clear();
	}
}