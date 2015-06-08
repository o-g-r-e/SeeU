package com.yuriydev.seeu.contactOptionsActivity;

import java.util.ArrayList;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.Contact;
import com.yuriydev.seeu.MessageType;
import com.yuriydev.seeu.SeeUGlobalContext;
import com.yuriydev.seeu.SeeUMarker;
import com.yuriydev.seeu.SeeUMessage;
import com.yuriydev.seeu.InfoActivity.InfoActivity;
import com.yuriydev.seeu.setContactIdDialog.SetContactIdDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ContactMarkersListAdapter extends BaseAdapter
{
	private SeeUGlobalContext globalContext;
	private ArrayList<SeeUMarker> markers;
	private LayoutInflater layoutInflater;
	//private Contact contact;
	private ContactOptionsActivity activityContext;
	
	ContactMarkersListAdapter(SeeUGlobalContext globalContext, /*Contact contact,*/ArrayList<SeeUMarker> markers, ContactOptionsActivity activityContext)
	{
		this.globalContext = globalContext;
		this.activityContext = activityContext;
		
		this.markers = markers;
		//this.markers.add(new SeeUMarker(null, 0.0, 0.0, 0, "", "", 0));
		//this.contact = contact;
		//this.markers.addAll(this.contact.getMarkers());
		
		this.layoutInflater = (LayoutInflater) globalContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setMarkers(ArrayList<SeeUMarker> markers) {
		this.markers = markers;
	}
	
	@Override
	public int getCount() {
		return markers.size();
	}

	@Override
	public Object getItem(int arg0) {
		return markers.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View rowView = null;
		
		//if(position == 0)
		//{
			//rowView = layoutInflater.inflate(R.layout.list_item_contact_options, parent, false);
			
			
		//}
		//else
		//{
			rowView = layoutInflater.inflate(R.layout.list_item_contact_marker, parent, false);
			rowView.setBackground(activityContext.getResources().getDrawable(R.drawable.listselector));
			TextView dateTimeTextView = (TextView) rowView.findViewById(R.id.date_time_text);
			dateTimeTextView.setText(markers.get(position).getTimestamp().getDateTime());
			
			rowView.setOnClickListener(new OnClickListener(){
				
				@Override
				public void onClick(View arg0)
				{
					
					globalContext.getApplicationState().setCameraPosition(markers.get(position).getLatitude(), markers.get(position).getLongitude(), 10, 0);
					globalContext.getApplicationState().setCurrentTab(0);
					globalContext.saveAppStateToFile();
					activityContext.finish();
					//globalContext.getMainActivity().recreate();
				}});
		//}
		
		return rowView;
	}
}