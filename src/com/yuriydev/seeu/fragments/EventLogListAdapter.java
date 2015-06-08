package com.yuriydev.seeu.fragments;

import java.util.ArrayList;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.SeeUEvent;
import com.yuriydev.seeu.SeeUGlobalContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventLogListAdapter extends BaseAdapter
{
	private ArrayList<SeeUEvent> events;
	private SeeUGlobalContext globalContext;
	private LayoutInflater layoutInflater;
	
	EventLogListAdapter(SeeUGlobalContext globalContext, ArrayList<SeeUEvent> events)
	{
		this.globalContext = globalContext;
	    this.events = events;
	    this.layoutInflater = (LayoutInflater) globalContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public Object getItem(int arg0) {
		return events.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View rowView = layoutInflater.inflate(R.layout.list_item_event_log, parent, false);
		
		/*SeeUEvent event = events.get(position);
        
		TextView dateTimeTextView = (TextView)rowView.findViewById(R.id.date_time_text);
		dateTimeTextView.setText(event.getTimestamp().getDateTime());
		
		TextView eventTextView = (TextView)rowView.findViewById(R.id.event_text);
		eventTextView.setText(event.getEventMessage());
		
		TextView responseTextView = (TextView)rowView.findViewById(R.id.response_text);
		responseTextView.setText(event.getResponseString());*/
		
		return rowView;
	}
}