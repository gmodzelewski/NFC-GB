package com.modzelewski.nfcgb.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.controller.GroupAdapter;
import com.modzelewski.nfcgb.controller.PersonAdapter;
import com.modzelewski.nfcgb.model.Person;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public class PersonDialog implements PersonDialogInterface {
	private final Context context;

	public PersonDialog(Context c) {
		this.context = c;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.modzelewski.nfcgb.view.PersonDialogInterface#addPerson(com.modzelewski
	 * .nfcgb.persistence.DatabaseHelper,
	 * com.modzelewski.nfcgb.model.BackgroundModel,
	 * com.modzelewski.nfcgb.controller.PersonAdapter)
	 */
	@Override
	public void addPerson(final DatabaseHelper dbh, final BackgroundModel model, final PersonAdapter personAdapter) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View personView = inflater.inflate(R.layout.person_dialog, null);
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle(context.getResources().getString(R.string.add_person_in) + " " + model.getCurrentEvent().getEventname());

		adb.setView(personView);
		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText nameET = (EditText) personView.findViewById(R.id.pd_name);
				EditText emailET = (EditText) personView.findViewById(R.id.pd_email);
				String name = nameET.getText().toString().trim();
				String email = emailET.getText().toString().trim();
				model.addPerson(name, email);
				personAdapter.notifyDataSetChanged();
			}
		}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// ignore, just dismiss
			}
		}).show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.modzelewski.nfcgb.view.PersonDialogInterface#editPerson(com.modzelewski
	 * .nfcgb.persistence.DatabaseHelper,
	 * com.modzelewski.nfcgb.model.BackgroundModel, android.view.MenuItem,
	 * com.modzelewski.nfcgb.controller.PersonAdapter)
	 */
	@Override
	public void editPerson(final BackgroundModel model, final MenuItem item, final GroupAdapter groupAdapter, final PersonAdapter personAdapter) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View personView = inflater.inflate(R.layout.person_dialog, null);
		EditText nameET = (EditText) personView.findViewById(R.id.pd_name);
		EditText emailET = (EditText) personView.findViewById(R.id.pd_email);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final AdapterView.AdapterContextMenuInfo pInfo = info;
		final Person person = model.persons.get(pInfo.position);
		nameET.setText(person.getName());
		emailET.setText(person.getEmail());
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle(context.getResources().getString(R.string.edit_person_in) + " " + model.getCurrentEvent().getEventname());
		adb.setView(personView);
		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText nameET = (EditText) personView.findViewById(R.id.pd_name);
				EditText emailET = (EditText) personView.findViewById(R.id.pd_email);
				String name = nameET.getText().toString().trim();
				String email = emailET.getText().toString().trim();
				model.editPerson(person, name, email);
                groupAdapter.notifyDataSetChanged();
                personAdapter.notifyDataSetChanged();
			}
		}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// ignore, just dismiss
			}
		}).show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.modzelewski.nfcgb.view.PersonDialogInterface#removePerson(com.modzelewski
	 * .nfcgb.persistence.DatabaseHelper,
	 * com.modzelewski.nfcgb.model.BackgroundModel, android.view.MenuItem,
	 * com.modzelewski.nfcgb.controller.PersonAdapter)
	 */
	@Override
	public void removePerson(final BackgroundModel model, final MenuItem item, final GroupAdapter groupAdapter, final PersonAdapter personAdapter) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle(R.string.context_menu_remove_title);
		adb.setMessage(R.string.context_menu_remove_message);
		adb.setNegativeButton(R.string.cancel_button, null);
		final AdapterView.AdapterContextMenuInfo pInfo = info;
		adb.setPositiveButton(R.string.ok_button, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Person person = model.persons.get(pInfo.position);
				model.removePerson(person);
                groupAdapter.notifyDataSetChanged();
                personAdapter.notifyDataSetChanged();
			}
		});
		adb.show();
	}
}
