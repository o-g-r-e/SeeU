package com.yuriydev.seeu;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

public class GoogleLocationListener implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener
{
	private LocationRequest mLocationRequest;
    private LocationClient mLocationClient;
    private long UPDATE_INTERVAL_IN_MILLISECONDS = 30000;
    private long FAST_INTERVAL_CEILING_IN_MILLISECONDS = 15000;
    
    private GoogleLocationListener(Context context)
    {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);
        mLocationClient = new LocationClient(context, this, this);
    }

	@Override
	public void onConnectionFailed(ConnectionResult result)
	{
	}
	
	

	@Override
	public void onConnected(Bundle connectionHint)
	{
	}

	@Override
	public void onDisconnected()
	{
	}
}