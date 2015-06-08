package com.yuriydev.seeu.helpActivity;

import com.yuriydev.seeu.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class HelpActivity extends Activity
{
	private Button confirmButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		Button confirmButton = (Button) findViewById(R.id.confirm_button);
		
		confirmButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				finish();
			}});
	}
}