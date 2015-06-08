package com.yuriydev.seeu;

import java.io.Serializable;

public class SeeUMarker implements Serializable
{
	private SeeUTimestamp timestamp;
	private boolean visible = true;
	private int precision;
	private double latitude;
	private double longitude;
	private String title;
	private String snippet;
	private int markerBodyImageResId;
	private String markerBodyImageUriPath;
	
	public SeeUMarker(SeeUTimestamp timestamp, double lat, double lng, int precision, String title, String snippet, int markerBodyImageResId)
	{
		this.timestamp = timestamp;
		this.latitude = lat;
		this.longitude = lng;
		this.precision = precision;
		this.title = title;
		this.snippet = snippet;
		this.markerBodyImageResId = markerBodyImageResId;
	}
	
	public SeeUMarker(SeeUTimestamp timestamp, double lat, double lng, int precision, String title, String snippet, String markerBodyImageUriPath)
	{
		this.timestamp = timestamp;
		this.latitude = lat;
		this.longitude = lng;
		this.precision = precision;
		this.title = title;
		this.snippet = snippet;
		this.markerBodyImageUriPath = markerBodyImageUriPath;
	}

	public SeeUTimestamp getTimestamp()
	{
		return timestamp;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public int getPrecision() {
		return precision;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
	public String getTitle() {
		return title;
	}

	public String getSnippet() {
		return snippet;
	}

	public int getMarkerBodyImageResId() {
		return markerBodyImageResId;
	}

	public void setMarkerBodyImageResId(int markerBodyImageResId) {
		this.markerBodyImageResId = markerBodyImageResId;
	}
}