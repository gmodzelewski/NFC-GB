package com.modzelewski.nfcgb.view;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.controller.EventAdapter;
import com.modzelewski.nfcgb.controller.GroupAdapter;
import com.modzelewski.nfcgb.controller.PersonAdapter;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public class EventSpinner {

	private Spinner eventSpinner;

	public EventSpinner(Spinner eventSpinner) {
		this.eventSpinner = eventSpinner;
	}

	/**
	 * Create spinner referencing at events in background model.
	 * @param databaseHelper 
	 * @param context 
	 * @param model 
	 * @param model 
	 * @return 
	 */
	public Spinner create(final BackgroundModel model, Context context, DatabaseHelper databaseHelper, final EventAdapter eventAdapter, final GroupAdapter groupAdapter, final PersonAdapter personAdapter) {
		eventSpinner.setAdapter(eventAdapter);
		eventSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				model.setCurrentEvent(model.getEvents().get(position));
				eventAdapter.notifyDataSetChanged();
				groupAdapter.notifyDataSetChanged();
				personAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});
		return eventSpinner;
	}
}
