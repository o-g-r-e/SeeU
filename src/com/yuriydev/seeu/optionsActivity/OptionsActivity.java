package com.yuriydev.seeu.optionsActivity;

import java.util.ArrayList;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.Contact;
import com.yuriydev.seeu.SeeUGlobalContext;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class OptionsActivity extends Activity
{
	ListView list;
	OptionsListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		
		list = (ListView) findViewById(R.id.options_list_view);
		adapter = new OptionsListAdapter((SeeUGlobalContext)getApplicationContext(), this);  
        list.setAdapter(adapter);
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		SeeUGlobalContext globalContext = (SeeUGlobalContext) getApplicationContext();
		globalContext.saveAppStateToFile();
	}
}