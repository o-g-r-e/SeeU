package com.yuriydev.seeu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.widget.Toast;

public class SeeUService extends Service
{
	
	void cancelNotif()
	{
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(101);
	}

	void sendNotif()
	{
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this,
		        												0,
		        												notificationIntent,
		        												0);

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification.Builder builder = new Notification.Builder(this);

		builder.setContentIntent(contentIntent)
		            .setSmallIcon(R.drawable.eye_green64)
		            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.eye_green64))
		            //.setTicker("Последнее китайское предупреждение!")
		            //.setWhen(System.currentTimeMillis())
		            //.setDefaults(Notification.DEFAULT_VIBRATE)
		            .setAutoCancel(true)
		            .setContentTitle("See U")
		            .setContentText("Приём заросов, определение местоположения.");
		
		
		Notification notification = builder.build();
		
		notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
		nm.notify(101, notification);
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		//Toast.makeText(this, "Start Service", Toast.LENGTH_SHORT).show();
		sendNotif();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return Service.START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		//Toast.makeText(this, "Destroy Service", Toast.LENGTH_SHORT).show();
		//cancelNotif();
	}
}