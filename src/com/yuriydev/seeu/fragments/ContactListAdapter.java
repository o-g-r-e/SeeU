package com.yuriydev.seeu.fragments;

import java.util.ArrayList;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.Contact;
import com.yuriydev.seeu.SeeUGlobalContext;
import com.yuriydev.seeu.contactOptionsActivity.ContactOptionsActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactListAdapter extends BaseAdapter
{
	private SeeUGlobalContext globalContext;
	private LayoutInflater layoutInflater;
	private ArrayList<Contact> contacts;
	
	public ContactListAdapter(SeeUGlobalContext globalContext, ArrayList<Contact> contacts)
	{
        this.globalContext = globalContext;
        this.contacts = contacts;
        this.layoutInflater = (LayoutInflater) globalContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
	
	@Override
    public View getView(final int position, View convertView, ViewGroup parent)
	{
        final Contact contact = contacts.get(position);
        View rowView = null;
        rowView = layoutInflater.inflate(R.layout.list_item_contact, parent, false);
	        
	    ImageView contactIcon = (ImageView) rowView.findViewById(R.id.image);
	    contactIcon.setImageURI(Uri.parse(contact.getPhotoURI()));
	        
	    TextView nameTextView = (TextView) rowView.findViewById(R.id.name_text_view);
	    nameTextView.setText(contact.getName());
	    
	    rowView.setOnClickListener(new OnClickListener(){
	    	@Override
			public void onClick(View arg0)
			{
	    		Intent intent = new Intent(globalContext, ContactOptionsActivity.class);
				intent.putExtra("contact", position);
				globalContext.getMainActivity().startActivity(intent);
			}
	    });
        
        return rowView;
    }

	@Override
	public int getCount() {
		return contacts.size();
	}

	@Override
	public Object getItem(int arg0) {
		return contacts.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
}