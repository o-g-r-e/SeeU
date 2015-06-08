package com.yuriydev.seeu.whoDialog;

import java.util.ArrayList;
import java.util.Calendar;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.Contact;
import com.yuriydev.seeu.MessageType;
import com.yuriydev.seeu.SeeUEvent;
import com.yuriydev.seeu.SeeUGlobalContext;
import com.yuriydev.seeu.SeeUMessage;
import com.yuriydev.seeu.SeeUTimestamp;

import android.app.Dialog;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WhoDialogListAdapter extends BaseAdapter
{
	private SeeUGlobalContext globalContext;
	private LayoutInflater layoutInflater;
	private ArrayList<Contact> validContactas;
	private Dialog dialogContext;
	
	WhoDialogListAdapter(SeeUGlobalContext globalContext, ArrayList<Contact> validContactas, Dialog dialogContext)
	{
		this.globalContext = globalContext;
	    this.validContactas = validContactas;
	    this.dialogContext = dialogContext;
	    this.layoutInflater = (LayoutInflater) this.globalContext.getSystemService(this.globalContext.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return validContactas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return validContactas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View rowView = layoutInflater.inflate(R.layout.list_item_contact, parent, false);
		
		ImageView contactIcon = (ImageView) rowView.findViewById(R.id.image);
        contactIcon.setImageURI(Uri.parse(validContactas.get(position).getPhotoURI()));
        
        TextView nameTextView = (TextView) rowView.findViewById(R.id.name_text_view);
        nameTextView.setText(validContactas.get(position).getName());
		
		rowView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
					
				int errorCode = globalContext.sendMessage(new SeeUMessage(MessageType.LOCATION_REQUEST, globalContext.getApplicationState().getClientId(), validContactas.get(position).getId()));
				switch (errorCode)
				{
				case -2:
					Toast.makeText(globalContext.getMainActivity(), "Ошибка ID", Toast.LENGTH_SHORT).show();
					break;
					
				case -1:
					Toast.makeText(globalContext.getMainActivity(), "Соединение с сервером не установлено. Повторите попытку позднее.", Toast.LENGTH_SHORT).show();
					break;
				}
				globalContext.getApplicationState().addEvent(new SeeUEvent(new SeeUTimestamp(Calendar.getInstance()), SeeUEvent.OUTGOING_MESSAGE, MessageType.LOCATION_REQUEST, "Отправлен запрос местоположения к "+validContactas.get(position).getName()));
				dialogContext.dismiss();
			}});
		
		return rowView;
	}
}