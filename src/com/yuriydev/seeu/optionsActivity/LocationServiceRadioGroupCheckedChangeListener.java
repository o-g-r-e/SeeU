package com.yuriydev.seeu.optionsActivity;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.ApplicationState;
import com.yuriydev.seeu.SeeUGlobalContext;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

public class LocationServiceRadioGroupCheckedChangeListener implements RadioGroup.OnCheckedChangeListener
{
	private SeeUGlobalContext globalContext;
	private CheckBox alternativeGPSCheckBox;
	private CheckBox alternativeNetmonitoringCheckBox;
	
	LocationServiceRadioGroupCheckedChangeListener(SeeUGlobalContext globalContext, CheckBox alternativeGPSCheckBox, CheckBox alternativeNetmonitoringCheckBox)
	{
		this.globalContext = globalContext;
		this.alternativeGPSCheckBox = alternativeGPSCheckBox;
		this.alternativeNetmonitoringCheckBox = alternativeNetmonitoringCheckBox;
	}
	
	private void checkBoxDisable(CheckBox checkBox)
	{
		checkBox.setChecked(false);
		checkBox.setEnabled(false);
		checkBox.setTextColor(globalContext.getResources().getColor(android.R.color.darker_gray));
	}
	
	private void checkBoxEnable(CheckBox checkBox)
	{
		checkBox.setEnabled(true);
		checkBox.setTextColor(globalContext.getResources().getColor(android.R.color.black));
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		switch (checkedId)
		{
		case R.id.use_gps_radio_button:
			
			globalContext.getApplicationState().setCurrentLocationService(ApplicationState.LOCATION_SERVICE_GPS);
			checkBoxDisable(alternativeNetmonitoringCheckBox);
			checkBoxEnable(alternativeGPSCheckBox);
			break;
			
		case R.id.use_netmonitoring_radio_button:
			
			globalContext.getApplicationState().setCurrentLocationService(ApplicationState.LOCATION_SERVICE_NETMONITORING);
			checkBoxDisable(alternativeGPSCheckBox);
			checkBoxEnable(alternativeNetmonitoringCheckBox);
			break;
		}
	}
}