package com.modzelewski.nfcgb.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.modzelewski.nfcgb.model.Person;

import java.util.List;

/**
 * ListView adapter for showing persons.
 * @author Georg
 */
public class PersonAdapter extends ArrayAdapter<Person> {

	public PersonAdapter(Context context, List<Person> items) {
		super(context, android.R.layout.simple_list_item_1, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text = (TextView)super.getView(position, convertView, parent);
		Person pd = getItem(position);
		text.setText(preparePersonData(pd));
		return text;
	}
	
	/**
	 * Prepares String to display: "NAME\nMAIL"
	 * @return prepared String
	 */
	private String preparePersonData(Person pd) {
		return String.format("%s\n%s", pd.getName(), pd.getEmail());
	}
}
