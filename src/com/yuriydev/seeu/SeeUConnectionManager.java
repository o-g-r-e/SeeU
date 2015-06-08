package com.yuriydev.seeu;

import java.util.Timer;
import java.util.TimerTask;

import android.widget.Toast;

public class SeeUConnectionManager extends TimerTask
{
	private Timer timer;
	private SeeUClientThread clientThread;
	private SeeUGlobalContext globalContext;
	private MainActivity mainActivityContext;
	
	SeeUConnectionManager(SeeUGlobalContext globalContext)
	{
		this.globalContext = globalContext;
		this.mainActivityContext = globalContext.getMainActivity();
		this.timer = new Timer();
		this.timer.schedule(this, 0, 30000);
	}
	
	public SeeUClientThread getClientThread() {
		return clientThread;
	}
	
	public void cancelManager()
	{
		timer.cancel();
		if(clientThread != null) clientThread.tryDisconnect();
	}
	
	@Override
	public void run()
	{
		if((clientThread == null)||(!clientThread.isAlive()))
		{
			clientThread = new SeeUClientThread(globalContext, mainActivityContext);
		}
		
		try {
			Thread.sleep(8000, 0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(clientThread.isConnectingState())
		{
			clientThread.tryDisconnect();
		}
	}
}