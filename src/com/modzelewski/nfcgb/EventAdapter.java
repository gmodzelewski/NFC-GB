package com.modzelewski.nfcgb;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Spinner adapter for showing events.
 * @author Georg
 */
public class EventAdapter extends ArrayAdapter<EventData> {

	public EventAdapter(Context context, int textViewResourceId, List<EventData> items) {
		super(context, textViewResourceId, items);
		setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text = (TextView)super.getView(position, convertView, parent);
		EventData ed = getItem(position);
		text.setText(prepareEventData(ed));
		return text;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView text = (TextView)super.getDropDownView(position, convertView, parent);
		EventData ed = getItem(position);
		text.setText(prepareEventData(ed));
		return text;
	}
	
	/**
	 * Prepares String to display: "NAME (SEMESTER YEAR)"
	 * @param ed EventData to display
	 * @return prepared String
	 */
	private String prepareEventData(EventData ed) {
		if(ed.year != 0)
			return String.format("%s (%s %d)", ed.eventname, ed.wintersemester ? "WiSe" : "SoSe", ed.year);
		else
			return String.format("%s (%s)", ed.eventname, ed.wintersemester ? "WiSe" : "SoSe");			
	}
}
