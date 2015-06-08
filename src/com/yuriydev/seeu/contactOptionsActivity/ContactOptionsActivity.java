package com.yuriydev.seeu.contactOptionsActivity;

import java.util.Observable;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yuriydev.seeu.R;
import com.yuriydev.seeu.Contact;
import com.yuriydev.seeu.MessageType;
import com.yuriydev.seeu.SeeUGlobalContext;
import com.yuriydev.seeu.SeeUMarker;
import com.yuriydev.seeu.SeeUMessage;
import com.yuriydev.seeu.InfoActivity.InfoActivity;
import com.yuriydev.seeu.optionsActivity.OptionsActivity;
import com.yuriydev.seeu.setContactIdDialog.SetContactIdDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ContactOptionsActivity extends Activity implements Observer
{
	private ListView markersListView;
	private SeeUGlobalContext globalContext;
	private boolean optionsChanged = false;
	private ImageView contactThumbnailImageView;
	private EditText contactNameEditText;
	private EditText contactIdEditText;
	private Switch contactWatcherOptionSwitch;
	private Button locationRequestButton;
	private Button clearMarkersButton;
	private MenuItem saveOptionsButton;
	private int contactIndex;
	private ContactNameTextChangedListener contactNameTextChangedListener;
	private ContactPhoneTextChangedListener contactPhoneTextChangedListener;
	private ContactIdTextChangedListener contactIdTextChangedListener;
	private Contact contact;
	
	private Uri thumbnailUri;
	private String name;
	private String id;
	private boolean wather;
	
	private Uri baseThumbnailUri;
	private String baseName;
	private String baseId;
	private boolean baseWather;
	
	ContactMarkersListAdapter markersListAdapter;
	
	private void setData(String name, String id, Uri thumbnailUri, boolean wather)
	{
		this.name = name;
		this.id = id;
		this.thumbnailUri = thumbnailUri;
		this.wather = wather; 
	}
	
	private void setBaseData(String name, String id, Uri thumbnailUri, boolean wather)
	{
		baseName = name;
		baseId = id;
		baseThumbnailUri = thumbnailUri;
		baseWather = wather; 
	}
	
	public boolean isContactNameValid(String name)
	{
		Pattern p = Pattern.compile("^[a-zA-Zа-яА-Я\\s]{2,40}$");
        Matcher m = p.matcher(name);
        return m.matches();
	}
	
	public boolean isContactPhoneValid(String phone)
	{
		Pattern p = Pattern.compile("^[0-9]{0,11}$");
        Matcher m = p.matcher(phone);
        return m.matches();
	}
	
	public boolean isContactIdValid(String id)
	{
		Pattern p = Pattern.compile("^[0-9a-zA-Z]{0,8}$");
        Matcher m = p.matcher(id);
        return m.matches();
	}
	
	private void saveOptionsToContact(String name, String id, boolean isWatcher, Uri thumbnailUri)
	{
		contact.setName(name);
		contact.setId(id);
		contact.setWatching(isWatcher);
		contact.setPhotoURI(thumbnailUri.getScheme()+"://"+thumbnailUri.getHost()+thumbnailUri.getPath());
	}
	
	private boolean isDatasValidate()
	{
		if((isContactNameValid(name))&&(isContactIdValid(id)))
		{
			return true;
		}
		
		return false;
	}
	
	public void notifyAboutChanges()
	{
		boolean equalsBase = isDatasEqualsBase();
		optionsChanged = !equalsBase;
		if(saveOptionsButton != null) saveOptionsButton.setVisible(!equalsBase);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
        if (resultCode == RESULT_OK)
        {
            if (requestCode == 1)
            {
            	thumbnailUri = data.getData();
                contactThumbnailImageView.setImageURI(thumbnailUri);
                notifyAboutChanges();
            }
        }
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_options);
		
		globalContext = (SeeUGlobalContext)getApplicationContext();
		
		globalContext.getGlobalObservableNode().addObserver(this);
		
		contactIndex = getIntent().getIntExtra("contact", -1);
		
		contact = globalContext.getApplicationState().getContacts().get(contactIndex);
		
		setTitle("Настройки: "+contact.getName());
		
		markersListView = (ListView) findViewById(R.id.contact_marker_list);
		contactThumbnailImageView = (ImageView) findViewById(R.id.image);
		contactNameEditText = (EditText) findViewById(R.id.contact_name_edit_text);
		contactIdEditText = (EditText) findViewById(R.id.contact_id_edit_text);
		contactWatcherOptionSwitch = (Switch) findViewById(R.id.is_watcher_switch);
		locationRequestButton = (Button) findViewById(R.id.location_request_button);
		clearMarkersButton = (Button) findViewById(R.id.clear_markers_button);
		
		String uriPath = contact.getPhotoURI();
		
		contactThumbnailImageView.setImageURI(Uri.parse(contact.getPhotoURI()));
		
		contactNameEditText.setText(contact.getName());
		contactIdEditText.setText(contact.getId());
		contactWatcherOptionSwitch.setChecked(contact.isWatching());
		
		contactNameTextChangedListener = new ContactNameTextChangedListener(this, contactNameEditText);
		contactIdTextChangedListener = new ContactIdTextChangedListener(this, contactIdEditText);
		
		contactNameEditText.addTextChangedListener(contactNameTextChangedListener);
		contactIdEditText.addTextChangedListener(contactIdTextChangedListener);
		
		/*if(contact.getMarkers().size() == 0)
		{
			ViewGroup vg = (ViewGroup) clearMarkersButton.getParent();
			vg.removeView(clearMarkersButton);
		}*/
		
		contactThumbnailImageView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
			}});
		
		/*if(!isContactIdValid(contactIdEditText.getText().toString()))
		{
			contactIdEditText.setBackgroundColor(globalContext.getResources().getColor(android.R.color.holo_red_light));
		}
		
		if(!isContactNameValid(contactNameEditText.getText().toString()))
		{
			contactNameEditText.setBackgroundColor(globalContext.getResources().getColor(android.R.color.holo_red_light));
		}
		
		if(!isContactPhoneValid(contactPhoneEditText.getText().toString()))
		{
			contactPhoneEditText.setBackgroundColor(globalContext.getResources().getColor(R.color.red));
		}*/
		
		locationRequestButton.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v)
        	{
        		if(optionsChanged)
        		{
        			Toast.makeText(ContactOptionsActivity.this, "Сохраните настройки", Toast.LENGTH_SHORT).show();
        		}
        		else
    			{
	        		if(contact.isValid())
	        		{
			        	int errorCode = globalContext.sendMessage(new SeeUMessage(MessageType.LOCATION_REQUEST, globalContext.getApplicationState().getClientId(), contact.getId()));
			        	switch (errorCode)
						{
						case -2:
							Toast.makeText(globalContext.getMainActivity(), "Ошибка ID", Toast.LENGTH_SHORT).show();
							break;
							
						case -1:
							Toast.makeText(globalContext.getMainActivity(), "Соединение с сервером не установлено. Повторите попытку позднее.", Toast.LENGTH_SHORT).show();
							break;
						}
	        		}
	        		else
	        		{
	        			Toast.makeText(ContactOptionsActivity.this, "Отсутствует ID контакта", Toast.LENGTH_SHORT).show();
	        		}
    			}
        	}
        });
		
		clearMarkersButton.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v)
        	{
        	    if(contact.getMarkers().size() > 1)
        	    {
        	    	contact.removeMarkers(0, contact.getMarkers().size() - 1);
        	    	
        	    	/*for (int i = contact.getMarkers().size()-1; i > 1; i--)
        	    	{
        	    		contact.getMarkers().remove(i);
					}*/
        	    }
        	    else
        	    {
        	    	contact.removeMarkers(0, contact.getMarkers().size());
        	    	//contact.getMarkers().remove(0);
        	    	
        	    	//ViewGroup vg = (ViewGroup) v.getParent();
        			//vg.removeView(v);
        	    }
        	    
        	    markersListAdapter.notifyDataSetChanged();
        	}
        });
		
		contactWatcherOptionSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				notifyAboutChanges();
			}});
		
		markersListAdapter = new ContactMarkersListAdapter(globalContext, contact.getMarkers(), this);
		markersListView.setAdapter(markersListAdapter);
		
		TextView ifEmpty = (TextView) findViewById(R.id.empty_markers_text);
		markersListView.setEmptyView(ifEmpty);
		
		thumbnailUri = baseThumbnailUri = Uri.parse(contact.getPhotoURI());
		name         = baseName         = contact.getName();
		id           = baseId           = contact.getId();
		wather       = baseWather       = contact.isWatching();
	}
	
	private boolean isDatasEqualsBase()
	{
		setData(contactNameEditText.getText().toString(), contactIdEditText.getText().toString(), thumbnailUri, contactWatcherOptionSwitch.isChecked());
		
		if(name.equals(baseName) && id.equals(baseId))
		{
			if((wather == baseWather) && thumbnailUri.getPath().equals(baseThumbnailUri.getPath()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void onBackPressed()
	{
		// super.onBackPressed();
		if(optionsChanged && isDatasValidate())
		{
			AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
			quitDialog.setTitle("Сохранить измененя ?");
	
			quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveOptionsToContact(name, id, contactWatcherOptionSwitch.isChecked(), thumbnailUri);
					globalContext.saveAppStateToFile();
					finish();
				}
			});
	
			quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
	
			quitDialog.show();
		}
		else
		{
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		globalContext.getGlobalObservableNode().deleteObserver(this);
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		saveOptionsButton = menu.add(0, 0, 0, "Сохр.");
		saveOptionsButton.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
		notifyAboutChanges();
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case 0:
			if(isDatasValidate())
			{
				saveOptionsToContact(name, id, contactWatcherOptionSwitch.isChecked(), thumbnailUri);
				setBaseData(name, id, thumbnailUri, contactWatcherOptionSwitch.isChecked());
				saveOptionsButton.setVisible(false);
				optionsChanged = false;
				globalContext.saveAppStateToFile();
				Toast.makeText(ContactOptionsActivity.this, "Изменения приняты", Toast.LENGTH_SHORT).show();
			}
			else
    		{
    			Toast.makeText(ContactOptionsActivity.this, "Исправьте данные, отмеченые красным", Toast.LENGTH_SHORT).show();
    		}
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void update(Observable observable, Object object)
	{
		if((object instanceof SeeUMarker)&&(object != null))
		{
			globalContext.getMainActivity().runOnUiThread(new Runnable()
			{
			    public void run()
			    {
			    	markersListAdapter.setMarkers(contact.getMarkers());
			    	markersListAdapter.notifyDataSetChanged();
			    }
			});
		}
	}
}