package com.farsight.golf.ui;

import java.util.Observable;

public class ActivityObservable extends Observable {
	public void setDate(Object object) {
		setChanged();
		notifyObservers(object);
	}
}
