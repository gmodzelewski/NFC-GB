package com.modzelewski.nfcgb.nfc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

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

import com.modzelewski.nfcgb.MainActivity;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.model.Event;
import com.modzelewski.nfcgb.model.EventMembership;
import com.modzelewski.nfcgb.model.Group;
import com.modzelewski.nfcgb.model.GroupMembership;
import com.modzelewski.nfcgb.model.Person;

public class Nfc extends MainActivity implements CreateNdefMessageCallback {
//	private final NfcAdapter nfcAdapter;
	private final Context context;
	private final BackgroundModel model;

	public Nfc(NfcAdapter nfcAdapter, Context context, BackgroundModel model) {
//		this.nfcAdapter = nfcAdapter;
		this.context = context;
		this.model = model;
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		Log.i("NFC", "CREATING NdefMessage");
		Event currentEvent = model.getCurrentEvent();
		List<Event> eventList = new LinkedList<Event>();
		eventList.add(currentEvent);

		List<EventMembership> eventMemberships = model.getEventMemberships(currentEvent);
		List<Person> persons = model.getPersons(eventMemberships);
		List<Group> groups = model.getGroups(currentEvent);
		List<GroupMembership> groupMemberships = new LinkedList<GroupMembership>();
		for (Group group : groups) {
			groupMemberships.addAll(model.getGroupMemberships(group));
		}

		byte[] eventBytes = writeByteArrayFromList(eventList);
		byte[] eventMembershipBytes = writeByteArrayFromList(eventMemberships);
		byte[] personBytes = writeByteArrayFromList(persons);
		byte[] groupBytes = writeByteArrayFromList(groups);
		byte[] groupMembershipBytes = writeByteArrayFromList(groupMemberships);

		NdefRecord eventRecord = NdefRecord.createExternal("com.modzelewski.nfcgb.nfc", "EVENT", eventBytes);
		NdefRecord eventMembershipRecord = NdefRecord.createExternal("com.modzelewski.nfcgb.nfc", "EVENTMEMBERSHIP",
				eventMembershipBytes);
		NdefRecord personRecord = NdefRecord.createExternal("com.modzelewski.nfcgb.nfc", "PERSON", personBytes);
		NdefRecord groupRecord = NdefRecord.createExternal("com.modzelewski.nfcgb.nfc", "GROUPS", groupBytes);
		NdefRecord groupMembershipRecord = NdefRecord.createExternal("com.modzelewski.nfcgb.nfc", "GROUPMEMBERSHIP",
				groupMembershipBytes);

		NdefMessage msg = new NdefMessage(eventRecord, eventMembershipRecord, personRecord, groupRecord,
				groupMembershipRecord);
		return msg;

	}

	// public NdefRecord createTextRecord(String payload, Locale locale, boolean
	// encodeInUtf8) {
	// byte[] langBytes =
	// locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
	// Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") :
	// Charset.forName("UTF-16");
	// byte[] textBytes = payload.getBytes(utfEncoding);
	// int utfBit = encodeInUtf8 ? 0 : (1 << 7);
	// char status = (char) (utfBit + langBytes.length);
	// byte[] data = new byte[1 + langBytes.length + textBytes.length];
	// data[0] = (byte) status;
	// System.arraycopy(langBytes, 0, data, 1, langBytes.length);
	// System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
	// textBytes.length);
	// NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
	// NdefRecord.RTD_TEXT, new byte[0], data);
	// return record;
	// }

	/**
	 * Creates a custom MIME type encapsulated in an NDEF record
	 */
	public NdefRecord createMimeRecord(String mimeType, byte[] payload) {

		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
		return mimeRecord;
	}

	/**
	 * Parses the NDEF Message from the intent and prints to a Toast
	 */
	public void processIntent(Intent intent) {
		Log.i("NFC", "PROCESSING");

		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// only one message sent during the beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		// record 0 contains the MIME type, record 1 is the AAR, if present
		Toast.makeText(context, "Got it", Toast.LENGTH_LONG).show();
		Toast.makeText(context, new String(msg.getRecords()[0].getPayload()), Toast.LENGTH_LONG).show();

		// 0: eventRecord
		// 1: eventMembershipRecord
		// 2: personRecord
		// 3: groupRecord
		// 4: groupMembershipRecord

		// ###########################################
		// # process intent #
		// ###########################################
		NdefRecord[] ndefRecords = msg.getRecords();

		/*
		 * ########################################## EVENT
		 * ##########################################
		 */
		int eventId = model.eventParser(model, ndefRecords[0].getPayload());

		/*
		 * ########################################## PERSON
		 * ##########################################
		 */
		Hashtable<Integer, Integer> changedPersonIds = model.personParser(model, ndefRecords[2].getPayload());

		/*
		 * ########################################## GROUP
		 * ##########################################
		 */
		Hashtable<Integer, Integer> changedGroupIds = model.groupParser(model, ndefRecords[3].getPayload(), eventId);

		/*
		 * ########################################## EventMembership
		 * ##########################################
		 */
		// is not needed because can be new generated at addPersonIfNotExists
		// model.createEventMembershipsFromNdef(model,
		// ndefRecords[1].getPayload(), eventId, changedPersonIds);

		/*
		 * ########################################## EventMembership
		 * ##########################################
		 */
		model.createGroupMembershipsFromNdef(model, ndefRecords[4].getPayload(), changedGroupIds, changedPersonIds);
	}

	private byte[] writeByteArrayFromList(List<?> list) {
		// write to byte array
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);
		for (Object element : list) {
			try {
				out.writeUTF(element.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		byte[] data = baos.toByteArray();
		return data;
	}

}
