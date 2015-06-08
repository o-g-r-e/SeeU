package com.yuriydev.seeu;

import java.util.Observable;

public class GlobalObservableNode extends Observable
{
	public void notifyAboutNewMarker(SeeUMarker newMarker)
	{
		setChanged();
		notifyObservers(newMarker);
	}
}
