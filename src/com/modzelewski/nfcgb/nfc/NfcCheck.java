package com.modzelewski.nfcgb.nfc;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.widget.Toast;

import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.controller.BackgroundModel;

public class NfcCheck {

	private final Context context;
	private final NfcAdapter nfcAdapter;

	public NfcCheck(NfcAdapter nfcAdapter, Context context) {
		this.context = context;
		this.nfcAdapter = nfcAdapter;
	}

	public void check(BackgroundModel model) {
		if (nfcAdapter == null) {
			Toast.makeText(context, context.getResources().getString(R.string.nfc_not_available), Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(context, context.getResources().getString(R.string.nfc_available), Toast.LENGTH_LONG).show();

//			// ###########################################
//			// # create NdefMessage #
//			// ###########################################
//
//			Event currentEvent = model.getCurrentEvent();
//			List<Event> eventList = new LinkedList<Event>();
//			eventList.add(currentEvent);
//
//			List<EventMembership> eventMemberships = model.getEventMemberships(currentEvent);
//
//			List<Person> persons = model.getPersons(eventMemberships);
//
//			List<Group> groups = model.getGroups(currentEvent);
//
//			List<GroupMembership> groupMemberships = new LinkedList<GroupMembership>();
//			for (Group group : groups) {
//				groupMemberships.addAll(model.getGroupMemberships(group));
//			}
//
//			byte[] eventBytes = writeByteArrayFromList(eventList);
//			byte[] eventMembershipBytes = writeByteArrayFromList(eventMemberships);
//			byte[] personBytes = writeByteArrayFromList(persons);
//			byte[] groupBytes = writeByteArrayFromList(groups);
//			byte[] groupMembershipBytes = writeByteArrayFromList(groupMemberships);
//
//			NdefRecord eventRecord = NdefRecord.createExternal("com.modzelewski.nfcgb.nfc", "EVENT", eventBytes);
//			NdefRecord eventMembershipRecord = NdefRecord.createExternal("com.modzelewski.nfcgb.nfc",
//					"EVENTMEMBERSHIP", eventMembershipBytes);
//			NdefRecord personRecord = NdefRecord.createExternal("com.modzelewski.nfcgb.nfc", "PERSON", personBytes);
//			NdefRecord groupRecord = NdefRecord.createExternal("com.modzelewski.nfcgb.nfc", "GROUPS", groupBytes);
//			NdefRecord groupMembershipRecord = NdefRecord.createExternal("com.modzelewski.nfcgb.nfc",
//					"GROUPMEMBERSHIP", groupMembershipBytes);
//
//			NdefMessage msg = new NdefMessage(eventRecord, eventMembershipRecord, personRecord, groupRecord,
//					groupMembershipRecord);
//			// return msg
//
//			// ###########################################
//			// # process intent #
//			// ###########################################
//			NdefRecord[] ndefRecords = msg.getRecords();
//
//			/*
//			 * ########################################## EVENT
//			 * ##########################################
//			 */
//			int eventId = eventParser(model, ndefRecords[0].getPayload());
//
//			/*
//			 * ########################################## PERSON
//			 * ##########################################
//			 */
//			Hashtable<Integer, Integer> changedPersonIds = personParser(model, ndefRecords[2].getPayload());
//
//			/*
//			 * ########################################## GROUP
//			 * ##########################################
//			 */
//			Hashtable<Integer, Integer> changedGroupIds = groupParser(model, ndefRecords[3].getPayload(), eventId);
//
//			/*
//			 * ########################################## EventMembership
//			 * ##########################################
//			 */
////			is not needed because can be new generated at addPersonIfNotExists
////			model.createEventMembershipsFromNdef(model, ndefRecords[1].getPayload(), eventId, changedPersonIds);
//
//			/*
//			 * ########################################## EventMembership
//			 * ##########################################
//			 */
//			model.createGroupMembershipsFromNdef(model, ndefRecords[4].getPayload(), changedGroupIds, changedPersonIds);

		}
	}

//	private Hashtable<Integer, Integer> groupParser(BackgroundModel model, byte[] groupBytes, int eventId) {
//		// read from byte array
//		ByteArrayInputStream bais = new ByteArrayInputStream(groupBytes);
//		DataInputStream in = new DataInputStream(bais);
//		List<String> groupStrings = new LinkedList<String>();
//		try {
//			while (in.available() > 0) {
//				groupStrings.add(in.readUTF());
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
////		if (groupStrings != null)
////			Log.i("NFCCHECKGroup", groupStrings.toString());
//		Hashtable<Integer, Integer> changedGroupIds = new Hashtable<Integer, Integer>();
//		for (String groupString : groupStrings) {
//			StringTokenizer tokens = new StringTokenizer(groupString, ",");
//
//			int oldId = Integer.parseInt(substringAfter(tokens.nextToken(), "id="));
//			// Log.i("NFCCHECK-OLDID", String.valueOf(oldId));
//			String groupName = substringAfter(tokens.nextToken(), "groupName=");
////			Log.i("NFCCHECK-NAME", groupName);
//			String oldEventId = substringAfter(tokens.nextToken(), "event_id=");
////			Log.i("NFCCHECK-EMAIL", oldEventId);
//
//			int newId = model.addGroupIfNotExists(groupName, eventId);
//
//			// newId == -1 means Person already exists. Btw: only exists if both
//			// name and email are the same
//			changedGroupIds.put(oldId, newId);
//			// int personId = model.addEventIfNotExists(eventName, year,
//			// wintersemester, info);
//		}
//		return changedGroupIds;
//	}
//
//	private int eventParser(BackgroundModel model, byte[] eventBytes) {
//		// byte[] eventBytes = ndefRecords[0].getPayload();
//		// read from byte array
//		ByteArrayInputStream bais = new ByteArrayInputStream(eventBytes);
//		DataInputStream in = new DataInputStream(bais);
//		String eventString = null;
//		try {
//			while (in.available() > 0) {
//				eventString = in.readUTF();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		// Log.i("NFCCHECK", eventString);
//		StringTokenizer tokens = new StringTokenizer(eventString, ",");
//		// Log.i("NFCCHECKTOKENSID", tokens.nextToken());
//		tokens.nextToken();
//		String eventName = substringAfter(tokens.nextToken(), "eventname=");
//		String year = substringAfter(tokens.nextToken(), "year=");
//		String wintersemester = substringAfter(tokens.nextToken(), "wintersemester=");
//		String info = substringAfter(tokens.nextToken(), "info=");
//		// Log.i("NFCCHECK-EVENTNAME", eventName);
//		// Log.i("NFCCHECK-YEAR", year);
//		// Log.i("NFCCHECK-WS", wintersemester);
//		// Log.i("NFCCHECK-INFO", info);
//		// Log.i("NFCCHECK-trim", String.valueOf(eventName.trim().length()));
//		int eventId = -1;
//		if (eventName != null && eventName.trim().length() != 0) {
//			eventId = model.addEventIfNotExists(eventName, year, wintersemester, info);
//		} else {
//			Toast.makeText(context, "No eventName specified.", Toast.LENGTH_LONG).show();
//		}
//		return eventId;
//	}
//
//	private Hashtable<Integer, Integer> personParser(BackgroundModel model, byte[] personBytes) {
//		// read from byte array
//		ByteArrayInputStream bais = new ByteArrayInputStream(personBytes);
//		DataInputStream in = new DataInputStream(bais);
//		List<String> personStrings = new LinkedList<String>();
//		try {
//			while (in.available() > 0) {
//				personStrings.add(in.readUTF());
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
////		if (personStrings != null)
////			Log.i("NFCCHECKPerson", personStrings.toString());
//		Hashtable<Integer, Integer> changedPersonIds = new Hashtable<Integer, Integer>();
//		for (String personString : personStrings) {
//			StringTokenizer tokens = new StringTokenizer(personString, ",");
//
//			int oldId = Integer.parseInt(substringAfter(tokens.nextToken(), "id="));
////			Log.i("NFCCHECK-OLDID", String.valueOf(oldId));
//			String name = substringAfter(tokens.nextToken(), "name=");
////			Log.i("NFCCHECK-NAME", name);
//			String email = substringAfter(tokens.nextToken(), "email=");
////			Log.i("NFCCHECK-EMAIL", email);
//
//			int newId = model.addPersonIfNotExists(name, email);
//			// newId == -1 means Person already exists. Btw: only exists if both
//			// name and email are the same
//			changedPersonIds.put(oldId, newId);
//			// int personId = model.addEventIfNotExists(eventName, year,
//			// wintersemester, info);
//		}
//		return changedPersonIds;
//	}
//
//	private byte[] writeByteArrayFromList(List<?> list) {
//		// write to byte array
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		DataOutputStream out = new DataOutputStream(baos);
//		for (Object element : list) {
//			try {
//				out.writeUTF(element.toString());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		byte[] data = baos.toByteArray();
//		return data;
//	}
//
//	/**
//	 * Returns the substring after the first occurrence of a delimiter. The
//	 * delimiter is not part of the result.
//	 * 
//	 * @param string
//	 *            String to get a substring from.
//	 * @param delimiter
//	 *            String to search for.
//	 * @return Substring after the last occurrence of the delimiter.
//	 */
//	public static String substringAfter(String string, String delimiter) {
//		int pos = string.indexOf(delimiter);
//
//		return pos >= 0 ? string.substring(pos + delimiter.length()) : "";
//	}
}
