package com.yuriydev.seeu;


import com.yuriydev.seeu.R;
import com.yuriydev.seeu.InfoActivity.InfoActivity;
import com.yuriydev.seeu.fragments.ContactListFragment;
import com.yuriydev.seeu.fragments.EventLogFragment;
import com.yuriydev.seeu.fragments.GoogleMapFragment;
import com.yuriydev.seeu.fragments.YandexMapFragment;
import com.yuriydev.seeu.helpActivity.HelpActivity;
import com.yuriydev.seeu.optionsActivity.OptionsActivity;
import com.yuriydev.seeu.phoneBookActivity.PhoneBookActivity;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener
{
	private SeeUGlobalContext globalContext;
	
	private Fragment mapFragment;
	private Fragment contactListFragment;
	private Fragment eventLogFragment;
	private Fragment currentFragment;
	
	private Tab mapTab;
	private Tab contactsTab;
	private Tab eventsTab;
	private ActionBar bar;
	
	public void displayToast(final String message, final int length)
	{
		runOnUiThread(new Runnable()
		{
		    public void run()
		    {
		    	Toast.makeText(globalContext, message, length).show();
		    }
		});
	}
	
	public void setMapFragment(Fragment newMapFragment)
	{
		if(currentFragment == mapFragment)
		{
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.fragments_content, newMapFragment);
			fragmentTransaction.commit();
		}
			
		mapFragment = newMapFragment;
		currentFragment = mapFragment;
	}
	
	private void configureTabHost(FragmentTabHost tabHost, Bundle savedInstanceState)
	{
		//tabHost.setup();
		//tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        TabHost.TabSpec tabSpec;
        
        /*tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator("Карта");
        tabSpec.setContent(R.id.tab1);
        tabHost.addTab(tabSpec);
        
        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator("Контакты");
        tabSpec.setContent(R.id.tab2);        
        tabHost.addTab(tabSpec);
        
        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setIndicator("Настройки");
        tabSpec.setContent(R.id.tab3);        
        tabHost.addTab(tabSpec);*/
        
        //tabSpec = tabHost.newTabSpec("tag1");
        
		/*tabHost.addTab(tabHost.newTabSpec("map").setIndicator("Карта"),
                com.example.seeu.mapActivity.SeeUMapFragment.class, null);*/
        tabSpec = tabHost.newTabSpec("tab1");
        tabSpec.setIndicator("map");
        //tabSpec.setContent(R.id.fragment1id);
		tabHost.addTab(tabSpec,com.yuriydev.seeu.fragments.GoogleMapFragment.class, savedInstanceState);
		
		/*tabHost.addTab(tabHost.newTabSpec("contacts").setIndicator("Контакты"),
                com.example.seeu.contactListActivity.ContactListFragment.class, null);*/
		
		/*tabHost.addTab(tabHost.newTabSpec("options").setIndicator("Настройки"),
                com.example.seeu.optionsActivity.OptionsFragment.class, null);*/
		
		/*tabHost.addTab(tabHost.newTabSpec("events").setIndicator("События"),
                com.example.seeu.eventLogActivity.EventLogFragment.class, null);*/

        /*mTabsAdapter = new TabsAdapter(this, tabHost, mViewPager);
        
        mTabsAdapter.addTab(tabHost.newTabSpec("map").setIndicator("Карта"), MapActivity.class, null);
        mTabsAdapter.addTab(tabHost.newTabSpec("contacts").setIndicator("Контакты"), ContactListActivity.class, null);
        mTabsAdapter.addTab(tabHost.newTabSpec("options").setIndicator("Настройки"), OptionsFragment.class, null);
        mTabsAdapter.addTab(tabHost.newTabSpec("events").setIndicator("Журнал"), EventLogActivity.class, null);*/
 
        //if (savedInstanceState != null) {
            //tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        //}
        
        //tabHost.setCurrentTabByTag("tag1");
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
    	globalContext = (SeeUGlobalContext)getApplicationContext();
    	
    	globalContext.setMainActivity(this);
    	
    	stopService(new Intent(this, SeeUService.class));
    		
    	mapFragment = new YandexMapFragment();
    	
    	currentFragment = mapFragment;
    	contactListFragment = new ContactListFragment();
    	eventLogFragment = new EventLogFragment();
    	
    	globalContext.initConnectionManager();
    	
    	bar = getActionBar();

        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mapTab = bar.newTab();
        mapTab.setText("Карта");
        mapTab.setTag("map");
        mapTab.setTabListener(this);
        bar.addTab(mapTab);

        contactsTab = bar.newTab();
        contactsTab.setText("Контакты");
        contactsTab.setTag("contacts");
        contactsTab.setTabListener(this);
        bar.addTab(contactsTab);
        
        eventsTab = bar.newTab();
        eventsTab.setText("События");
        eventsTab.setTag("events");
        eventsTab.setTabListener(this);
        bar.addTab(eventsTab);
        
        bar.setSelectedNavigationItem(globalContext.getApplicationState().getCurrentTab());
        
        if(globalContext.getApplicationState().isFirstStart())
		{
			startActivity(new Intent(globalContext, HelpActivity.class));
			globalContext.getApplicationState().setFirstStart(false);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		bar.setSelectedNavigationItem(globalContext.getApplicationState().getCurrentTab());
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.fragments_content, currentFragment );
		fragmentTransaction.commit();
		
	}
	
	@Override
    protected void onDestroy()
	{
		if(globalContext.getAndroidLocationManager() != null) globalContext.getAndroidLocationManager().cancelManager();
			
		if(globalContext.getApplicationState().isService())
		{
			startService(new Intent(this, SeeUService.class));
		}
		else
		{
			if(globalContext.getConnectionManager() != null) globalContext.getConnectionManager().cancelManager();
		}
		
				
		
		globalContext.saveAppStateToFile();
		globalContext.setMainActivity(null);
		
		
        super.onDestroy();
    }
	

	public Fragment getMapFragment() {
		return mapFragment;
	}

	@Override
	public void onTabReselected(Tab arg0, android.app.FragmentTransaction arg1) {
		
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft)
	{
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		
		if("Карта".equals(tab.getText()))
		{
			fragmentTransaction.replace(R.id.fragments_content, mapFragment);
			currentFragment = mapFragment;
		}
		
		if("Контакты".equals(tab.getText()))
		{
			fragmentTransaction.replace(R.id.fragments_content, contactListFragment);
			currentFragment = contactListFragment;
		}
		
		if("События".equals(tab.getText()))
		{
			fragmentTransaction.replace(R.id.fragments_content, eventLogFragment);
			currentFragment = eventLogFragment;
		}
		
		fragmentTransaction.commit();
		
		globalContext.getApplicationState().setCurrentTab(bar.getSelectedNavigationIndex());
	}

	public ActionBar getBar() {
		return bar;
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.remove(currentFragment);
		fragmentTransaction.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.options:
			startActivity(new Intent(globalContext, OptionsActivity.class));
			break;
			
		case R.id.show_id_menu:
			String clientId = globalContext.getApplicationState().getClientId();
			
			if(clientId==null)
			{
				Toast.makeText(this, "У вас ещё нету ID", Toast.LENGTH_SHORT).show();
			}
			else
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				
				builder.setMessage("Ваш ID: "+(clientId==null?"":clientId));
				//builder.setTitle("Ваш ID");
				AlertDialog dialog = builder.create();
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.show();
			}
			break;
			
		case R.id.help_menu:
			startActivity(new Intent(globalContext, HelpActivity.class));
			break;
			
		case R.id.exit:
			finish();
			break;
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}