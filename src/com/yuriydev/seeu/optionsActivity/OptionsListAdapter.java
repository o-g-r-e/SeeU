package com.yuriydev.seeu.optionsActivity;


import java.net.InetSocketAddress;
import java.util.ArrayList;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.ApplicationState;
import com.yuriydev.seeu.Contact;
import com.yuriydev.seeu.SeeUGlobalContext;
import com.yuriydev.seeu.fragments.GoogleMapFragment;
import com.yuriydev.seeu.fragments.YandexMapFragment;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class OptionsListAdapter extends BaseAdapter
{
	private SeeUGlobalContext globalContext;
	private LayoutInflater layoutInflater;
	private final static int LIST_SIZE = 7;
	private Activity activityForDialog;
	private EditText serverIPEditText;
	
	public OptionsListAdapter(SeeUGlobalContext globalContext, Activity activityForDialog)
    {
        this.globalContext = globalContext;
        this.activityForDialog = activityForDialog;
        this.layoutInflater = (LayoutInflater) this.globalContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = null;
        
        switch (position)
        {
		case 0:
			
			rowView = layoutInflater.inflate(R.layout.list_item_options_checkbox, parent, false);
			
			CheckBox markersShowModecheckBox = (CheckBox)rowView.findViewById(R.id.check_box);
			markersShowModecheckBox.setText("Показывать на карте результаты только последних запросов.");
			markersShowModecheckBox.setChecked(!globalContext.getApplicationState().isSavePreviousLocationMarkers());
			
	        markersShowModecheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

	        	   @Override
	        	   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	        	   {
	        		   globalContext.getApplicationState().setSavePreviousLocationMarkers(!isChecked);
	        		   
	        		   ArrayList<Contact> contacts = globalContext.getApplicationState().getContacts();
	        		   
	        		   for (int i = 0; i < contacts.size(); i++)
	        		   {
	        			   for (int j = 0; j < contacts.get(i).getMarkers().size()-1; j++) //Все предыдущие
	        			   {
	        				   contacts.get(i).getMarkers().get(j).setVisible(!globalContext.getApplicationState().isSavePreviousLocationMarkers());
	        			   }
	        		   }
	        	   }
	        });
	        
			break;
			
		case 1:
			
			rowView = layoutInflater.inflate(R.layout.list_item_options_location_update_interval, parent, false);
			
			final TextView timeTextView = (TextView)rowView.findViewById(R.id.time_text_view);
			long minutes = globalContext.getApplicationState().getLocationUpdateInterval()/1000/60;
			long hours = minutes/60;
			minutes -= 60*hours;
			timeTextView.setText(hours+"ч. "+minutes+"мин.");
			
			final TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker timePicker, int hour, int minute)
				{
					globalContext.getApplicationState().setLocationUpdateInterval(hour*3600000+minute*60000);
					timeTextView.setText(hour+"ч. "+minute+"мин.");
				}
	        	
	        };
	        
			rowView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0)
				{
					TimePickerDialog timepickerdialog = new TimePickerDialog(activityForDialog, mTimeSetListener, 0, 0, true);
			        timepickerdialog.setTitle("Установите временной интервал");
			        timepickerdialog.show();
				}});
			break;
			
		case 2:
			
			rowView = layoutInflater.inflate(R.layout.list_item_options_checkbox, parent, false);
			
			CheckBox checkBox2 = (CheckBox)rowView.findViewById(R.id.check_box);
			checkBox2.setText("Генерировать ложные координаты");
			checkBox2.setChecked(globalContext.getApplicationState().isFalsificaeGeoCoordinates());
			
	        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

	        	   @Override
	        	   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	        		   
	        		   globalContext.getApplicationState().setFalsificaeGeoCoordinates(isChecked);
	        	   }
	        });
			break;
			
		case 3:
			
			rowView = layoutInflater.inflate(R.layout.list_item_options_spinner, parent, false);
			
			Spinner spinner = (Spinner) rowView.findViewById(R.id.spinner1);
			TextView titleTextView = (TextView)rowView.findViewById(R.id.textView1);
			titleTextView.setText("Предпочитаемый режим передачи данных");
			
			String[] data = {"Wi-Fi", "Mobile"};
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(globalContext, android.R.layout.simple_spinner_item, data);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner.setAdapter(adapter);
	        
	        if(globalContext.getApplicationState().getPrefferedDataCommunication().equals(ApplicationState.PREFFERED_WIFI))
	        	spinner.setSelection(0);
	        else
	        	spinner.setSelection(1);
	        
	        spinner.setOnItemSelectedListener(new OnItemSelectedListener()
	        {
	            @Override
	            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	            {
	            	TextView selectedText = (TextView) parent.getChildAt(0);
	            	selectedText.setTextColor(Color.BLACK);
	            	   
	            	if(position == 0)
	            		globalContext.getApplicationState().setPrefferedDataCommunication(ApplicationState.PREFFERED_WIFI);
	            	else
	            		globalContext.getApplicationState().setPrefferedDataCommunication(ApplicationState.PREFFERED_MOBILE);
	            }
	            @Override
	            public void onNothingSelected(AdapterView<?> arg0)
	            {
	            }
	          });
			break;
			
		case 4:
			
			rowView = layoutInflater.inflate(R.layout.list_item_options_choose_location_service, parent, false);
			
			//RadioButton useGPSRadioButton = (RadioButton) rowView.findViewById(R.id.use_gps_radio_button);
			//RadioButton useNetmonitoringRadioButton = (RadioButton) rowView.findViewById(R.id.use_netmonitoring_radio_button);
			RadioGroup radioGroup = (RadioGroup) rowView.findViewById(R.id.radio_group);
			CheckBox alternativeGPSCheckBox = (CheckBox) rowView.findViewById(R.id.alternative_gps_check_box);
			CheckBox alternativeNetmonitoringCheckBox = (CheckBox) rowView.findViewById(R.id.alternative_netmonitoring_check_box);
			
			
			//Toast.makeText(activityForDialog, "alternative: "+alternative, Toast.LENGTH_SHORT).show();
			OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					globalContext.getApplicationState().setAlternativeLocationServise(isChecked);
				}};
				
			alternativeGPSCheckBox.setOnCheckedChangeListener(checkedChangeListener);
			alternativeNetmonitoringCheckBox.setOnCheckedChangeListener(checkedChangeListener);
			
			
			LocationServiceRadioGroupCheckedChangeListener locationServiceRadioGroupCheckedChangeListener = new LocationServiceRadioGroupCheckedChangeListener(globalContext, alternativeGPSCheckBox, alternativeNetmonitoringCheckBox);
			radioGroup.setOnCheckedChangeListener(locationServiceRadioGroupCheckedChangeListener);
			
			boolean alternative = globalContext.getApplicationState().isAlternativeLocationServise();
			if(globalContext.getApplicationState().getCurrentLocationService().equals(ApplicationState.LOCATION_SERVICE_NETMONITORING))
			{
				radioGroup.check(R.id.use_netmonitoring_radio_button);//((RadioButton)rg.getChildAt(2)).setChecked(true);
				alternativeNetmonitoringCheckBox.setChecked(alternative);
			}
			else
			{
				radioGroup.check(R.id.use_gps_radio_button);//((RadioButton)rg.getChildAt(0)).setChecked(true);
				alternativeGPSCheckBox.setChecked(alternative);
			}
			
			
			
			break;
			
		case 5:
			
			rowView = layoutInflater.inflate(R.layout.list_item_options_edit_text, parent, false);
			
			TextView gpsWaitingTimeTextView = (TextView)rowView.findViewById(R.id.textView);
			EditText gpsWaitingTimeEditText = (EditText)rowView.findViewById(R.id.editText);
			
			gpsWaitingTimeTextView.setText("Время ожидания GPS (в сек.)");
			gpsWaitingTimeEditText.setText(String.valueOf(globalContext.getApplicationState().getGpsSecondsWaitingTime()));
			gpsWaitingTimeEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			gpsWaitingTimeEditText.addTextChangedListener(new TextWatcher(){

				@Override
				public void afterTextChanged(Editable s) {
					globalContext.getApplicationState().setGpsSecondsWaitingTime(Long.parseLong(s.toString()));
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					
				}

				});
			
			break;
			
		case 6:
			
			rowView = layoutInflater.inflate(R.layout.list_item_options_checkbox, parent, false);
			
			CheckBox serviceCheckBox = (CheckBox)rowView.findViewById(R.id.check_box);
			serviceCheckBox.setText("Фоновый режим (После закрытия приложение будет получать и обрабатывать запросы)");
			serviceCheckBox.setChecked(globalContext.getApplicationState().isService());
			
			serviceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

	        	   @Override
	        	   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	        		   
	        		   globalContext.getApplicationState().setService(isChecked);
	        	   }
	        });
	        
			break;
		}

        rowView.setBackground(activityForDialog.getResources().getDrawable(R.drawable.listselector));
        return rowView;
    }

	@Override
	public int getCount()
	{
		return LIST_SIZE;
	}

	@Override
	public Object getItem(int arg0) {
		return null; //Если null возможно пункты выехавшие за экран потеряют своё состояние
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	public EditText getServerIPEditText() {
		return serverIPEditText;
	}
}