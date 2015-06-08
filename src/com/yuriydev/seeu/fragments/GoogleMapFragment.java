package com.yuriydev.seeu.fragments;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.yuriydev.seeu.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yuriydev.seeu.Contact;
import com.yuriydev.seeu.MessageType;
import com.yuriydev.seeu.SeeUGlobalContext;
import com.yuriydev.seeu.SeeUMarker;
import com.yuriydev.seeu.SeeUMessage;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class GoogleMapFragment extends MapFragment implements Observer, OnMarkerClickListener
{
	private SeeUGlobalContext globalContext;
	private GoogleMap map;
	private Circle selectedMarkerRadius;
	private MarkerOptions createMarkerOptions(LatLng position, String title, String snippet, int markerBodyImageResId)
	{
		MarkerOptions options = new MarkerOptions();
		
		if(markerBodyImageResId != 0)
		{
			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(markerBodyImageResId);
			options.icon(bitmapDescriptor);
		}
		
		options.position(position);
		options.snippet(snippet);
		options.title(title);
		
		return options;
	}
	
	public void displayMarker(SeeUMarker marker)
	{
		MarkerOptions markerOptions = createMarkerOptions(new LatLng(marker.getLatitude(), marker.getLongitude()) , marker.getTitle(), marker.getSnippet(), marker.getMarkerBodyImageResId());
		map.addMarker(markerOptions);
	}
	
	public void displayMarkers()
	{
		if(globalContext.getApplicationState().getUserLocationMarker() != null)
			displayMarker(globalContext.getApplicationState().getUserLocationMarker());
		
		ArrayList<Contact> contacts = globalContext.getApplicationState().getContacts();
		
		int contactsSize = contacts.size();
		for (int i = 0; i < contactsSize; i++)
		{
			ArrayList<SeeUMarker> clientMarkers = contacts.get(i).getMarkers();
			
			for (int j = 0; j < clientMarkers.size(); j++)
			{
				if(clientMarkers.get(j).isVisible())
					displayMarker(clientMarkers.get(j));
			}
		}
	}
	
	/*public void removeMarker(Marker marker)
	{
		marker.remove();
	}*/
	
	/*public void removeMarkers()
	{
		if(globalContext.getApplicationState().getUserLocationMarker().getTimestamp() != null)
			removeMarker(((GoogleMapMarker)globalContext.getApplicationState().getUserLocationMarker()).getMarker());
		
		ArrayList<Contact> contacts = globalContext.getApplicationState().getContacts();
		
		int contactsSize = contacts.size();
		for (int i = 0; i < contactsSize; i++)
		{
			ArrayList<SeeUMarker> clientMarkers = contacts.get(i).getMarkers();
			
			int markersSize = clientMarkers.size();
			
			if(markersSize > 0)
			{
				for (int j = 0; j < markersSize; j++)
				{
					removeMarker(((GoogleMapMarker)clientMarkers.get(j)).getMarker());
				}
			}
		}
	}*/
	
	public void setMapCameraPosition(double lat, double lng, final float zoom)
	{
		final LatLng latlng = new LatLng(lat, lng);
		
		globalContext.getMainActivity().runOnUiThread(new Runnable()
		{
		    public void run()
		    {
		    	map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
		    }
		});
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	    Log.d("DATA", "SeeUMapFragment onAttach");
	  }
	  
	  
	public void onCreate(Bundle savedInstanceState)
	{
		globalContext = (SeeUGlobalContext) getActivity().getApplicationContext();
		globalContext.getGlobalObservableNode().addObserver(this);
		setHasOptionsMenu(true);
		map.setOnMarkerClickListener(this);
		super.onCreate(savedInstanceState);
	}
	
	private Circle drawCircle(double lat, double lng, int radius)
	{
		CircleOptions co = new CircleOptions();
		co.center(new LatLng(lat, lng));
		co.radius(radius);
		
		//co.fillColor(Color.TRANSPARENT);
        co.strokeColor(0x10FF0000);
        co.strokeWidth(5);
		
        return map.addCircle(co);
	}
	  
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	  
	public void onActivityCreated(Bundle savedInstanceState) {
		
		map = getMap();
		/*map.setOnMarkerClickListener(new OnMarkerClickListener(){
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				drawCircle(marker.getPosition().latitude, marker.getPosition().longitude);
				return false;
			}});*/
		displayMarkers();
		setMapCameraPosition(globalContext.getApplicationState().getMapCameraLat(), globalContext.getApplicationState().getMapCameraLng(), globalContext.getApplicationState().getCamZoom());
				
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.getUiSettings().setZoomControlsEnabled(false);
		super.onActivityCreated(savedInstanceState);
	}
	  
	public void onStart() {
	    super.onStart();
	}
	  
	public void onResume() {
		super.onResume();
	}
	  
	public void onPause() {
	    super.onPause();
	}
	  
	public void onStop() {
	    super.onStop();
	}
	  
	public void onDestroyView() {
	    super.onDestroyView();
	}
	  
	public void onDestroy() {
		globalContext.getApplicationState().setCameraPosition(map.getCameraPosition().target.latitude,//globalContext.getGoogleMap().getLastCameraPosition().latitude,
															  map.getCameraPosition().target.longitude,//globalContext.getGoogleMap().getLastCameraPosition().longitude,
															  map.getCameraPosition().zoom,//globalContext.getGoogleMap().getLastCameraZoom(),
															  map.getCameraPosition().tilt/*globalContext.getGoogleMap().getLastCameraTilt()*/);
		globalContext.getGlobalObservableNode().deleteObserver(this);
		super.onDestroy();
	}
	  
	public void onDetach() {
	    super.onDetach();
	}
	  
	@Override
	public void onDestroyOptionsMenu() {
		super.onDestroyOptionsMenu();
	}
	  
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.clear();
		inflater.inflate(R.menu.map, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.user_location_menu_item:
			globalContext.initAndroidLocationManager();
			break;
		
		case R.id.query_contact_location_menu_item:
			/*if(globalContext.getApplicationState().getClientId().length() > 0)
				new WhoDialog(this,(SeeUGlobalContext) getApplicationContext()).show();
			else
				Toast.makeText(globalContext, "error", Toast.LENGTH_SHORT).show();*/
			//globalContext.getConnectionManager().getClientThread().sendMessage(new SeeUMessage(MessageType.LOCATION_REQUEST, globalContext.getApplicationState().getClientId(), globalContext.getApplicationState().getClientId()));
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void update(Observable observable, final Object data)
	{
		if(data != null)
		{
			globalContext.getMainActivity().runOnUiThread(new Runnable()
			{
			    public void run()
			    {
			    	SeeUMarker marker = (SeeUMarker)data;
			    	map.clear();
					displayMarkers();
					//marker.getMarker().showInfoWindow();
					setMapCameraPosition(marker.getLatitude(), marker.getLongitude(), 10);
			    }
			});
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker)
	{
		//drawCircle(double lat, double lng, int radius);
		return false;
	}
}