package com.modzelewski.nfcgb;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * ListView adapter for showing persons.
 * @author Georg
 */
public class PersonAdapter extends ArrayAdapter<PersonData> {

	public PersonAdapter(Context context, int textViewResourceId, List<PersonData> items) {
		super(context, textViewResourceId, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text = (TextView)super.getDropDownView(position, convertView, parent);
		PersonData pd = getItem(position);
		text.setText(preparePersonData(pd));
		return text;
	}
	
	/**
	 * Prepares String to display: "NAME\nMAIL"
	 * @param ed PersonData to display
	 * @return prepared String
	 */
	private String preparePersonData(PersonData pd) {
		return String.format("%s\n%s", pd.name, pd.email);
	}
}
