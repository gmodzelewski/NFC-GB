package com.modzelewski.nfcgb.nfc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

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
import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.model.Event;
import com.modzelewski.nfcgb.model.EventMembership;
import com.modzelewski.nfcgb.model.Group;
import com.modzelewski.nfcgb.model.GroupMembership;
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
    	Log.i("NFC", "CREATING");
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
        
//        NdefRecord eventRecord = NdefRecord.createExternal("com.modzelewski.nfcgb", "EVENT", eventBytes);
//        NdefRecord eventMembershipRecord = NdefRecord.createExternal("com.modzelewski.nfcgb", "EVENTMEMBERSHIP", eventMembershipBytes);
//        NdefRecord personRecord = NdefRecord.createExternal("com.modzelewski.nfcgb", "PERSON", personBytes);
//        NdefRecord groupRecord = NdefRecord.createExternal("com.modzelewski.nfcgb", "GROUPS", groupBytes);
//        NdefRecord groupMembershipRecord = NdefRecord.createExternal("com.modzelewski.nfcgb", "GROUPMEMBERSHIPS", groupMembershipBytes);
        NdefRecord eventRecord = NdefRecord.createMime("application/com.modzelewski.nfcgb", eventBytes);
        NdefRecord eventMembershipRecord = NdefRecord.createMime("application/com.modzelewski.nfcgb", eventMembershipBytes);
        NdefRecord personRecord = NdefRecord.createMime("application/com.modzelewski.nfcgb", personBytes);
        NdefRecord groupRecord = NdefRecord.createMime("application/com.modzelewski.nfcgb", groupBytes);
        NdefRecord groupMembershipRecord = NdefRecord.createMime("application/com.modzelewski.nfcgb", groupMembershipBytes);
        
        NdefMessage msg = new NdefMessage(eventRecord, eventMembershipRecord, personRecord, groupRecord, groupMembershipRecord);
//        NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord("application/com.modzelewski.nfcgb", eventBytes) });
        return msg;
    	
    	
//        // String text = ("Beam me up, Android!\n\n" + "Beam Time: " +
//        // System.currentTimeMillis());
//        Event currentEvent = model.getCurrentEvent();
////        LinkedList<GroupMembership> groupMemberships = (LinkedList<GroupMembership>) model.getGroupMemberships(currentEvent);
//        LinkedList<EventMembership> eventMemberships = (LinkedList<EventMembership>) model.getEventMemberships(currentEvent);
//        LinkedList<Group> groups = (LinkedList<Group>) model.getGroups(currentEvent);
//        LinkedList<Person> persons = (LinkedList<Person>) model.getPersons(eventMemberships);
//
//        Gson gson = new Gson();
//        String eventrep = gson.toJson(currentEvent, Event.class);
//        Log.i("NFC", "eventrep = " + eventrep);
//        
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
    }

//    public NdefRecord createTextRecord(String payload, Locale locale, boolean encodeInUtf8) {
//        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
//        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
//        byte[] textBytes = payload.getBytes(utfEncoding);
//        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
//        char status = (char) (utfBit + langBytes.length);
//        byte[] data = new byte[1 + langBytes.length + textBytes.length];
//        data[0] = (byte) status;
//        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
//        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
//        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
//        return record;
//    }


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
    	Log.i("NFC", "PROCESSING");

        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        Toast.makeText(context, "Got it", Toast.LENGTH_LONG).show();
        Toast.makeText(context, new String(msg.getRecords()[0].getPayload()), Toast.LENGTH_LONG).show();
        
//      0: eventRecord
//      1: eventMembershipRecord
//      2: personRecord
//      3: groupRecord
//      4: groupMembershipRecord
        
        NdefRecord[] ndefRecords = msg.getRecords();
        byte[] eventBytes = ndefRecords[0].getPayload();
        byte[] eventMembershipBytes = ndefRecords[1].getPayload();
        byte[] personBytes = ndefRecords[2].getPayload();
        byte[] groupBytes = ndefRecords[3].getPayload();
        byte[] groupMembershipBytes = ndefRecords[4].getPayload();
        
        Event event = readFromByteToEvent(eventBytes);
        List<EventMembership> eventMemberships = readFromByteToEventMembershipList(eventMembershipBytes);
        List<Person> persons = readFromByteToPersonList(personBytes);
        List<Group> groups = readFromByteToGroupList(groupBytes);
        List<GroupMembership> groupMemberships = readFromByteToGroupMembershipList(groupMembershipBytes);
        
        if(!model.addEventIfNotExists(event)){
//        	//make everything new
//        	
//        	//make new EventMemberships from the event id and the persons
//        	model.addEventMemberships(eventMemberships, event.getId(), persons);
//        	
//        	//add Persons, their ids are in addEventMemberships already
//        	model.addPersons(persons);
//        	
//        	//add Groups, the eventid is also important
//        	model.addGroups(groups, event.getId());
//        	
//        	//add GroupMemberships between FUCK
//        	model.addGroupMemberships(persons, groups);
        } else {
        	//update everything
        }
        
    }


	private List<GroupMembership> readFromByteToGroupMembershipList(
			byte[] groupMembershipBytes) {
		// TODO Auto-generated method stub
		return null;
	}


	private List<Group> readFromByteToGroupList(byte[] groupBytes) {
		// TODO Auto-generated method stub
		return null;
	}


	private List<Person> readFromByteToPersonList(byte[] personBytes) {
		// TODO Auto-generated method stub
		return null;
	}


	private List<EventMembership> readFromByteToEventMembershipList(byte[] eventMembershipBytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(eventMembershipBytes);
        DataInputStream in = new DataInputStream(bais);
        String eventMembershipMessage = null;
        try {
			while (in.available() > 0) {
			    eventMembershipMessage = in.readUTF();
			    Log.i("NFCCHECK-EventMembershipMSG", eventMembershipMessage);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        StringTokenizer tokens = new StringTokenizer(eventMembershipMessage, ",");
//        String idToken = tokens.nextToken();
//        String nameToken = tokens.nextToken();
//        String yearToken = tokens.nextToken();
//        String wsToken = tokens.nextToken();
//        String infoToken = tokens.nextToken();
//        
//        Event event = new Event(nameToken, Integer.getInteger(yearToken), Boolean.valueOf(wsToken), infoToken);
//        StringTokenizer idToken = new StringTokenizer(idToken), "=");
        
//        Log.i("NFCEVENTID", id);
//        Log.i("NFCEVENTNAME", name);
//        Log.i("NFCEVENTYEAR", year);
//        Log.i("NFCEVENTWS", ws);
//        Log.i("NFCEVENTINFO", info); 
//        return new Event(name, Integer.getInteger(year), Boolean.valueOf(ws), info);

		return null;
	}


	private Event readFromByteToEvent(byte[] eventBytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(eventBytes);
        DataInputStream in = new DataInputStream(bais);
        String eventMessage = null;
        try {
			while (in.available() > 0) {
			    eventMessage = in.readUTF();
			    Log.i("NFCCHECK-EventMSG", eventMessage);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        StringTokenizer tokens = new StringTokenizer(eventMessage, ",");
        String idToken = tokens.nextToken();
        String nameToken = tokens.nextToken();
        String yearToken = tokens.nextToken();
        String wsToken = tokens.nextToken();
        String infoToken = tokens.nextToken();
        
        Event event = new Event(nameToken, Integer.getInteger(yearToken), Boolean.valueOf(wsToken), infoToken);
//        StringTokenizer idToken = new StringTokenizer(idToken), "=");
        
//        Log.i("NFCEVENTID", id);
//        Log.i("NFCEVENTNAME", name);
//        Log.i("NFCEVENTYEAR", year);
//        Log.i("NFCEVENTWS", ws);
//        Log.i("NFCEVENTINFO", info); 
//        return new Event(name, Integer.getInteger(year), Boolean.valueOf(ws), info);
        return event;
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
