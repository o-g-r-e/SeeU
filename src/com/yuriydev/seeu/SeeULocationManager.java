package com.yuriydev.seeu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import android.location.LocationManager;
import android.widget.Toast;

public class SeeULocationManager extends Thread
{
	private AndroidGPSLocationListener gpsListener;
	private YandexLocationService yandexLocationService;
	private SeeUGlobalContext globalContext;
	private boolean cancel = false;
	private boolean locationChange = false;
	
	public SeeULocationManager(SeeUGlobalContext globalContext, MainActivity mainActivity)
	{
		this.globalContext = globalContext;
		this.gpsListener = new AndroidGPSLocationListener(globalContext, LocationManager.GPS_PROVIDER);
		this.yandexLocationService = new YandexLocationService(globalContext);
		this.start();
	}
	
	public void cancelManager()
	{
		cancel = true;
		
		if(yandexLocationService != null)
		{
			long sTime = System.currentTimeMillis();
			while (((System.currentTimeMillis() - sTime) < 17000)&&(yandexLocationService.isAlive()));
		}
	}
	
	private void doGPS()
	{
		ProvidersSwitcher.turnGPSOn(globalContext);
		long startTime = System.currentTimeMillis();
		while (((System.currentTimeMillis() - startTime) < 10000)&&(!ProvidersSwitcher.isGPSOn(globalContext))&&(!cancel));
		
		if(ProvidersSwitcher.isGPSOn(globalContext))
		{
			if(!cancel)
			{
				gpsListener.startLocationUpdates();
				globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.GPS_UPDATING_START, (byte)0, "Определение положения по GPS..."));
				
				long gpsMillisWaitingTime = globalContext.getApplicationState().getGpsSecondsWaitingTime()*1000;
				startTime = System.currentTimeMillis();
				while (((System.currentTimeMillis() - startTime) < gpsMillisWaitingTime)&&(!gpsListener.isLocationChanged())&&(!cancel));
				
				if(gpsListener.isLocationChanged())
				{
					String locationString = "\nшир: "+gpsListener.getLastLocation().getLatitude()+"\nдол: "+gpsListener.getLastLocation().getLongitude()+"\nрад: "+gpsListener.getLastLocation().getAccuracy()+"\nвремя: "+(System.currentTimeMillis() - startTime)+" ms";
					globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.GPS_LOCATION_CHANGED, (byte)0, "Определены координаты по GPS"+locationString));
					updateGlobalLocation(gpsListener.getLastLocation().getLatitude(), gpsListener.getLastLocation().getLongitude(), gpsListener.getLastLocation().getAccuracy());
					cancel = true;
				}
				else
				{	
					globalContext.getMainActivity().displayToast("Нет связи со спутниками.", Toast.LENGTH_SHORT);
				}
				
				gpsListener.removeLocationUpdates();
			}
		}
		else
		{
			//this.globalContext.getMainActivity().displayToast("Для определения вашего местоположения GPS должен быть включен.", Toast.LENGTH_LONG);
		}
		
		ProvidersSwitcher.turnGPSOff(globalContext);
	}
	
	private void doNetmonitoring()
	{
		boolean netEnabled = true;
		boolean needDisconnect = false;
		if(!ProvidersSwitcher.isInternetConnected(globalContext))
		{
			ProvidersSwitcher.switchNet(globalContext, true);
			
			long sTime = System.currentTimeMillis();
			while (((System.currentTimeMillis() - sTime) < 8000)&&(!ProvidersSwitcher.isInternetConnected(globalContext)));
			
			if(!ProvidersSwitcher.isInternetConnected(globalContext))
			{
				netEnabled = false;
			}
			
			needDisconnect = true;
		}
		
		if(netEnabled)
		{
			if(needDisconnect)
				globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.NET_CONNECTION_SUCCESS, (byte)0, "Подключение к сети установлено"));
			
			yandexLocationService.startLocationUpdates();
			
			long sTime = System.currentTimeMillis();
			while (((System.currentTimeMillis() - sTime) < 20000)&&(!yandexLocationService.isLocationChanged()));
			
			if(yandexLocationService.isLocationChanged())
			{
				updateGlobalLocation(yandexLocationService.getLastLatitude(), yandexLocationService.getLastLongitude(), yandexLocationService.getLastAccuracy());
				cancel = true;
			}
		}
		else
		{
			globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.NET_CONNECTION_FAIL, (byte)0, "Подключиться к сети не удалось"));
		}
		
		if(needDisconnect)
		{
			ProvidersSwitcher.switchNet(globalContext, false);
		}
	}
	
	@Override
	public void run()
	{
		if(globalContext.getApplicationState().isFalsificaeGeoCoordinates())
		{
			final Random random = new Random();
			double falseLat = 52.0 + (53.0 - 52.0) * random.nextDouble();
			double falseLng = 44.0 + (45.0 - 44.0) * random.nextDouble();
			
			updateGlobalLocation(falseLat, falseLng, 0.0f);
		}
		else
		{
			if(globalContext.getApplicationState().getCurrentLocationService() == ApplicationState.LOCATION_SERVICE_GPS)
			{
				doGPS();
				
				if((!cancel)&&(globalContext.getApplicationState().isAlternativeLocationServise()))
				{
					doNetmonitoring();
				}
			}
			else
			{
				doNetmonitoring();
				
				if((!cancel)&&(globalContext.getApplicationState().isAlternativeLocationServise()))
				{
					doGPS();
				}
			}
		}
			
		ArrayList<String> waitingsContactIDs = globalContext.getConnectionManager().getClientThread().getWaitingsContactIDs();
			
			if(waitingsContactIDs.size() > 0)
			{
				//ArrayList<String> waitingsClone;
				
				//synchronized (waitings)
				//{
					//waitingsClone = (ArrayList<String>) globalContext.getConnectionManager().getClientThread().getWaitings().clone();
					//globalContext.getConnectionManager().getClientThread().clearWaitings();
					//waitings.clear();
				//}
				
				for (int i = 0; i < waitingsContactIDs.size(); i++)
				{
					if(locationChange)
					{
						globalContext.getConnectionManager().getClientThread().sendMessage(new SeeUMessage(MessageType.LOCATION_RESPONSE, "",
			                    globalContext.getApplicationState().getClientId(),
			                    waitingsContactIDs.get(i),
			                    globalContext.getApplicationState().getUserLocationMarker().getLatitude(),
			                    globalContext.getApplicationState().getUserLocationMarker().getLongitude(),
			                    globalContext.getApplicationState().getUserLocationMarker().getPrecision(),
			                    new SeeUTimestamp(Calendar.getInstance())));
						
						globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.OUTGOING_MESSAGE, MessageType.LOCATION_RESPONSE, "Сообщено о местоположении. Получатель:"+globalContext.getApplicationState().getContactById(waitingsContactIDs.get(i)).getName()));
					}
					else
					{
						globalContext.getConnectionManager().getClientThread().sendMessage(new SeeUMessage(MessageType.COULD_NOT_DETERMINE_LOCATION, globalContext.getApplicationState().getClientId(), waitingsContactIDs.get(i)));
					}
				}
			}
		
	}
	
	private void updateGlobalLocation(double lat, double lng, float accuarcy)
	{
		locationChange = true;
		SeeUTimestamp timestamp = new SeeUTimestamp(Calendar.getInstance());
		SeeUMarker newUserMarker = new SeeUMarker(timestamp, lat, lng, (int) accuarcy, "вы здесь", "шир: "+lat+" дол: "+lng+" Радиус:"+accuarcy+" м.", R.drawable.ya_m);
		globalContext.getApplicationState().setUserLocationMarker(newUserMarker);
		globalContext.getGlobalObservableNode().notifyAboutNewMarker(newUserMarker);
	}
}