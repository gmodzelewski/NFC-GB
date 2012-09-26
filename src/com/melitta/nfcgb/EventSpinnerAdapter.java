package com.melitta.nfcgb;

import java.util.List;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;

public class EventSpinnerAdapter implements SpinnerAdapter {
	List<EventData> data;
	public EventSpinnerAdapter(List<EventData> data) {
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return android.R.layout.simple_spinner_item;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EventData event = data.get(position);
		// TODO: view with string
		//return String.format("%s (%s %d)", event.eventname, event.wintersemester ? "WiSe" : "SoSe", event.year);
		return null;
	}

	@Override
	public int getViewTypeCount() {
		// TODO: 
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO: 
		return false;
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO: 
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO: 
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO: 
		return null;
	}
}
