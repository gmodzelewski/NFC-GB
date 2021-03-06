package com.modzelewski.nfcgb.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.modzelewski.nfcgb.model.Event;

import java.util.List;

/**
 * Spinner adapter for showing events.
 * @author Georg
 */
public class EventAdapter extends ArrayAdapter<Event> {

	public EventAdapter(Context context, List<Event> items) {
		super(context, android.R.layout.simple_spinner_item, items);
		setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text = (TextView)super.getView(position, convertView, parent);
		Event ed = getItem(position);
		text.setText(prepareEventData(ed));
		return text;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView text = (TextView)super.getDropDownView(position, convertView, parent);
		Event ed = getItem(position);
		text.setText(prepareEventData(ed));
		return text;
	}
	
	/**
	 * Prepares String to display: "NAME (SEMESTER YEAR)"
	 * @param ed EventData to display
	 * @return prepared String
	 */
	private String prepareEventData(Event ed) {
		if(ed.getYear() != 0)
			return String.format("%s (%s %d)", ed.getEventname(), ed.isWintersemester() ? "WiSe" : "SoSe", ed.getYear());
		else
			return String.format("%s (%s)", ed.getEventname(), ed.isWintersemester() ? "WiSe" : "SoSe");			
	}
}
