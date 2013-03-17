package com.modzelewski.nfcgb.nfc;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.modzelewski.nfcgb.MainActivity;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.model.Event;
import com.modzelewski.nfcgb.model.EventMembership;
import com.modzelewski.nfcgb.model.Group;
import com.modzelewski.nfcgb.model.Person;

public class Nfc extends MainActivity implements CreateNdefMessageCallback {
    private final NfcAdapter nfcAdapter;
    private final Context context;
    private final BackgroundModel model;

    public Nfc(NfcAdapter nfcAdapter, Context context, BackgroundModel model) {
        this.nfcAdapter = nfcAdapter;
        this.context = context;
        this.model = model;
    }


    // CREATE!!!
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        // String text = ("Beam me up, Android!\n\n" + "Beam Time: " +
        // System.currentTimeMillis());
        Event currentEvent = model.getCurrentEvent();
//        LinkedList<GroupMembership> groupMemberships = (LinkedList<GroupMembership>) model.getGroupMemberships(currentEvent);
        LinkedList<EventMembership> eventMemberships = (LinkedList<EventMembership>) model.getEventMemberships(currentEvent);
        LinkedList<Group> groups = (LinkedList<Group>) model.getGroups(currentEvent);
        LinkedList<Person> persons = (LinkedList<Person>) model.getPersons(eventMemberships);

        Gson gson = new Gson();
        String eventrep = gson.toJson(currentEvent, Event.class);
        Log.i("NFC", "eventrep = " + eventrep);
        
//        gson.toJson(groupMemberships, GroupMembership.class);
//        gson.toJson(eventMemberships, EventMembership.class);
//        gson.toJson(groups, Group.class);
//        gson.toJson(persons, Person.class);
        
//		Person person1 = new Person("Hans", "hans@email.de");
//		// PersonData person2 = new PersonData("Peter", "peter@email.de");
//		String person1Name = person1.getName();
//		NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord("application/com.modzelewski.nfcgb", person1Name.getBytes())
//		/**
//		 * The Android Application Record (AAR) is commented out. When a device
//		 * receives a push with an AAR in it, the application specified in the
//		 * AAR is guaranteed to run. The AAR overrides the tag dispatch system.
//		 * You can add it back in to guarantee that this activity starts when
//		 * receiving a beamed message. For now, this code uses the tag dispatch
//		 * system.
//		 */
//		// ,NdefRecord.createApplicationRecord("com.modzelewski.nfcgb")
//				});
//		return msg;
        return null;
    }

    public NdefRecord createTextRecord(String payload, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
        return record;
    }


    /**
     * Creates a custom MIME type encapsulated in an NDEF record
     */
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {

        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }

    // /**
    // * Parses the NDEF Message from the intent and prints to a Toast
    // */
    // public void processIntent(Context context, Intent intent) {
    // Parcelable[] rawMsgs =
    // intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
    // // only one message sent during the beam
    // NdefMessage msg = (NdefMessage) rawMsgs[0];
    // // record 0 contains the MIME type, record 1 is the AAR, if present
    // Toast.makeText(context, new String(msg.getRecords()[0].getPayload()),
    // Toast.LENGTH_LONG).show();
    // }

    //

    /**
     * Parses the NDEF Message from the intent and prints to a Toast
     */
    public void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        Toast.makeText(context, "Got it", Toast.LENGTH_LONG).show();
        Toast.makeText(context, new String(msg.getRecords()[0].getPayload()), Toast.LENGTH_LONG).show();
    }

}
