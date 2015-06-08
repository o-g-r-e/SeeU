package com.yuriydev.seeu.whoDialog;

import java.util.ArrayList;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.Contact;
import com.yuriydev.seeu.SeeUGlobalContext;

import android.app.Dialog;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;
import android.os.Bundle;

public class WhoDialog extends Dialog
{
	private ListView idsContactsListView;
	private WhoDialogListAdapter adapter;
	private ArrayList<Contact> idsContacts;
	
	public WhoDialog(Context contextActivity, SeeUGlobalContext globalContext/*, WhoDialogAdapter adapter*/)
	{
		super(contextActivity);
		idsContacts = globalContext.getApplicationState().getValidContacts();
		this.adapter = new WhoDialogListAdapter(globalContext, idsContacts, this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dialog_who);
	    
	    //requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setTitle("Укажите контакт для запроса:");
	    
	    idsContactsListView = (ListView) findViewById(R.id.ids_contacts_list_view);
	    TextView messageTextView = (TextView) findViewById(R.id.message_text_view);
	    if(idsContacts.size() > 0)
	    {
	    	ViewGroup messageTextViewGroup = (ViewGroup) messageTextView.getParent();
	    	messageTextViewGroup.removeView(messageTextView);
	    }
	    idsContactsListView.setAdapter(adapter);
	}
}