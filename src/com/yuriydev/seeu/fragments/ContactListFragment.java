package com.yuriydev.seeu.fragments;

import java.util.ArrayList;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.Contact;
import com.yuriydev.seeu.SeeUGlobalContext;
import com.yuriydev.seeu.dialogSetNewContactName.DialogSetNewContactName;
import com.yuriydev.seeu.phoneBookActivity.PhoneBookActivity;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;


public class ContactListFragment extends Fragment
{
	private SeeUGlobalContext globalContext;
	private ListView contactListView;
	private ViewGroup ifEmpty;
	private ContactListAdapter adapter;
	
	public void callAddContactsActivity()
	{
		startActivity(new Intent(globalContext, PhoneBookActivity.class));
	}
	
	public void displayCotactList()
	{
		contactListView.setAdapter(adapter);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.fragment_contact_list);
		setHasOptionsMenu(true);
		
		globalContext = (SeeUGlobalContext) getActivity().getApplicationContext();
		adapter = new ContactListAdapter(globalContext, globalContext.getApplicationState().getContacts());
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		displayCotactList();
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_contact_list, null);
		contactListView  = (ListView) view.findViewById(R.id.contact_list);
		ifEmpty = (LinearLayout) view.findViewById(R.id.if_empty);
		contactListView.setEmptyView(ifEmpty);
		ifEmpty.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0)
			{
				new DialogSetNewContactName(ContactListFragment.this.getActivity(), ContactListFragment.this).show();
				//globalContext.getMainActivity().startActivity(new Intent(globalContext, PhoneBookActivity.class));
			
			}
		});
			
		displayCotactList();
		
		return view;
	}
	  
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	  
	public void onResume()
	{
	    super.onResume();
	}
	  
	public void onPause()
	{
	    super.onPause();
	}
	  
	public void onStop()
	{
	    super.onStop();
	}
	  
	public void onDestroyView()
	{
	    super.onDestroyView();
	}
	  
	public void onDestroy()
	{
	    super.onDestroy();
	}
	  
	public void onDetach()
	{
	    super.onDetach();
	}
	  
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		menu.clear();
		inflater.inflate(R.menu.contacts, menu);
	}
	  
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.add_contacts:
			new DialogSetNewContactName(getActivity(), this).show();
			//startActivity(new Intent(globalContext, PhoneBookActivity.class));
			break;
			
		case R.id.clear_contacts:
    		globalContext.getApplicationState().clearContacts();
    		displayCotactList();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public ContactListAdapter getAdapter() {
		return adapter;
	}
}