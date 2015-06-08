package com.yuriydev.seeu.InfoActivity;

import java.util.Calendar;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.Contact;
import com.yuriydev.seeu.SeeUGlobalContext;
import com.yuriydev.seeu.SeeUMarker;
import com.yuriydev.seeu.SeeUTimestamp;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoActivity extends Activity
{
	private SeeUGlobalContext globalContext;
	//private ImageView serverConectionStatusImageView;
	//private TextView serverConectionStatusTextView;
	private TextView idTextView;
	private Button showIdButton;
	private TextView lastLocationTextView;
	private TextView lastLocationTimeTextView;
	
	/*public void displayConectionState(final int state)
	{
		runOnUiThread(new Runnable()
		{
		    public void run()
		    {
				switch (state)
				{
				case 1:
					serverConectionStatusTextView.setText("Offline");
					serverConectionStatusImageView.setImageResource(android.R.drawable.presence_invisible);
					break;
					
				case 2:
					serverConectionStatusTextView.setText("Соединение...");
					break;
					
				case 3:
					serverConectionStatusTextView.setText("Online");
					serverConectionStatusImageView.setImageResource(android.R.drawable.presence_online);
					break;
			
				case 4:
					serverConectionStatusTextView.setText("Получение id...");
					break;
					
				case 5:
					serverConectionStatusTextView.setText("Запрос обрабатывается...");
					break;
					
				default:
					break;
				}
		    }
		});
	}*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		
		globalContext = (SeeUGlobalContext) getApplicationContext();
		
		//serverConectionStatusImageView = (ImageView) findViewById(R.id.server_status_image);
		//serverConectionStatusTextView = (TextView) findViewById(R.id.server_status_text);
		idTextView = (TextView) findViewById(R.id.id_text_view);
		showIdButton = (Button) findViewById(R.id.show_id);
		lastLocationTextView = (TextView) findViewById(R.id.last_location_text_view);
		lastLocationTimeTextView = (TextView) findViewById(R.id.last_location_time_text_view);
		
		StringBuilder hidenUserId = new StringBuilder();
		String clientId = globalContext.getApplicationState().getClientId();
		if(clientId == null)
		{
			idTextView.setTextColor(globalContext.getResources().getColor(R.color.red));
			idTextView.setText("id: Отсутствует");
		}
		else
		{
			int userIDLength = globalContext.getApplicationState().getClientId().length();
			for (int i = 0; i < userIDLength; i++) hidenUserId.append("*");
			
			idTextView.setTextColor(globalContext.getResources().getColor(android.R.color.holo_blue_light));
			idTextView.setText("id: "+hidenUserId);
		}
		
		
		
		showIdButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
				String clientId = globalContext.getApplicationState().getClientId();
				builder.setMessage(clientId==null?"":clientId)
				       .setTitle("Ваш ID");
				
				AlertDialog dialog = builder.create();
				
				dialog.show();
			}
		});
		
		SeeUMarker userLocationMarker = globalContext.getApplicationState().getUserLocationMarker();
		
		if(userLocationMarker != null)
		{
			double userLat = userLocationMarker.getLatitude();
			double userLng = userLocationMarker.getLongitude();
			lastLocationTextView.setText("шир: "+userLat+" долг: "+userLng);
			
			/*long minutes = globalContext.getApplicationState().getLastLocationTime()/1000/60;
			long hours = minutes/60;
			minutes -= 60*hours;*/
			SeeUTimestamp time = globalContext.getApplicationState().getUserLocationMarker().getTimestamp();

			int hours = time.getHour();
			int minutes = time.getMinutes();
			int day = time.getDay();
			int month = time.getMonth();
			int shortYear = time.getShortYear();
			lastLocationTimeTextView.setText(day+"/"+month+"/"+shortYear+" "+hours+"ч. "+minutes+"мин.");
		}
	}
}