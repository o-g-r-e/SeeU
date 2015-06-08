package com.yuriydev.seeu.fragments;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.MessageType;
import com.yuriydev.seeu.SeeUEvent;
import com.yuriydev.seeu.SeeUGlobalContext;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.Toast;

public class EventLogFragment extends Fragment implements Observer
{
	private SeeUGlobalContext globalContext;
	private ListView eventsListView;
	private EditText eventLogEditText;
	
	
	private void displayEventLogList(String[] strings)
	{
		///EventLogListAdapter adapter = new EventLogListAdapter(globalContext, globalContext.getApplicationState().getEventLog());
		//String[] strings =  getEventsStringsForListView(globalContext.getApplicationState().getEventLog());
		ArrayAdapter<String> adapter =  new ArrayAdapter<String>(globalContext, R.layout.list_item_event_log, strings);
		eventsListView.setAdapter(adapter);
	}
	
	private void displayEventLogText(ArrayList<SeeUEvent> eventLog)
	{
		eventLogEditText.setText(getEventsStringForEditText(eventLog));
	}
	
	private String getEventsStringForEditText(ArrayList<SeeUEvent> eventLog)
	{
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < eventLog.size(); i++)
		{
			sb.append("[");
			sb.append(eventLog.get(i).getTimestamp().getDateTime());
			sb.append("]: ");
			sb.append(eventLog.get(i).getEventMessage());
			sb.append("\n");
		}
		
		return sb.substring(0);
	}
	
	private String[] getEventsStringsForListView(ArrayList<SeeUEvent> eventLog)
	{
		
		ArrayList<String> stringsForListView = new ArrayList<String>();
		//String[] stringsForListView = new String[eventLog.size()];
		
		for (int i = 0; i < eventLog.size(); i++)
		{
			SeeUEvent event = eventLog.get(i);
			if((event.getEventType() == SeeUEvent.INCOMING_MESSAGE)||(event.getEventType() == SeeUEvent.OUTGOING_MESSAGE))
			{
				if((event.getEventMessageType() == MessageType.LOCATION_REQUEST)||(event.getEventMessageType() == MessageType.LOCATION_RESPONSE)||(event.getEventMessageType() == MessageType.DENIED))
				{
					StringBuilder sb = new StringBuilder();
					sb.append(eventLog.get(i).getTimestamp().getDateTime());
					sb.append("\n");
					sb.append(eventLog.get(i).getEventMessage());
					sb.append("\n");
					
					stringsForListView.add(sb.substring(0));
					//stringsForListView[i] = sb.substring(0);
				}
			}
		}
		
		return stringsForListView.toArray(new String[stringsForListView.size()]);
	}
	
	@Override
	  public void onAttach(Activity activity) {
	    super.onAttach(activity);
	  }
	  
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	    globalContext = (SeeUGlobalContext) getActivity().getApplicationContext();
	    globalContext.getGlobalObservableNode().addObserver(this);
	  }
	  
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	  {
		View view = inflater.inflate(R.layout.fragment_event_log, null);
		eventLogEditText  = (EditText) view.findViewById(R.id.event_log_edit_text);
		eventsListView = (ListView) view.findViewById(R.id.list_view);
		
		LinearLayout ifEmpty = (LinearLayout) view.findViewById(R.id.if_empty);
		eventsListView.setEmptyView(ifEmpty);
		
		eventLogEditText.setInputType(InputType.TYPE_NULL);
		//eventLogEditText.setVerticalScrollBarEnabled(true);
		eventLogEditText.setSingleLine(false);
		eventLogEditText.setLines(40);
		Scroller s = new Scroller(globalContext);
		
		eventLogEditText.setScroller(s); 
		//eventLogEditText.setMaxLines(1); 
		//eventLogEditText.setVerticalScrollBarEnabled(true); 
		eventLogEditText.setMovementMethod(new ScrollingMovementMethod()); 
		
		if(globalContext.getApplicationState().isEventLogListMode())
		{
			ViewGroup viewGroup = (ViewGroup)eventLogEditText.getParent();
			viewGroup.removeView(eventLogEditText);
	  	}
		else
		{
			ViewGroup viewGroup = (ViewGroup)eventsListView.getParent();
			viewGroup.removeView(eventsListView);
		}
		
		//globalContext.getMainActivity().displayToast("eventsListView="+eventsListView+" "+"eventLogEditText="+eventLogEditText, Toast.LENGTH_LONG);
		//displayEventList();
		return view;
	  }
	  
	  public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	  }
	  
	  public void onStart() {
	    super.onStart();
	  }
	  
	  public void onResume() //Обновлять EditText нужно именно здесь, потомучто в onCreateView глючит, это ошибка Android связана с фрагментами (Fragment)
	  {
		  if(globalContext.getApplicationState().isEventLogListMode())
			  displayEventLogList(getEventsStringsForListView(globalContext.getApplicationState().getEventLog()));
		  else
			  displayEventLogText(globalContext.getApplicationState().getEventLog());
		  super.onResume();
	  }
	  
	  public void onPause() {
	    super.onPause();
	  }
	  
	  public void onStop() {
	    super.onStop();
	  }
	  
	  public void onDestroyView() {
	    super.onDestroyView();
	  }
	  
	  public void onDestroy()
	  {
		  globalContext.getGlobalObservableNode().deleteObserver(this);
		  super.onDestroy();
	  }
	  
	  public void onDetach() {
	    super.onDetach();
	  }
	  
	  @Override
	  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	  {
		  menu.clear();
		  inflater.inflate(R.menu.events, menu);
		  super.onCreateOptionsMenu(menu, inflater);
	  }
	  
	  @Override
		public boolean onOptionsItemSelected(MenuItem item)
		  {
			switch (item.getItemId())
			{
				
			/*case R.id.replace:
				
				if(globalContext.getApplicationState().isEventLogListMode())
				{
					ViewGroup viewGroup = (ViewGroup)eventsListView.getParent();
					viewGroup.removeView(eventsListView);
					
					viewGroup.addView(eventLogEditText);
					displayEventLogText(globalContext.getApplicationState().getEventLog());
					
					globalContext.getApplicationState().setEventLogListMode(false);
				}
				else
				{
					ViewGroup viewGroup = (ViewGroup)eventLogEditText.getParent();
					viewGroup.removeView(eventLogEditText);
						
					viewGroup.addView(eventsListView);
					displayEventLogList(getEventsStringsForListView(globalContext.getApplicationState().getEventLog()));
						
					globalContext.getApplicationState().setEventLogListMode(true);
				}
				break;*/
				
			case R.id.clear_event_log:
				
				globalContext.getApplicationState().clearEventLog();
				
				if(globalContext.getApplicationState().isEventLogListMode())
					displayEventLogList(getEventsStringsForListView(globalContext.getApplicationState().getEventLog()));
		    	else
		    		displayEventLogText(globalContext.getApplicationState().getEventLog());
				break;

			default:
				break;
			}
			return super.onOptionsItemSelected(item);
		}

	@Override
	public void update(Observable observable, Object data)
	{
		globalContext.getMainActivity().runOnUiThread(new Runnable()
		{
		    public void run()
		    {
		    	if(globalContext.getApplicationState().isEventLogListMode())
					  displayEventLogList(getEventsStringsForListView(globalContext.getApplicationState().getEventLog()));
		    	else
					  displayEventLogText(globalContext.getApplicationState().getEventLog());
		    }
		});
	}
}