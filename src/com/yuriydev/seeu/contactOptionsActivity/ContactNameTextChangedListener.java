package com.yuriydev.seeu.contactOptionsActivity;

import com.yuriydev.seeu.R;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class ContactNameTextChangedListener implements TextWatcher
{
	private ContactOptionsActivity contextActivity;
	private EditText contactNameEditText;
	private Drawable originalDrawable;
	
	ContactNameTextChangedListener(ContactOptionsActivity contextActivity, EditText contactNameEditText)
	{
		this.contextActivity = contextActivity;
		this.contactNameEditText = contactNameEditText;
		this.originalDrawable = this.contactNameEditText.getBackground();
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
		
		if(contextActivity.isContactNameValid(s.toString()))
		{
			contactNameEditText.setBackground(originalDrawable);
		}
		else
		{
			contactNameEditText.setBackgroundColor(contextActivity.getResources().getColor(android.R.color.holo_red_light));
		}
	}
}