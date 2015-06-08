package com.yuriydev.seeu;

import java.util.Calendar;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class AndroidGPSLocationListener implements LocationListener
{
	private LocationManager locationManager;
	private SeeUGlobalContext globalContext;
	private String provider;
	private Location lastLocation;
	private boolean locationChanged = false;
	
	AndroidGPSLocationListener(SeeUGlobalContext globalContext, String provider)
	{
		this.globalContext = globalContext;
		this.provider = provider;
		this.locationChanged = false;
		this.locationManager = (LocationManager) this.globalContext.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public void removeLocationUpdates()
	{
		locationManager.removeUpdates(this);
		locationChanged = false;
	}

	public boolean isLocationChanged() {
		return locationChanged;
	}

	public void startLocationUpdates()
	{ 
		globalContext.getMainActivity().runOnUiThread(new Runnable()
		{
		    public void run()
		    {
		    	locationManager.requestLocationUpdates(provider, globalContext.getApplicationState().getGpsMinTime(), globalContext.getApplicationState().getGpsMinDistance(), AndroidGPSLocationListener.this);
		    }
		});
		
	}
	
	@Override
	public void onLocationChanged(Location location)
	{
		lastLocation = location;
		locationChanged = true;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	@Override
	public void onProviderDisabled(String arg0) {
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
	}
}