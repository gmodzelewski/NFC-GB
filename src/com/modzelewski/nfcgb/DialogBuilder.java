package com.modzelewski.nfcgb;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;

public class DialogBuilder extends MainActivity {
	
//	private DatabaseHelper databaseHelper = null;

	private final String ADD_PERSON = "ADD PERSON";
	private final String EDIT_PERSON = "EDIT PERSON";
	private final String ADD_GROUP = "ADD GROUP";
	private final String EDIT_GROUP = "EDIT GROUP";

	// Create and set the tags for the Buttons
	final String LISTVIEW_TAG = "ListView";
	final String EXPLISTVIEW_TAG = "ELV";
	final String TARGETLAYOUT_TAG = "targetLayout";
	
	public DialogBuilder(BackgroundModel model) {
		this.model = model;
	}

	void menuAddEvent() {

		LayoutInflater inflater = LayoutInflater.from(this);
//		LayoutInflater inflater = LayoutInflater.from(this);
		final View eventView = inflater.inflate(R.layout.event_dialog, null);

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(getResources().getString(R.string.add_event));
		adb.setView(eventView);
		NumberPicker year = (NumberPicker) eventView.findViewById(R.id.np_year);
		year.setMinValue(1940);
		year.setMaxValue(2300);
		Calendar c = Calendar.getInstance();
		year.setValue(c.get(Calendar.YEAR));
		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText eventname = (EditText) eventView.findViewById(R.id.ed_eventname);
				Switch wintersemester = (Switch) eventView.findViewById(R.id.ed_wintersemester);
				NumberPicker year = (NumberPicker) eventView.findViewById(R.id.np_year);
				EditText info = (EditText) eventView.findViewById(R.id.ed_info);

				EventData ed = new EventData();

				ed.eventname = eventname.getText().toString();
				ed.wintersemester = wintersemester.isChecked();
				ed.year = year.getValue();
				ed.info = info.getText().toString();

//				RuntimeExceptionDao<EventData, Integer> eventDao = databaseHelper.getEventDataDao();
//				eventDao.create(ed);
				model.events.add(ed);
				model.setCurrentEvent(ed);
//				setCurrentEvent(ed);
//				spinner.setSelection(ea.getPosition(ed));
//				ea.notifyDataSetChanged();
			}
		}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// ignore, just dismiss
			}
		}).show();

	}

//	private void menuGroup(final String task, final MenuItem item) {
//		final EventData currentEvent = model.getCurrentEvent();
//		LayoutInflater inflater = LayoutInflater.from(this);
//		final View groupView = inflater.inflate(R.layout.group_dialog, null);
//
//		AlertDialog.Builder adb = new AlertDialog.Builder(this);
//		EditText groupNameET = (EditText) groupView.findViewById(R.id.gd_groupName);
//		groupNameET.requestFocus();
//
//		String title = null;
//		if (task == ADD_GROUP)
//			title = getString(R.string.add_group);
//
//		if (task == EDIT_GROUP) {
//			title = getString(R.string.edit_group);
//			final ExpandableListContextMenuInfo pInfo = (ExpandableListContextMenuInfo) item.getMenuInfo();
//			final GroupData gd = model.groups.get((int) pInfo.id);
//			groupNameET.setText(gd.groupName);
//		}
//
//		adb.setTitle(title);
//		adb.setView(groupView);
//		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int whichButton) {
//				EditText groupNameET = (EditText) groupView.findViewById(R.id.gd_groupName);
//				String groupName = groupNameET.getText().toString().trim();
//				GroupData group = new GroupData(groupName, currentEvent.id);
//
//				// create Object
//				RuntimeExceptionDao<GroupData, Integer> groupDao = databaseHelper.getGroupDataDao();
//				if (task == ADD_GROUP) {
//					groupDao.create(group);
//					model.groups.add(group);
//				}
//				if (task == EDIT_GROUP) {
//					final ExpandableListContextMenuInfo pInfo = (ExpandableListContextMenuInfo) item.getMenuInfo();
//					final GroupData gd = model.groups.get((int) pInfo.id);
//					gd.groupName = groupName;
//					groupDao.update(gd);
//					groupDao.refresh(gd);
//				}
//				refreshListViews();
//			}
//		}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int whichButton) {
//			}
//		}).show();
//	}
//
//	private void menuGroupEmail(MenuItem item) {
//		ExpandableListContextMenuInfo pInfo = (ExpandableListContextMenuInfo) item.getMenuInfo();
//		GroupData gd = model.groups.get((int) pInfo.id);
//		List<PersonData> persons = gd.person;
//		String emailAddresses = "";
//		for (PersonData person : persons) {
//			if (person.email.contains("@")) {
//				emailAddresses += ", " + person.email;
//			} else {
//				Toast.makeText(getApplicationContext(), getString(R.string.incorrect_mail) + person.name, Toast.LENGTH_LONG).show();
//			}
//		}
//
//		final Intent i = new Intent(android.content.Intent.ACTION_SEND);
//		i.setType("message/rfc822");
//		i.putExtra(Intent.EXTRA_EMAIL, new String[] { emailAddresses });
//		try {
//			startActivity(Intent.createChooser(i, getString(R.string.about_email_chooser)));
//		} catch (android.content.ActivityNotFoundException ex) {
//			Toast.makeText(getBaseContext(), getString(R.string.about_no_email_apps), Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	private void menuGroupRemove(MenuItem item) {
//		ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
//		AlertDialog.Builder adb = new AlertDialog.Builder(this);
//		adb.setTitle(R.string.remove_group);
//		adb.setMessage(R.string.remove_group_message);
//		adb.setNegativeButton(R.string.cancel_button, null);
//		final ExpandableListView.ExpandableListContextMenuInfo pInfo = info;
//		adb.setPositiveButton(R.string.ok_button, new AlertDialog.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				GroupData gd = model.groups.get((int) pInfo.id);
//				RuntimeExceptionDao<GroupData, Integer> groupDao = databaseHelper.getGroupDataDao();
//				groupDao.delete(gd);
//				model.groups.remove(gd);
//				refreshListViews();
//			}
//		});
//		adb.show();
//	}
//
//	private void menuPerson(final String task, final MenuItem item) {
//		final EventData currentEvent = model.getCurrentEvent();
//		LayoutInflater inflater = LayoutInflater.from(this);
//		final View personView = inflater.inflate(R.layout.person_dialog, null);
//
//		String title = null;
//		String addPerson = getString(R.string.add_person_in) + " " + currentEvent.eventname;
//		String editPerson = getString(R.string.edit_person_in) + " " + currentEvent.eventname;
//		if (task == ADD_PERSON)
//			title = addPerson;
//		if (task == EDIT_PERSON) {
//			title = editPerson;
//
//			EditText nameET = (EditText) personView.findViewById(R.id.pd_name);
//			EditText emailET = (EditText) personView.findViewById(R.id.pd_email);
//
//			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//			final AdapterView.AdapterContextMenuInfo pInfo = info;
//			final PersonData pd = model.persons.get(pInfo.position);
//			nameET.setText(pd.name);
//			emailET.setText(pd.email);
//		}
//
//		AlertDialog.Builder adb = new AlertDialog.Builder(this);
//		adb.setTitle(title);
//		adb.setView(personView);
//		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int whichButton) {
//				EditText nameET = (EditText) personView.findViewById(R.id.pd_name);
//				EditText emailET = (EditText) personView.findViewById(R.id.pd_email);
//				String name = nameET.getText().toString().trim();
//				String email = emailET.getText().toString().trim();
//				RuntimeExceptionDao<PersonData, Integer> personDao = databaseHelper.getPersonDataDao();
//
//				if (task == ADD_PERSON) {
//					PersonData person = new PersonData(name, email);
//					RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = databaseHelper.getEventMembershipDataDao();
//					// RuntimeExceptionDao<GroupMembershipData, Integer>
//					// groupMembershipDao =
//					// databaseHelper.getGroupMembershipDataDao();
//
//					personDao.create(person);
//					EventMembershipData emd = new EventMembershipData(currentEvent.id, person.id);
//					eventMembershipDao.create(emd);
//					model.persons.add(person);
//				}
//
//				if (task == EDIT_PERSON) {
//					AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//					final AdapterView.AdapterContextMenuInfo pInfo = info;
//					final PersonData pd = model.persons.get(pInfo.position);
//
//					pd.name = name;
//					pd.email = email;
//
//					personDao.update(pd);
//					personDao.refresh(pd);
//				}
//				refreshListViews();
//			}
//		}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int whichButton) {
//				// ignore, just dismiss
//			}
//		}).show();
//	}
//
//	private void menuPersonRemove(MenuItem item) {
//		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//		AlertDialog.Builder adb = new AlertDialog.Builder(this);
//		adb.setTitle(R.string.context_menu_remove_title);
//		adb.setMessage(R.string.context_menu_remove_message);
//		adb.setNegativeButton(R.string.cancel_button, null);
//		final AdapterView.AdapterContextMenuInfo pInfo = info;
//		adb.setPositiveButton(R.string.ok_button, new AlertDialog.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = databaseHelper.getEventMembershipDataDao();
//				RuntimeExceptionDao<GroupMembershipData, Integer> groupMembershipDao = databaseHelper.getGroupMembershipDataDao();
//				List<EventMembershipData> emd = null;
//				List<GroupMembershipData> gmd = null;
//
//				PersonData pd = model.persons.get(pInfo.position);
//				try {
//					emd = eventMembershipDao.query(eventMembershipDao.queryBuilder().where().eq("person_id", pd.id).and().eq("event_id", model.getCurrentEvent().id).prepare());
//					gmd = groupMembershipDao.query(groupMembershipDao.queryBuilder().where().eq("person_id", pd.id).prepare());
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//				
//				for (GroupMembershipData groupMembershipData : gmd) {
//					model.getGroupById(groupMembershipData.group_id).person.remove(model.getPersonById(pd.id));
//				}
//				model.persons.remove(pd);
//				eventMembershipDao.delete(emd);
//				groupMembershipDao.delete(gmd);
//
//				refreshListViews();
//			}
//		});
//		adb.show();
//	}
//
//	private void menuEvent(final MenuItem item) {
//		// TODO menuAddEvent hier rein
//		final EventData currentEvent = model.getCurrentEvent();
//		LayoutInflater inflater = LayoutInflater.from(spinner.getContext());
//		final RuntimeExceptionDao<EventData, Integer> eventDao = databaseHelper.getEventDataDao();
//		final View eventView = inflater.inflate(R.layout.event_dialog, null);
//		EditText eventname = (EditText) eventView.findViewById(R.id.ed_eventname);
//		Switch wintersemester = (Switch) eventView.findViewById(R.id.ed_wintersemester);
//		NumberPicker year = (NumberPicker) eventView.findViewById(R.id.np_year);
//		EditText info = (EditText) eventView.findViewById(R.id.ed_info);
//		eventname.setText(currentEvent.eventname);
//		wintersemester.setChecked(currentEvent.wintersemester);
//		year.setMinValue(1940);
//		year.setMaxValue(2300);
//		year.setValue(currentEvent.year);
//		info.setText(currentEvent.info);
//
//		AlertDialog.Builder adb = new AlertDialog.Builder(spinner.getContext());
//		String title = getString(R.string.edit_event);
//		adb.setTitle(title);
//		adb.setView(eventView);
//		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int whichButton) {
//				EditText eventname = (EditText) eventView.findViewById(R.id.ed_eventname);
//				Switch wintersemester = (Switch) eventView.findViewById(R.id.ed_wintersemester);
//				NumberPicker year = (NumberPicker) eventView.findViewById(R.id.np_year);
//				EditText info = (EditText) eventView.findViewById(R.id.ed_info);
//
//				if (eventname.toString().trim().isEmpty())
//					currentEvent.eventname = getString(R.string.unknown_eventname);
//				else
//					currentEvent.eventname = eventname.getText().toString().trim();
//				currentEvent.wintersemester = wintersemester.isChecked();
//				currentEvent.year = year.getValue();
//				currentEvent.info = info.getText().toString();
//
//				eventDao.update(currentEvent);
//				eventDao.refresh(currentEvent);
//
//				ea.notifyDataSetChanged();
//			}
//		}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int whichButton) {
//				// ignore, just dismiss
//			}
//		}).show();
//
//	}
//
//	private void menuEventRemove(final MenuItem item) {
//		AlertDialog.Builder adb = new AlertDialog.Builder(this);
//		adb.setTitle(R.string.context_menu_remove_title);
//		adb.setMessage(R.string.context_menu_remove_message);
//		adb.setNegativeButton(R.string.cancel_button, null);
//		adb.setPositiveButton(R.string.ok_button, new AlertDialog.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				EventData currentEvent = model.getCurrentEvent();
//				RuntimeExceptionDao<EventData, Integer> eventDao = databaseHelper.getEventDataDao();
//				RuntimeExceptionDao<GroupData, Integer> groupDao = databaseHelper.getGroupDataDao();
//				RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = databaseHelper.getEventMembershipDataDao();
//				RuntimeExceptionDao<GroupMembershipData, Integer> groupMembershipDao = databaseHelper.getGroupMembershipDataDao();
//				List<EventData> ed = null;
//				List<GroupData> gd = null;
//				List<EventMembershipData> emd = null;
//				List<GroupMembershipData> gmd = null;
//				
//				try {
//					ed = eventDao.query(eventDao.queryBuilder().where().eq("id", model.getCurrentEvent().id).prepare());
//					gd = groupDao.query(groupDao.queryBuilder().where().eq("event_id", model.getCurrentEvent().id).prepare());
//					emd = eventMembershipDao.query(eventMembershipDao.queryBuilder().where().eq("event_id", model.getCurrentEvent().id).prepare());
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//				
//				for (GroupData groupData : gd) {
//					try {
//						gmd = groupMembershipDao.query(groupMembershipDao.queryBuilder().where().eq("group_id", groupData.id).prepare());
//					} catch (SQLException e) {
//						e.printStackTrace();
//					}
//				}
//				
//				eventDao.delete(ed);
//				groupDao.delete(gd);
//				eventMembershipDao.delete(emd);
//				groupMembershipDao.delete(gmd);
//
//				model.persons.clear();
//				model.groups.clear();
//				model.events.remove(currentEvent);
//				if(model.events.contains(currentEvent))
//					Toast.makeText(getBaseContext(), "ist noch drin", Toast.LENGTH_LONG).show();
//
//				ea.notifyDataSetChanged();
//				refreshListViews();
//
//				if (!model.getEvents().isEmpty())
//					setCurrentEvent(model.getEvents().get(0));
//			}
//		});
//		adb.show();
//	}

}
