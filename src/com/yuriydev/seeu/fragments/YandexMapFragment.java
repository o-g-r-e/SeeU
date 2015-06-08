package com.yuriydev.seeu.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import com.yuriydev.seeu.ApplicationState;
import com.yuriydev.seeu.R;
import com.yuriydev.seeu.MainActivity;
import com.yuriydev.seeu.Contact;
import com.yuriydev.seeu.MessageType;
import com.yuriydev.seeu.SeeUEvent;
import com.yuriydev.seeu.SeeUGlobalContext;
import com.yuriydev.seeu.SeeUMarker;
import com.yuriydev.seeu.SeeUMessage;
import com.yuriydev.seeu.SeeUTimestamp;
import com.yuriydev.seeu.whoDialog.WhoDialog;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.map.MapEvent;
import ru.yandex.yandexmapkit.map.OnMapListener;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.overlay.location.MyLocationItem;
import ru.yandex.yandexmapkit.overlay.location.OnMyLocationListener;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class YandexMapFragment extends Fragment implements Observer, OnMyLocationListener
{
	private SeeUGlobalContext globalContext;
	private MapView mapView;
	private MapController mapController;
	private Overlay markersOverlay;
	private Spinner spinner;
	private ViewGroup spinnerViewGroup;
	private ArrayAdapter<String> spinnerArrayAdapter;
	private LinearLayout ll;
	
	public void displayMarker(SeeUMarker marker)
	{
		int imgResId = marker.getMarkerBodyImageResId();
		if(imgResId == 0)
			imgResId = R.drawable.ya_m;
		
		GeoPoint geoPoint = new GeoPoint(marker.getLatitude(), marker.getLongitude());
		
		OverlayItem overlayItem = new OverlayItem(geoPoint, globalContext.getResources().getDrawable(imgResId));
		BalloonItem balloon = new BalloonItem(getActivity(), geoPoint);
		balloon.setText(marker.getTitle()+"\n"+marker.getSnippet());
		balloon.setVisible(true);
		overlayItem.setBalloonItem(balloon);
		markersOverlay.addOverlayItem(overlayItem);
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
			/*
			clientMarkers.add(new SeeUMarker(new SeeUTimestamp(Calendar.getInstance()), 56.3210373, 38.1166121, 0, "Анна", "шир: "+56.3210373+" дол: "+38.1166121, 0));
			clientMarkers.add(new SeeUMarker(new SeeUTimestamp(Calendar.getInstance()), 56.3110373, 38.1066121, 0, "Анна", "шир: "+56.3110373+" дол: "+38.1066121, 0));
			clientMarkers.add(new SeeUMarker(new SeeUTimestamp(Calendar.getInstance()), 56.2910373, 38.1066121, 0, "Анна", "шир: "+56.2910373+" дол: "+38.1066121, 0));
			clientMarkers.add(new SeeUMarker(new SeeUTimestamp(Calendar.getInstance()), 56.2920373, 38.1466121, 0, "Анна", "шир: "+56.2920373+" дол: "+38.1466121, 0));
			clientMarkers.add(new SeeUMarker(new SeeUTimestamp(Calendar.getInstance()), 56.3020373, 38.1416121, 0, "Анна", "шир: "+56.3020373+" дол: "+38.1416121, 0));
			clientMarkers.add(new SeeUMarker(new SeeUTimestamp(Calendar.getInstance()), 56.3040373, 38.1216121, 0, "Анна", "шир: "+56.3040373+" дол: "+38.1216121, 0));
			clientMarkers.add(new SeeUMarker(new SeeUTimestamp(Calendar.getInstance()), 56.3240373, 38.1416121, 0, "Анна", "шир: "+56.3240373+" дол: "+38.1416121, 0));
			*/
			for (int j = 0; j < clientMarkers.size(); j++)
			{
				if(clientMarkers.get(j).isVisible())
					displayMarker(clientMarkers.get(j));
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		globalContext = (SeeUGlobalContext) getActivity().getApplicationContext();
		globalContext.getGlobalObservableNode().addObserver(this);
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		displayMarkers();
		
		super.onActivityCreated(savedInstanceState);
	}
	
	public void setMapCameraPosition(double lat, double lng, float zoom)
	{
		mapController.setPositionAnimationTo(new GeoPoint(lat, lng));
		mapController.setZoomCurrent(zoom);
	}
	
	private String[] initSpinnerArray()
	{
		ArrayList<String> array = new ArrayList<String>();
		if(globalContext.getApplicationState().getUserLocationMarker() != null)
		{
			array.add("Моё положение");
		}
		
		ArrayList<Contact> contacts = globalContext.getApplicationState().getContacts();
		
		for (int i = 0; i < contacts.size(); i++)
		{
			Contact contact = contacts.get(i);
			if((contact.getMarkers() != null)&&(contact.getMarkers().size() > 0))
			{
				array.add(contact.getName());
			}
		}
		
		String[] spinnerArray = array.toArray(new String[array.size()]);
		
		return spinnerArray;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_yandex_map, null);
		spinner = (Spinner) view.findViewById(R.id.spinner1);
		ll = (LinearLayout) view.findViewById(R.id.sp);
		spinnerViewGroup = (ViewGroup)ll.getParent();
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
            	TextView selectedText = (TextView) view.findViewById(R.id.text1);
            	
            	if(selectedText.getText().equals("Моё положение"))
            	{
            		SeeUMarker userMarker = globalContext.getApplicationState().getUserLocationMarker();
            		setMapCameraPosition(userMarker.getLatitude(), userMarker.getLongitude(), globalContext.getApplicationState().getCamZoom());
            	}
            	else
            	{
	            	Contact contact = globalContext.getApplicationState().getContactByName((String)selectedText.getText());
	            	if(contact != null)
	            	{
	            		SeeUMarker lastMarker = contact.getLastMarker();
	            		setMapCameraPosition(lastMarker.getLatitude(), lastMarker.getLongitude(), globalContext.getApplicationState().getCamZoom());
	            	}
            	}
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
          });
		
		String[] spinnerArray = initSpinnerArray();
		
		if(spinnerArray.length > 0)
		{
			spinnerArrayAdapter = new ArrayAdapter<String>(globalContext, R.layout.map_spinner_item, R.id.text1, spinnerArray);
			spinnerArrayAdapter.setDropDownViewResource(R.layout.map_spinner_item_drop_down);
			spinner.setAdapter(spinnerArrayAdapter);
	        
	        //spinner.setSelection(0);
		}
		else
		{
			ll.setVisibility(View.INVISIBLE);
			spinnerViewGroup.removeView(ll);
		}
        
		mapView = (MapView) view.findViewById(R.id.yandex_map_view);
		
		mapController = mapView.getMapController();
		OverlayManager overlayManager = mapController.getOverlayManager();
		//overlayManager.getMyLocation().addMyLocationListener(this);
		overlayManager.getMyLocation().setEnabled(false);
		setMapCameraPosition(globalContext.getApplicationState().getMapCameraLat(), globalContext.getApplicationState().getMapCameraLng(), globalContext.getApplicationState().getCamZoom());
		markersOverlay = new Overlay(mapController);
		overlayManager.addOverlay(markersOverlay);
		
		return view;
	}
	
	@Override
	public void onDestroy()
	{
		globalContext.getApplicationState().setCameraPosition(mapController.getMapCenter().getLat(),
															  mapController.getMapCenter().getLon(),
															  mapController.getZoomCurrent(),
															  0.0f);
		//Toast.makeText(getActivity(), mapController.getMapCenter().getLat()+" "+ mapController.getMapCenter().getLon()+" "+mapController.getZoomCurrent(), Toast.LENGTH_SHORT).show();
		globalContext.getGlobalObservableNode().deleteObserver(this);
		super.onDestroy();
	}

	@Override
	public void update(Observable observable, final Object object)
	{
		if((object instanceof SeeUMarker)&&(object != null))
		{
			globalContext.getMainActivity().runOnUiThread(new Runnable()
			{
			    public void run()
			    {
			    	SeeUMarker marker = (SeeUMarker)object;
			    	markersOverlay.clearOverlayItems();
					displayMarkers();
					setMapCameraPosition(marker.getLatitude(), marker.getLongitude(), 10);
					
					if(!ll.isShown())
					{
						spinnerViewGroup.addView(ll, 0);
						ll.setVisibility(View.VISIBLE);
					}
					
					String[] spinnerArray = initSpinnerArray();
					
					spinnerArrayAdapter = new ArrayAdapter<String>(globalContext, R.layout.map_spinner_item, R.id.text1, spinnerArray);
					spinnerArrayAdapter.setDropDownViewResource(R.layout.map_spinner_item_drop_down);
					spinner.setAdapter(spinnerArrayAdapter);
					
					//spinner.setOnItemSelectedListener(spinnerItemSelectedListener);
			    }
			});
		}
	}
	
	@Override
	public void onMyLocationChange(MyLocationItem arg0) {
		Toast.makeText(globalContext, "Yandex onMyLocationChange", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
			globalContext.getMainActivity().displayToast("Местоположение вычисляеться...", Toast.LENGTH_SHORT);
			globalContext.initAndroidLocationManager();
			break;
		
		case R.id.query_contact_location_menu_item:
			new WhoDialog(globalContext.getMainActivity(), globalContext).show();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}