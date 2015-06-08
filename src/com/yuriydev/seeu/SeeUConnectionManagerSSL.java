package com.yuriydev.seeu;

import java.util.Timer;
import java.util.TimerTask;

public class SeeUConnectionManagerSSL extends TimerTask
{
	private Timer timer = null;
	private SeeUClientThreadSSL clientThread = null;
	private SeeUGlobalContext globalContext = null;
	private MainActivity mainActivityContext = null;
	
	SeeUConnectionManagerSSL(SeeUGlobalContext globalContext)
	{
		this.globalContext = globalContext;
		this.mainActivityContext = globalContext.getMainActivity();
		//this.clientThread = globalContext.getClientThread();
		this.timer = new Timer();
		this.timer.schedule(this, 0, 30000);
	}
	
	public SeeUClientThreadSSL getClientThread() {
		return clientThread;
	}
	
	public void cancelManager()
	{
		timer.cancel();
		if(clientThread != null) clientThread.tryDisconnect();
		
		long sTime = System.currentTimeMillis();
		while (((System.currentTimeMillis() - sTime) < 3000)&&(clientThread.isAlive()));
	}
	
	@Override
	public void run()
	{
		if((clientThread == null)||(!clientThread.isAlive()))
		{
			clientThread = new SeeUClientThreadSSL(globalContext, mainActivityContext);
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