package com.yuriydev.seeu.contactOptionsActivity;

import com.yuriydev.seeu.R;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class ContactIdTextChangedListener implements TextWatcher
{
	private ContactOptionsActivity contextActivity;
	private EditText contactIdEditText;
	private Drawable originalDrawable;
	
	ContactIdTextChangedListener(ContactOptionsActivity contextActivity, EditText contactIdEditText)
	{
		this.contextActivity = contextActivity;
		this.contactIdEditText = contactIdEditText;
		this.originalDrawable = this.contactIdEditText.getBackground();
	}
	
	@Override
	public void afterTextChanged(Editable arg0)
	{
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
	{
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		contextActivity.notifyAboutChanges();
		
		if(contextActivity.isContactIdValid(s.toString()))
		{
			contactIdEditText.setBackground(originalDrawable);
		}
		else
		{
			contactIdEditText.setBackgroundColor(contextActivity.getResources().getColor(android.R.color.holo_red_light));
		}
	}
}