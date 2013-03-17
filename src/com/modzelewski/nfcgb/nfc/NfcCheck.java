package com.modzelewski.nfcgb.nfc;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.model.Group;
import com.modzelewski.nfcgb.model.Person;

public class NfcCheck {
    private final Context context;
    private final NfcAdapter nfcAdapter;

    public NfcCheck(NfcAdapter nfcAdapter, Context context) {
        this.context = context;
        this.nfcAdapter = nfcAdapter;
    }

    @SuppressLint("NewApi")
	public void check(BackgroundModel model) {
        if (nfcAdapter == null) {
            Toast.makeText(context, context.getResources().getString(R.string.nfc_not_available), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.nfc_available), Toast.LENGTH_LONG).show();

            List<Group> groups = model.getGroups();
//            if (!groups.isEmpty())
//                Toast.makeText(context, "Groups ist nicht leer", Toast.LENGTH_LONG).show();
//            
//            Log.i("NFC", "personsrep = " + persons.toString());
//            LinkedList<GroupMembership> groupMemberships = (LinkedList<GroupMembership>) model.getGroupMemberships(currentEvent);
//            LinkedList<EventMembership> eventMemberships = (LinkedList<EventMembership>) model.getEventMemberships(currentEvent);
//            LinkedList<Group> groups = (LinkedList<Group>) model.getGroups(currentEvent);
//            LinkedList<Person> persons = (LinkedList<Person>) model.getPersons(eventMemberships);

            Nfc nfc = new Nfc(nfcAdapter, context, model);
            
            Gson gson = new Gson();
            LinkedList<Person> persons = (LinkedList<Person>) model.getPersons();
            String payload = gson.toJson(persons);
//            NdefRecord ndefRecord = nfc.createTextRecord("hallo", Locale.GERMAN, true);
//            Log.i("NFC", "record = " + ndefRecord.toString());
//            NdefMessage msg = new NdefMessage(ndefRecord);
//            Log.i("NFC", "records = " + msg.getRecords().toString());
//            NdefRecord[] records = msg.getRecords();
//            String data = records[0].getPayload().toString();
            
            
            NdefRecord mimeRecord = NdefRecord.createMime("application/vnd.com.example.android.beam",
            	    payload.getBytes(Charset.forName("US-ASCII")));
            Log.i("NFC", "mimeRecord = " + mimeRecord.toString());
            
            
//            NdefRecord mimeRecord = new NdefRecord(
//            	    NdefRecord.TNF_MIME_MEDIA ,
//            	    "application/vnd.com.example.android.beam".getBytes(Charset.forName("US-ASCII")),
//            	    new byte[0], "Beam me up, Android!".getBytes(Charset.forName("US-ASCII")));
//            Log.i("NFC", "mimeRecord = " + mimeRecord.getPayload().toString());
//            
//            Event currentEvent = model.getCurrentEvent();
//            NdefMessage msg = new NdefMessage();
//            msg.toString();
//            
//            String gmsrep = gson.toJsonTree(currentEvent, persons);
    

        
        
        
        
        
        }
    }

}
