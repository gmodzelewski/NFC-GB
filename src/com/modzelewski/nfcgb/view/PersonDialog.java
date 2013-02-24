package com.modzelewski.nfcgb.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.controller.GroupAdapter;
import com.modzelewski.nfcgb.controller.PersonAdapter;
import com.modzelewski.nfcgb.model.BackgroundModel;
import com.modzelewski.nfcgb.model.EventMembershipData;
import com.modzelewski.nfcgb.model.GroupMembershipData;
import com.modzelewski.nfcgb.model.PersonData;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

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
	public void addPerson(final DatabaseHelper dbh, final BackgroundModel model, final PersonAdapter pa) {
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
				RuntimeExceptionDao<PersonData, Integer> personDao = dbh.getPersonDataDao();
				PersonData person = new PersonData(name, email);
				RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = dbh.getEventMembershipDataDao();
				personDao.create(person);
				EventMembershipData emd = new EventMembershipData(model.getCurrentEvent().getId(), person.getId());
				eventMembershipDao.create(emd);
				model.persons.add(person);
				pa.notifyDataSetChanged();
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
	public void editPerson(final DatabaseHelper dbh, final BackgroundModel model, final MenuItem item, final PersonAdapter pa, final GroupAdapter ga) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View personView = inflater.inflate(R.layout.person_dialog, null);
		EditText nameET = (EditText) personView.findViewById(R.id.pd_name);
		EditText emailET = (EditText) personView.findViewById(R.id.pd_email);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final AdapterView.AdapterContextMenuInfo pInfo = info;
		final PersonData pd = model.persons.get(pInfo.position);
		nameET.setText(pd.getName());
		emailET.setText(pd.getEmail());
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
				RuntimeExceptionDao<PersonData, Integer> personDao = dbh.getPersonDataDao();
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
				final AdapterView.AdapterContextMenuInfo pInfo = info;
				final PersonData pd = model.persons.get(pInfo.position);

				pd.setName(name);
				pd.setEmail(email);

				personDao.update(pd);
				personDao.refresh(pd);

				pa.notifyDataSetChanged();
				ga.notifyDataSetChanged();
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
	public void removePerson(final DatabaseHelper dbh, final BackgroundModel model, final MenuItem item, final PersonAdapter pa, final GroupAdapter ga) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle(R.string.context_menu_remove_title);
		adb.setMessage(R.string.context_menu_remove_message);
		adb.setNegativeButton(R.string.cancel_button, null);
		final AdapterView.AdapterContextMenuInfo pInfo = info;
		adb.setPositiveButton(R.string.ok_button, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = dbh.getEventMembershipDataDao();
				RuntimeExceptionDao<GroupMembershipData, Integer> groupMembershipDao = dbh.getGroupMembershipDataDao();
				List<EventMembershipData> emd = null;
				List<GroupMembershipData> gmd = null;

				PersonData pd = model.persons.get(pInfo.position);
				try {
					emd = eventMembershipDao.query(eventMembershipDao.queryBuilder().where().eq("person_id", pd.getId()).and().eq("event_id", model.getCurrentEvent().getId()).prepare());
					gmd = groupMembershipDao.query(groupMembershipDao.queryBuilder().where().eq("person_id", pd.getId()).prepare());
				} catch (SQLException e) {
					e.printStackTrace();
				}

				for (GroupMembershipData groupMembershipData : gmd) {
					model.getGroupById(groupMembershipData.getGroup_id()).getPerson().remove(model.getPersonById(pd.getId()));
				}
				model.persons.remove(pd);
				eventMembershipDao.delete(emd);
				groupMembershipDao.delete(gmd);

				pa.notifyDataSetChanged();
				ga.notifyDataSetChanged();
			}
		});
		adb.show();
	}

}
