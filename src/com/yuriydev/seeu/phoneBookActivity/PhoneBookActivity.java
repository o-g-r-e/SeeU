package com.yuriydev.seeu.phoneBookActivity;


import java.util.ArrayList;
import java.util.Calendar;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.Contact;
import com.yuriydev.seeu.SeeUGlobalContext;
import com.yuriydev.seeu.SeeUMarker;
import com.yuriydev.seeu.SeeUTimestamp;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class PhoneBookActivity extends Activity
{
	private SeeUGlobalContext globalContext;
	private ListView list;
	
	private ArrayList<Contact> phoneBook = null;
	private ArrayList<Integer> prepearedIndexes = new ArrayList<Integer>();
	
	public void addPrepearedContacts(ArrayList<Contact> contacts)
	{
		int size = prepearedIndexes.size();
		
		for (int i = 0; i < size; i++)
		{
			contacts.add(phoneBook.get(prepearedIndexes.get(i)));
		}
		
		clearPhoneBook();
		clearPrepearedIndexes();
	}
	
	public void clearPhoneBook()
	{
		phoneBook.clear();
		phoneBook.trimToSize();
	}
	
	public void clearPrepearedIndexes()
	{
		prepearedIndexes.clear();
		prepearedIndexes.trimToSize();
	}
	
	public void checkIndexForPrepeared(int index){
		prepearedIndexes.add(index);
	}
	
	public void uncheckIndexForPrepeared(int index)
	{
		for (int i = 0; i < prepearedIndexes.size(); i++)
		{
			if(prepearedIndexes.get(i) == index)
			{
				prepearedIndexes.remove(i);
				break;
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_book);
		
		globalContext = (SeeUGlobalContext)getApplicationContext();
		
		list = (ListView) findViewById(R.id.phone_book_list);
		
		findViewById(R.id.confirm_button).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0)
			{
				addPrepearedContacts(globalContext.getApplicationState().getContacts());
				
				Contact contact = globalContext.getApplicationState().getContacts().get(0);
				Calendar calendar = Calendar.getInstance();
				String snippet = calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+(calendar.get(Calendar.YEAR)-2000)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
				double lat = 55.0;
				double lng = 44.0;
				getResources().getDrawable(R.drawable.ya_m);

				for (int i = 0; i < 5; i++) {
					SeeUMarker marker = new SeeUMarker(new SeeUTimestamp(Calendar.getInstance()), lat, lng, 0, contact.getName(), snippet+" шир: "+lat+" дол: "+lng, R.drawable.ya_m);
					contact.addMarker(marker);
					lat += 0.1;
				}
				
				finish();
			}
		});
		
		findViewById(R.id.cancel_button).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0)
			{
				clearPhoneBook();
				clearPrepearedIndexes();
        	    finish();
			}
		});
		
		queryPhoneBook();
        
		PhoneBookArrayAdapter adapter = new PhoneBookArrayAdapter(globalContext, phoneBook, this);
        
        list.setAdapter(adapter);
	}
	
	public void queryPhoneBook()
	{
		Cursor contactCursor = this.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, 
                                                                  		new String[] {Contacts._ID, Contacts.DISPLAY_NAME, Contacts.HAS_PHONE_NUMBER}, 
                                                                  		null, 
                                                                  		null, 
                                                                  		Contacts.DISPLAY_NAME+" ASC"); 
		
		phoneBook = new ArrayList<Contact>(contactCursor.getCount());
		
		while (contactCursor.moveToNext())
		{
			String contactId = contactCursor.getString(0);
			String hasPhone = contactCursor.getString(2);
			
			if (Integer.parseInt(hasPhone)>0)
			{
				Cursor phoneCursor = this.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
				                     new String[] {Phone.NORMALIZED_NUMBER, Phone.PHOTO_THUMBNAIL_URI}, 
				                     ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId +" AND "+ Phone.TYPE + " = " +Phone.TYPE_MOBILE, null, null);
				
				/*Cursor photoCursor = getContentResolver().query( ContactsContract.Data.CONTENT_URI, 
				new String[] {ContactsContract.CommonDataKinds.Photo.PHOTO}, 
				ContactsContract.CommonDataKinds.Photo.CONTACT_ID +" = "+ contactId, null, null);*/
				
				/*Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, Long.parseLong(contactId));
				Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
				Cursor cursor = getContentResolver().query(photoUri, new String[] {Contacts.Photo.PHOTO}, null, null, null);
				
				ByteArrayInputStream bais;
				int count = 0;
				try {
				if (cursor.moveToFirst()) {
				byte[] data = cursor.getBlob(0);
				if (data != null) {
				bais = new ByteArrayInputStream(data);
				count++;
				}
				}
				} finally {
				cursor.close();
				}*/
				
				
				while (phoneCursor.moveToNext())
				{
					if(!phoneCursor.isNull(0))
					{
						Contact contact = new Contact();
						contact.setName(contactCursor.getString(1));
						//contact.setNumber(phoneCursor.getString(0));
						if(!phoneCursor.isNull(1))
							contact.setPhotoURI(phoneCursor.getString(1));
						else
							contact.setPhotoURI(Uri.parse("android.resource://com.yuriydev.seeu/"+ R.drawable.ic_menu_camera).toString());
						phoneBook.add(contact);
					}
				}
				
				/*Bitmap thumbnail = null;
				if (photoCursor.moveToFirst())
				{
				final byte[] thumbnailBytes = photoCursor.getBlob(0);
				
				if (thumbnailBytes != null) {
				thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
				}
				}*/
				phoneCursor.close();
			}
		}
		contactCursor.close();
	}
}