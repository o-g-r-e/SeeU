package com.yuriydev.seeu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

import android.net.Uri;

public class Contact implements Serializable
{
	private String name;
	private String id;
	private String photoURI;
	private boolean watching = false;
	private ArrayList<SeeUMarker> markers;

	public Contact(String name, String photoURI, boolean watching)
	{
		this.name = formatName(name);
		this.watching = watching;
		this.photoURI = photoURI;
		this.markers = new ArrayList<SeeUMarker>();
		this.id = "";
	}
	
	public Contact()
	{
		this.watching = false;
		this.markers = new ArrayList<SeeUMarker>();
	}
	
	private String formatName(String nameString)
	{
		nameString = nameString.trim();
		char[] chars = nameString.toCharArray();
		
		String upChar = Character.toString(chars[0]).toUpperCase();
		chars[0] = upChar.charAt(0);
		
		for (int i = 0; i < chars.length; i++)
		{
			if(chars[i] == ' ')
			{
				upChar = Character.toString(chars[i+1]).toUpperCase();
				chars[i+1] = upChar.charAt(0);
			}
		}
		
		return String.copyValueOf(chars);
	}
	
	public boolean isValid()
	{
		boolean result = false;
		
		if((id != null)&&(id.length() > 0))
		{
			if((name != null)&&(name.length() > 0))
			{
				result = true;
			}
		}
		
		return result;
	}
	
	public void substituteLastMarker(SeeUMarker marker)
	{
		if(markers.size() > 0)
		{
			markers.set(markers.size()-1, marker);
		}
		else
		{
			markers.add(marker);
		}
	}
	
	public boolean isWatching() {
		return watching;
	}
	
	public void setWatching(boolean watching) {
		this.watching = watching;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = formatName(name);
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPhotoURI() {
		return photoURI;
	}
	
	public void setPhotoURI(String photoURI) {
		this.photoURI = photoURI;
	}
	
	public SeeUMarker getMarker(int i) {
		return markers.get(i);
	}
	
	public void addMarker(SeeUMarker marker) {
		markers.add(marker);
	}
	
	public SeeUMarker getLastMarker()
	{
		if(markers.size() > 0)
			return markers.get(markers.size()-1);
		
		return null;
	}
	
	public void removeMarkers(int start, int end)
	{
		for(ListIterator<SeeUMarker> i = markers.subList(start, end).listIterator(); i.hasNext();)
		{
			SeeUMarker el = i.next();
			i.remove();
		}
	}
	
	public ArrayList<SeeUMarker> getMarkers() {
		return markers;
	}
}