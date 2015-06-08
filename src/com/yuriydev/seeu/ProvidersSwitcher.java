package com.yuriydev.seeu;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;

public class ProvidersSwitcher
{
	public static boolean isWiFiConnnected(SeeUGlobalContext globalContext)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) globalContext.getSystemService(Application.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifiInfo.isConnected();
	}
	
	public static boolean isMobileConnnected(SeeUGlobalContext globalContext)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) globalContext.getSystemService(Application.CONNECTIVITY_SERVICE);
		NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return mobileInfo.isConnected();
	}
	
	public static boolean isInternetConnected(SeeUGlobalContext globalContext)
	{
		if((isWiFiConnnected(globalContext))||(isMobileConnnected(globalContext)))
			return true;
		
		return false;
	}
	
	public static void switchNet(SeeUGlobalContext globalContext, boolean enable)
	{
		if(globalContext.getApplicationState().getPrefferedDataCommunication().equals(ApplicationState.PREFFERED_WIFI))
			switchWifi(globalContext, enable);
		else
			try {
				switchMobileData(globalContext, enable);
			} catch (IllegalArgumentException e) {
				//e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
	}
	
	public static boolean switchWifi(SeeUGlobalContext globalContext, boolean enable)
	{
		if(enable)
			globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.TRYING_ENABLE_WIFI_PROVAIDER, (byte)0, "Включение Wi-Fi..."));
		else
			globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.WIFI_PROVAIDER_DISABLED, (byte)0, "WiFi Выключен"));
		WifiManager wifiManager = (WifiManager)globalContext.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.setWifiEnabled(enable);
	}
	
	public static void turnGPSOn(SeeUGlobalContext globalContext)
	{
		globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.GPS_PROVIDER_ENABLE, (byte)0, "GPS Включен"));
		
	    String provider = Settings.Secure.getString(globalContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

	    if(!provider.contains("gps"))
	    {
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        globalContext.sendBroadcast(poke);
	    }
	}

	public static void turnGPSOff(SeeUGlobalContext globalContext)
	{
		globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.GPS_PROVIDER_DISABLE, (byte)0, "GPS Выключен"));
		
	    String provider = Settings.Secure.getString(globalContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

	    if(provider.contains("gps"))
	    {
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        globalContext.sendBroadcast(poke);
	    }
	}
	
	public static boolean isGPSOn(SeeUGlobalContext globalContext)
	{
		LocationManager locationManager = (LocationManager) globalContext.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	public static void switchMobileData(Context context, boolean enable) throws ClassNotFoundException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
	{
		
		if(enable)
			((SeeUGlobalContext)context).getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.TRYING_ENABLE_MOBILE_DATA, (byte)0, "Включение Mobile Data..."));
		else
			((SeeUGlobalContext)context).getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.WIFI_PROVAIDER_DISABLED, (byte)0, "Mobile Data Выключен"));
			
		   ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
		   
		   Class conmanClass = Class.forName(conman.getClass().getName());
		   
		   Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
		   
		   iConnectivityManagerField.setAccessible(true);
		   
		   Object iConnectivityManager = iConnectivityManagerField.get(conman);
		   
		   Class iConnectivityManagerClass =  Class.forName(iConnectivityManager.getClass().getName());
		   
		   Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		   
		   setMobileDataEnabledMethod.setAccessible(true);

		   setMobileDataEnabledMethod.invoke(iConnectivityManager, enable);
	}
}