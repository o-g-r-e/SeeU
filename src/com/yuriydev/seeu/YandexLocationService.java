package com.yuriydev.seeu;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Xml;

public class YandexLocationService extends Thread
{
	private URL url;
	private HttpURLConnection httpUrlConnection;
	private /*DataOutputStream*/OutputStream outputStream;
	private InputStream inputStream;
	private SeeUGlobalContext globalContext;
	
	int currentCID;
	int currentLAC;
	int currentMCC;
	int currentMNC;
	int currentGSMSignalStrenght;
	String ipv4;
	List<ScanResult> wifiList = null;
	List<NeighboringCellInfo> neighboringCellsList = null;
	
	WifiManager wifiManager;
	
	AndroidPhoneStateListener phoneStateListener;
	WifiReceiver wifiReceiver;
	
	private boolean locationChanged = false;
	private double lastLatitude;
	private double lastLongitude;
	private float lastAccuracy;
	
	private class AndroidPhoneStateListener extends PhoneStateListener
	{
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength)
        {
            super.onSignalStrengthsChanged(signalStrength);
            
            if (signalStrength.isGsm())
            {
            	
                if (signalStrength.getGsmSignalStrength() != 99)
                	currentGSMSignalStrenght = signalStrength.getGsmSignalStrength() * 2 - 113;
                else
                	currentGSMSignalStrenght = signalStrength.getGsmSignalStrength();
                
            }
            else
            {
            	currentGSMSignalStrenght = signalStrength.getCdmaDbm();
            }
        }
    }
	
	private class WifiReceiver extends BroadcastReceiver
    {
		WifiReceiver()
		{
			wifiManager = (WifiManager)globalContext.getSystemService(Context.WIFI_SERVICE);
		}
		
		@Override
        public void onReceive(Context c, Intent intent)
        {
            wifiList = wifiManager.getScanResults();
        }
    }
	
	YandexLocationService(SeeUGlobalContext globalContext)
	{
		this.globalContext = globalContext;
		globalContext.getMainActivity().runOnUiThread(new Runnable()
		{
		    public void run()
		    {
		    	YandexLocationService.this.phoneStateListener = new AndroidPhoneStateListener();
		    }
		});
		
		this.wifiReceiver = new WifiReceiver();
	}
	
	public void startLocationUpdates()
	{
		this.start();
	}
	
	private boolean wifiListReady()
	{
		if(wifiList != null)
		{
			return true;
		}
		return false;
	}
	
	private boolean mobileParametersReady()
	{
		if((currentGSMSignalStrenght != 0)&&(neighboringCellsList != null))
		{
			return true;
		}
		return false;
	}
	
	private boolean currentMobileNetworkParametersReady()
	{
		if((currentMCC != 0)&&(currentMNC != 0))
			return true;
		
		return false;
	}
	
	private boolean initCurrentMobileParamrters(TelephonyManager telephonyManager)
	{
		GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
		currentCID = cellLocation.getCid();
        currentLAC = cellLocation.getLac();
        
        String networkOperator = telephonyManager.getNetworkOperator();

        if (networkOperator != null)
        {
            currentMCC = Integer.parseInt(networkOperator.substring(0, 3));
            currentMNC = Integer.parseInt(networkOperator.substring(3));
        }
        
        if((currentCID!=0)&&(currentLAC!=0)&&(currentMCC!=0)&&(currentMNC!=0))
        	return true;
        
        return false;
	}
	
	private void initParameters()
	{
		TelephonyManager telephonyManager = (TelephonyManager) globalContext.getSystemService(Context.TELEPHONY_SERVICE);
		initCurrentMobileParamrters(telephonyManager);
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		
        neighboringCellsList = telephonyManager.getNeighboringCellInfo();
        
        globalContext.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        
        wifiManager.startScan();
        
        ipv4 = getipAddress();
	}
	
	private void parsingLocation(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		int eventType = parser.getEventType();
		
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			switch (eventType) {
			
			case XmlPullParser.START_TAG:
				
				if(parser.getName().equals("latitude"))
				{
					lastLatitude = Double.valueOf(parser.nextText());
				}
				
				if(parser.getName().equals("longitude"))
				{
					lastLongitude = Double.valueOf(parser.nextText());
				}
				
				if(parser.getName().equals("precision"))
				{
					lastAccuracy = Float.valueOf(parser.nextText());
				}
				
				break;

			default:
				break;
			}
			
			eventType = parser.next();
		}
	}
	
	private String parametersString(int currentMNC, List<NeighboringCellInfo> neighboringCellsList, List<ScanResult> wifiList, String ipv4)
	{
		String parametersString = "\n";
		if(currentMNC != 0)
			parametersString += "Текущая сеть: есть\n";
		else
			parametersString += "Текущая сеть: недоступна\n";
		
		if(neighboringCellsList != null)//if(neighboringCellsList.get(0).getCid() != NeighboringCellInfo.UNKNOWN_CID)
			parametersString += "Соседние соты: "+neighboringCellsList.size()+"\n";
		else
			parametersString += "Соседние соты: 0\n";
		
		if(wifiList != null)
			parametersString += "Доступные wifi сети: "+wifiList.size()+"\n";
		else
			parametersString += "Доступные wifi сети: 0\n";
		
		if(ipv4 != null)
			parametersString += "IP: "+ipv4+"\n";
		else
			parametersString += "IP: null\n";
		
		return parametersString;
	}
	
	@Override
	public void run()
	{
		globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.YANDEX_LOCATION_SERVICE_START, (byte)0, "Определение положения по \"Яндекс.Локатор\"..."));
		
		initParameters();
		
		long sTime = System.currentTimeMillis();
		
		while (((System.currentTimeMillis() - sTime) < 15000)&&(!currentMobileNetworkParametersReady()))
		{
			TelephonyManager telephonyManager = (TelephonyManager) globalContext.getSystemService(Context.TELEPHONY_SERVICE);
			initCurrentMobileParamrters(telephonyManager);
		}

		sTime = System.currentTimeMillis();
		while (((System.currentTimeMillis() - sTime) < 15000)&&(!mobileParametersReady()));
		
		if(ProvidersSwitcher.isWiFiConnnected(globalContext))
		{
			sTime = System.currentTimeMillis();
			while (((System.currentTimeMillis() - sTime) < 15000)&&(!wifiListReady()));
		}
		
		String parametersString = parametersString(currentMNC, neighboringCellsList, wifiList, ipv4);
		globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.YANDEX_LOCATION_SERVICE_PHONE_PARAMETERS_READY, (byte)0, "Определены следующие параметры:"+parametersString));
		
		try {
			url = new URL("http://api.lbs.yandex.net/geolocation");
			
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setRequestMethod("POST");
			httpUrlConnection.setConnectTimeout(8000);
			httpUrlConnection.setReadTimeout(8000);
			byte[] data = xmlParam(currentCID, currentLAC, currentMCC, currentMNC, currentGSMSignalStrenght, 1000, neighboringCellsList, wifiList, 1000, ipv4);
			//httpUrlConnection.setRequestProperty("Accept-Encoding", "identity");
			//httpUrlConnection.setRequestProperty("Content-length", ""+data.length());
			//httpUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			
			outputStream = new BufferedOutputStream(httpUrlConnection.getOutputStream());
			
			//outputStream.writeBytes(data);
			outputStream.write(data);
			outputStream.flush();
			
			int responseCode = httpUrlConnection.getResponseCode();
			int len = httpUrlConnection.getContentLength();
			if(len > 0)
			{
				byte[] bytesResponse = new byte[len];
				StringBuilder stringResponse = new StringBuilder(len);
				
				if(responseCode == 200) //OK
				{
					inputStream = new BufferedInputStream(httpUrlConnection.getInputStream());
		            int bytesRead = inputStream.read(bytesResponse);
				}
				else
				{
					inputStream = httpUrlConnection.getErrorStream();
					inputStream.read(bytesResponse);
				}
				
				for (int i = 0; i < bytesResponse.length; i++)
				{
					stringResponse.append((char)bytesResponse[i]);
				}
				
				XmlPullParser parser = Xml.newPullParser();
	            try {
					parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
					parser.setInput(new StringReader(stringResponse.toString()));
					parsingLocation(parser);
					locationChanged = true;
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				}
				
				globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.YANDEX_LOCATION_SERVICE_LOCATION_CHANGED, (byte)0, "Определены координаты по \"Яндекс.Локатор\""+"\nКод: "+responseCode+"\n"+stringResponse));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
            if (outputStream != null)
            {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                	
                }
            }
            
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                	
                }
            }
            
            if (httpUrlConnection != null) {
            	httpUrlConnection.disconnect();
            }
            
            //finished = true;
        }
	}
	
	private String getipAddress()
	{
        try {
        	
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    
                    if ((!inetAddress.isLoopbackAddress())&& (InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())))
                    {
                        String ipaddress=inetAddress.getHostAddress().toString();
                        return ipaddress;
                    }
                }
            }
            
        } catch (SocketException e) {
        }
        return null; 
}
	
	private byte[] xmlParam(int currentCID, int currentLAC, int currentMCC, int currentMNC, int currentGSMSignalStrenght, long gsmAge, List<NeighboringCellInfo> neighboringCellsList, List<ScanResult> wifiList, long wifiAge, String ip) throws IOException
	{
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		
		result.write("xml=".getBytes());
		result.write("<ya_lbs_request>\n".getBytes());
		result.write("<common>\n".getBytes());
		result.write("<version>1.0</version>\n".getBytes());
		result.write("<api_key>ABM6WU0BAAAANfFuIQIAV1pUEYIBeogyUNvVbhNaJPWeM-AAAAAAAAAAAACRXgDsaYNpZWpBczn4Lq6QmkwK6g==</api_key>\n".getBytes());
		result.write("</common>\n".getBytes());
		result.write("<gsm_cells>\n".getBytes());
		
		result.write("<cell>\n".getBytes());
		result.write(("<countrycode>"+currentMCC+"</countrycode>\n").getBytes());
		result.write(("<operatorid>"+currentMNC+"</operatorid>\n").getBytes());
		result.write(("<cellid>"+currentCID+"</cellid>\n").getBytes());
		result.write(("<lac>"+currentLAC+"</lac>\n").getBytes());
		result.write(("<signal_strength>"+currentGSMSignalStrenght+"</signal_strength>\n").getBytes());
		result.write(("<age>"+gsmAge+"</age>\n").getBytes());
		result.write("</cell>\n".getBytes());
		
		if(neighboringCellsList != null)
		{
			for (int i = 0; i < neighboringCellsList.size(); i++)
			{
				if(neighboringCellsList.get(i).getCid() != NeighboringCellInfo.UNKNOWN_CID)
				{
					result.write("<cell>\n".getBytes());
					result.write(("<countrycode>"+currentMCC+"</countrycode>\n").getBytes());
					result.write(("<operatorid>"+currentMNC+"</operatorid>\n").getBytes());
					result.write(("<cellid>"+neighboringCellsList.get(i).getCid()+"</cellid>\n").getBytes());
					result.write(("<lac>"+neighboringCellsList.get(i).getLac()+"</lac>\n").getBytes());
					result.write(("<signal_strength>"+neighboringCellsList.get(i).getRssi()+"</signal_strength>\n").getBytes());
					result.write(("<age>"+gsmAge+"</age>").getBytes());
					result.write("</cell>".getBytes());
				}
			}
		}
		
		result.write("</gsm_cells>\n".getBytes());
		result.write("<wifi_networks>\n".getBytes());
		
		if(wifiList != null)
		{
			for (int i = 0; i < wifiList.size(); i++)
			{
				result.write("<network>\n".getBytes());
				result.write(("<mac>"+wifiList.get(i).BSSID+"</mac>\n").getBytes());
				result.write(("<signal_strength>"+wifiList.get(i).level+"</signal_strength>\n").getBytes());
				if(i == 0)
					result.write(("<age>"+wifiAge+"</age>\n").getBytes());
				result.write("</network>\n".getBytes());
			}
		}
		
		
		result.write("</wifi_networks>\n".getBytes());
		result.write("<ip>\n".getBytes());
		result.write(("<address_v4>"+ip+"</address_v4>\n").getBytes());
		result.write("</ip>\n".getBytes());
		result.write("</ya_lbs_request>\n".getBytes());
		//return result.toString();
		return result.toByteArray();
	}
	
	public boolean isLocationChanged() {
		return locationChanged;
	}

	public double getLastLatitude() {
		return lastLatitude;
	}

	public double getLastLongitude() {
		return lastLongitude;
	}

	public float getLastAccuracy() {
		return lastAccuracy;
	}
	
	/*public boolean isFinished() {
		return finished;
	}*/
}
