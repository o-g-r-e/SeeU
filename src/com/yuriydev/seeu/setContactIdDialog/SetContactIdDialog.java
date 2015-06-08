package com.yuriydev.seeu.setContactIdDialog;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.Contact;
import com.yuriydev.seeu.contactOptionsActivity.ContactMarkersListAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class SetContactIdDialog extends Dialog
{
	private EditText setContactIdEditText;
	private Button confirmButton;
	private Contact contact;
	private Activity contextActivity;
	
	public SetContactIdDialog(Activity contextActivity, Contact contact)
	{
		super(contextActivity);
		this.contact = contact;
		this.contextActivity = contextActivity;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_setter_layout);
		setTitle("¬ведите ID контакта");
		setContactIdEditText = (EditText) findViewById(R.id.contact_id_edit_text);
		confirmButton = (Button) findViewById(R.id.confirm_button);
		InputMethodManager imm = (InputMethodManager)contextActivity.getSystemService(Service.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		
		confirmButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				contact.setId(setContactIdEditText.getText().toString());
				SetContactIdDialog.this.cancel();
				
				InputMethodManager imm = (InputMethodManager)contextActivity.getSystemService(Service.INPUT_METHOD_SERVICE);
				//imm.hideSoftInputFromWindow(setContactIdEditText.getWindowToken(), 0);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			}
		});
		
		/*setContactIdEditText.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				contact.setId(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}

			});*/
	}
}