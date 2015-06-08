package com.yuriydev.seeu.contactOptionsActivity;

import com.yuriydev.seeu.R;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class ContactPhoneTextChangedListener implements TextWatcher
{
	private ContactOptionsActivity contextActivity;
	private EditText contactPhoneEditText;
	private Drawable originalDrawable;
	
	ContactPhoneTextChangedListener(ContactOptionsActivity contextActivity, EditText contactPhoneEditText)
	{
		this.contextActivity = contextActivity;
		this.contactPhoneEditText = contactPhoneEditText;
		this.originalDrawable = this.contactPhoneEditText.getBackground();
	}

	@Override
	public void afterTextChanged(Editable s)
	{
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		contextActivity.notifyAboutChanges();
		
		if(contextActivity.isContactPhoneValid(s.toString()))
		{
			contactPhoneEditText.setBackground(originalDrawable);
		}
		else
		{
			contactPhoneEditText.setBackgroundColor(contextActivity.getResources().getColor(android.R.color.holo_red_light));
		}
	}
}