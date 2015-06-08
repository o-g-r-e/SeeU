package com.yuriydev.seeu.dialogSetNewContactName;

import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.Contact;
import com.yuriydev.seeu.SeeUGlobalContext;
import com.yuriydev.seeu.fragments.ContactListFragment;
import com.yuriydev.seeu.setContactIdDialog.SetContactIdDialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DialogSetNewContactName extends Dialog
{
	private EditText conatctNameEditText;
	private Button confirmButton;
	private Activity contextActivity;
	private ContactListFragment contextContactListFragment;
	private Drawable originalDrawable;
	
	public DialogSetNewContactName(Activity contextActivity, ContactListFragment contextContactListFragment)
	{
		super(contextActivity);
		this.contextActivity = contextActivity;
		this.contextContactListFragment = contextContactListFragment;
	}
	
	private boolean isContactNameValid(String name)
	{
		Pattern p = Pattern.compile("^[a-zA-Zа-яА-Я\\s]{2,40}$");
        Matcher m = p.matcher(name);
        return m.matches();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_setter_layout);
		setTitle("Введите имя нового контакта");
		conatctNameEditText = (EditText) findViewById(R.id.edit_text);
		confirmButton = (Button) findViewById(R.id.confirm_button);
		
		originalDrawable = conatctNameEditText.getBackground();
		
		final InputMethodManager imm = (InputMethodManager)contextActivity.getSystemService(Service.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		
		confirmButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String name = conatctNameEditText.getText().toString();
				if(isContactNameValid(name))
				{
					((SeeUGlobalContext)contextActivity.getApplicationContext()).getApplicationState().getContacts().add(new Contact(name, "android.resource://com.yuriydev.seeu/"+ R.drawable.ic_menu_camera, false));
					dismiss();
					imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
					
					contextContactListFragment.getAdapter().notifyDataSetChanged();
				}
				else
				{
					Toast.makeText(contextActivity, "Некорректное имя", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		conatctNameEditText.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(isContactNameValid(s.toString()))
				{
					conatctNameEditText.setBackground(originalDrawable);
				}
				else
				{
					conatctNameEditText.setBackgroundColor(contextActivity.getResources().getColor(android.R.color.holo_red_light));
				}
			}});
	}
}