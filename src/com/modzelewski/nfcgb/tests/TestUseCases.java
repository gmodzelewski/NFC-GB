package com.modzelewski.nfcgb.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import com.jayway.android.robotium.solo.Solo;
import com.modzelewski.nfcgb.MainActivity;
import com.modzelewski.nfcgb.R;

public class TestUseCases extends
		ActivityInstrumentationTestCase2<MainActivity> {
	private Solo solo;

	public TestUseCases() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		solo.assertCurrentActivity("Check on first activity",
				MainActivity.class);
	}

	public void testUseCase01CreateEvent() {
		Spinner eventSpinner = (Spinner) solo.getView(R.id.events_spinner);
		int eventCount = eventSpinner.getCount();
		Log.i(getName(), "Eventcount = " + String.valueOf(eventCount));
		assertNotNull(eventSpinner);
		assertNotNull(eventCount);

		View labelForCreateEvent = solo.getView(R.id.om_add_event);
		solo.clickOnView(labelForCreateEvent);

		EditText eventNameEditText = (EditText) solo.getView(R.id.ed_eventname);
		solo.enterText(eventNameEditText,
				solo.getString(R.string.use_case_1_eventname));

		solo.clickOnButton(solo.getString(R.string.ok_button));

		// solo.clickOnView(labelForCreateEvent); // EventSpinner changes his
		// count
		// only at click on it

		solo.clickOnView(eventSpinner);
		int newEventCount = eventSpinner.getCount();
		Log.i(getName(), "newEventCount = " + String.valueOf(newEventCount));
		assertTrue(newEventCount == eventCount + 1);
	}

	public void testUseCase02EditEvent() {
		Spinner eventSpinner = (Spinner) solo.getView(R.id.events_spinner);
		int eventCount = eventSpinner.getCount();
		String newText = solo.getString(R.string.use_case_2_eventname);
		assertNotNull(eventSpinner);
		assertNotNull(eventCount);

		String labelForEditEvent = solo.getString(R.string.edit_event);
		solo.clickLongOnView(eventSpinner);
		solo.clickOnText(labelForEditEvent);

		EditText eventNameEditText = (EditText) solo.getView(R.id.ed_eventname);
		solo.clearEditText(eventNameEditText);
		solo.enterText(eventNameEditText, newText);

		solo.clickOnButton(solo.getString(R.string.ok_button));

		String eventSpinnerLabel = eventSpinner.getSelectedItem().toString();
		Log.i(getName(), "Spinnerlabel = " + eventSpinnerLabel);

		assertTrue(eventSpinnerLabel.contains(newText));

	}


	public void testUseCase03ChangeEvent() {
		Spinner eventSpinner = (Spinner) solo.getView(R.id.events_spinner);

		// create 2 events - same as in BackgroundModelTest
		int eventCountBefore = eventSpinner.getCount();
		assertNotNull(eventSpinner);
		assertNotNull(eventCountBefore);
		View labelForCreateEvent = solo.getView(R.id.om_add_event);
		// add event 1
		solo.clickOnView(labelForCreateEvent);
		EditText eventNameEditText = (EditText) solo.getView(R.id.ed_eventname);
		solo.enterText(eventNameEditText,
				solo.getString(R.string.use_case_1_eventname));
		solo.clickOnButton(solo.getString(R.string.ok_button));
		// add event 2
		solo.clickOnView(labelForCreateEvent);
		EditText eventNameEditText2 = (EditText) solo
				.getView(R.id.ed_eventname);
		solo.enterText(eventNameEditText2,
				solo.getString(R.string.use_case_3_eventname));
		solo.clickOnButton(solo.getString(R.string.ok_button));
		solo.clickOnView(eventSpinner);
		int eventCountAfter = eventSpinner.getCount();
		assertTrue(eventCountAfter - 2 == eventCountBefore);

		solo.clickOnText(solo.getString(R.string.use_case_1_eventname));
		// assertTrue(); don't know
	}


	public void testUseCase04RemoveEvent() {
		Spinner eventSpinner = (Spinner) solo.getView(R.id.events_spinner);
		int eventCountBefore = eventSpinner.getCount();
		Log.i(getName(), "Eventcount = " + String.valueOf(eventCountBefore));
		assertNotNull(eventSpinner);
		assertNotNull(eventCountBefore);

		String labelForRemoveEvent = solo.getString(R.string.remove_event);
		solo.clickLongOnView(eventSpinner);
		solo.clickOnText(labelForRemoveEvent);
		solo.clickOnButton(solo.getString(R.string.ok_button));

		solo.clickOnView(eventSpinner);
		int eventCountAfter = eventSpinner.getCount();
		Log.i("USECASEREMOVE", "count before = \n\r" + eventCountBefore
				+ "\ncount after = \n\t" + eventCountAfter);
		assertEquals(eventCountAfter, eventCountBefore - 1);
	}

	public void testUseCase04RemoveEventWhileMoreThanOne() {
		Spinner eventSpinner = (Spinner) solo.getView(R.id.events_spinner);
		String labelForRemoveEvent = solo.getString(R.string.remove_event);

		int i = eventSpinner.getCount();
		while (i > 1) {
			assertNotNull(eventSpinner);
			solo.clickLongOnView(eventSpinner);
			solo.clickOnText(labelForRemoveEvent);
			solo.clickOnButton(solo.getString(R.string.ok_button));
			i--;
		}
		assertTrue(eventSpinner.getCount() == 1);
	}

	public void testUseCase05CreatePerson() {
		View labelForCreatePerson = solo.getView(R.id.om_add_person);
		ListView personLV = (ListView) solo.getView(R.id.personsLV);
		int countBefore = personLV.getCount();
		solo.clickOnView(labelForCreatePerson);

		EditText personNameEditText = (EditText) solo.getView(R.id.pd_name);
		EditText personMailEditText = (EditText) solo.getView(R.id.pd_email);
		solo.enterText(personNameEditText, "Person with a name");
		solo.enterText(personMailEditText, "person@emailadress.de");
		solo.clickOnButton(solo.getString(R.string.ok_button));
		int countAfter = personLV.getCount();
		Log.i("testUseCase05CreatePerson", "countBefore =\n\t"+countBefore);
		Log.i("testUseCase05CreatePerson", "countAfter =\n\t"+countAfter);
		assertTrue(countBefore == countAfter - 1);
	}

	public void testUseCase06EditPersonPerson() {
		String editPerson = solo.getString(R.string.edit_person);
		ListView personLV = (ListView) solo.getView(R.id.personsLV);
		int countBefore = personLV.getCount();
		solo.clickOnView(personLV.getChildAt(countBefore));
		solo.clickOnText(editPerson);

		EditText personNameEditText = (EditText) solo.getView(R.id.pd_name);
		solo.clearEditText(personNameEditText);
		EditText personMailEditText = (EditText) solo.getView(R.id.pd_email);
		solo.clearEditText(personMailEditText);
		
		String personName = "The edited name of a person";
		String personMail = "person@mailadress.de";
		solo.enterText(personNameEditText, personName);
		solo.enterText(personMailEditText, personMail);
		solo.clickOnButton(solo.getString(R.string.ok_button));
		int countAfter = personLV.getCount();
		assertTrue(countBefore == countAfter);
		assertTrue(solo.searchText(personName));
		assertTrue(solo.searchText(personMail));
	}

	@Suppress
	public void testUseCase07DeletePerson() {

	}

	@Suppress
	public void testUseCase08CreateGroup() {

	}

	@Suppress
	public void testUseCase09EditGroup() {

	}

	@Suppress
	public void testUseCase10RemoveGroup() {

	}

	@Suppress
	public void testUseCase11AssignPersonToGroup() {

	}

	@Suppress
	public void testUseCase12SendGroupMail() {

	}

	@Suppress
	public void testUseCase13ShowAndHidePersonsInAGroup() {

	}

	@Suppress
	public void testUseCase14EditPersonWhichIsInAGroup() {

	}

	@Suppress
	public void testUseCase15RemovePersonFromGroup() {

	}

	@Suppress
	public void testUseCase16SyncViaNFC() {

	}

	@Override
	protected void tearDown() throws Exception {

		solo.finishOpenedActivities();
	}
}