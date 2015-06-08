package com.yuriydev.seeu.phoneBookActivity;

import java.util.ArrayList;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.Contact;
import com.yuriydev.seeu.SeeUGlobalContext;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneBookArrayAdapter extends BaseAdapter
{
	private SeeUGlobalContext globalContext;
	private ArrayList<Contact> phoneBook;
	private PhoneBookActivity contextActivity;
	private LayoutInflater layoutInflater;
	

    public PhoneBookArrayAdapter(SeeUGlobalContext globalContext, ArrayList<Contact> phoneBook, PhoneBookActivity contextActivity)
    {
        this.phoneBook = phoneBook;
        this.globalContext = globalContext;
        this.contextActivity = contextActivity;
        this.layoutInflater = (LayoutInflater) this.globalContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = layoutInflater.inflate(R.layout.list_item_contact, parent, false);
        
        TextView nameTextView = (TextView) rowView.findViewById(R.id.name_text_view);
        nameTextView.setText(phoneBook.get(position).getName());
        
        final CheckBox checkBox = (CheckBox)rowView.findViewById(R.id.check_box);
        checkBox.setTag(position);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

        	   @Override
        	   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
        		   if(isChecked)
        		   {
        			   contextActivity.checkIndexForPrepeared((Integer)buttonView.getTag());
        		   }
        		   else
        		   {
        			   contextActivity.uncheckIndexForPrepeared((Integer)buttonView.getTag());
        		   }
        	   }
        });
        
        ImageView thumbnail = (ImageView)rowView.findViewById(R.id.image);
        thumbnail.setImageURI(Uri.parse(phoneBook.get(position).getPhotoURI()));
        rowView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0)
			{
				if(checkBox.isChecked())
				{
					checkBox.setChecked(false);
				}
				else
				{
					checkBox.setChecked(true);
				}
			}});

        return rowView;
    }

	@Override
	public int getCount() {
		return phoneBook.size();
	}

	@Override
	public Object getItem(int arg0) {
		return phoneBook.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
}