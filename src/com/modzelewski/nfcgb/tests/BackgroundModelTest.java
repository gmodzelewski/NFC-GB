package com.modzelewski.nfcgb.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import com.jayway.android.robotium.solo.Solo;
import com.modzelewski.nfcgb.MainActivity;
import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.model.Event;

/**
 * Created with IntelliJ IDEA. User: melitta Date: 12.03.13 Time: 21:57 To
 * change this template use File | Settings | File Templates.
 */
public class BackgroundModelTest extends
        ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;
    private BackgroundModel model;
    
    public BackgroundModelTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
        solo.assertCurrentActivity("Check on first activity",
                MainActivity.class);
        assertNotNull(getActivity());
        this.model = getActivity().getModel();
        model.persons.clear();
        model.groups.clear();
        model.events.clear();
        assertNotNull(model);
        assertTrue(model.events.isEmpty());
        assertTrue(model.groups.isEmpty());
        assertTrue(model.persons.isEmpty());
        model.reloadEverything();
    }

    public void testEvent() {
        Event testEvent = new Event("test", 2004, true, "");
        model.addEvent(testEvent);
        assert (model.getEvents().size() > 0);
    }

    //Too buggy, make testcases first
    @Suppress
    public void testAdd2Events() {
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

    @Override
    protected void tearDown() throws Exception {

        solo.finishOpenedActivities();
    }
}
