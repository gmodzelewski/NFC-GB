package com.modzelewski.nfcgb.nfc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.util.Log;
import android.widget.Toast;

import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.model.Event;
import com.modzelewski.nfcgb.model.EventMembership;
import com.modzelewski.nfcgb.model.Group;
import com.modzelewski.nfcgb.model.GroupMembership;
import com.modzelewski.nfcgb.model.Person;

public class NfcCheck {
    private final Context context;
    private final NfcAdapter nfcAdapter;

    public NfcCheck(NfcAdapter nfcAdapter, Context context) {
        this.context = context;
        this.nfcAdapter = nfcAdapter;
    }

	public void check(BackgroundModel model) {
        if (nfcAdapter == null) {
            Toast.makeText(context, context.getResources().getString(R.string.nfc_not_available), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.nfc_available), Toast.LENGTH_LONG).show();

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
            NdefRecord eventMembershipRecord = NdefRecord.createExternal("com.modzelewski.nfcgb.nfc", "EVENTMEMBERSHIP", eventMembershipBytes);
            NdefRecord personRecord = NdefRecord.createExternal("com.modzelewski.nfcgb.nfc", "PERSON", personBytes);
            NdefRecord groupRecord = NdefRecord.createExternal("com.modzelewski.nfcgb.nfc", "GROUPS", groupBytes);
            NdefRecord groupMembershipRecord = NdefRecord.createExternal("com.modzelewski.nfcgb.nfc", "GROUPMEMBERSHIP", groupMembershipBytes);
            
            NdefMessage msg = new NdefMessage(eventRecord, eventMembershipRecord, personRecord, groupRecord, groupMembershipRecord);
            //return msg
            
            //process intent
            NdefRecord[] ndefRecords = msg.getRecords();
            
            /*	##########################################
             * 			EVENT
             *	##########################################
             */
            byte[] bytes = ndefRecords[0].getPayload();
            // read from byte array
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            DataInputStream in = new DataInputStream(bais);
            String eventString = null;
            try {
				while (in.available() > 0) {
				    eventString = in.readUTF();
//				    Group aGroup = element;
//				    Log.i("NFCCHECK", eventString);
//                System.out.println(element);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            StringTokenizer tokens = new StringTokenizer(eventString, ",");
//            Log.i("NFCCHECKTOKENSID", tokens.nextToken());
            String eventName = substringAfter(tokens.nextToken(), "eventname=");
            String year = substringAfter(tokens.nextToken(), "year=");
            String wintersemester = substringAfter(tokens.nextToken(), "wintersemester=");
            String info = substringAfter(tokens.nextToken(), "info=");
            
//            Log.i("EVENTNAME", eventName);
//            Log.i("YEAR", year);
//            Log.i("WS", wintersemester);
//            Log.i("INFO", info);

            
            /*	##########################################
             * 			EVENTMEMBERSHIPS
             *	##########################################
             */
            byte[] eventMembershipBytes2 = ndefRecords[1].getPayload();
            // read from byte array
            ByteArrayInputStream eventMembershipBais = new ByteArrayInputStream(eventMembershipBytes2);
            DataInputStream eventMembershipIn = new DataInputStream(eventMembershipBais);
            String eventMembershipString = null;
            try {
            	while (eventMembershipIn.available() > 0) {
            		eventMembershipString = eventMembershipIn.readUTF();
//				    Group aGroup = element;
            		Log.i("NFCCHECK-EMS", eventMembershipString);
            		StringTokenizer tokens2 = new StringTokenizer(eventMembershipString, ",");
                  Log.i("NFCCHECKTOKENSID", tokens2.nextToken());
                  String event_id = substringAfter(tokens2.nextToken(), "event_id=");
                  String person_id = substringAfter(tokens2.nextToken(), "person_id=");
                  Log.i("EVENTID= ", event_id);
                  Log.i("personID= ", person_id);
            	
            	}
            } catch (IOException e) {
            	// TODO Auto-generated catch block
            	e.printStackTrace();
            }
            
            
//            StringTokenizer tokens = new StringTokenizer(eventString, ",");
//            Log.i("NFCCHECKTOKENSID", tokens.nextToken());
//            String eventName = substringAfter(tokens.nextToken(), "eventname=");
//            String year = substringAfter(tokens.nextToken(), "year=");
//            String wintersemester = substringAfter(tokens.nextToken(), "wintersemester=");
//            String info = substringAfter(tokens.nextToken(), "info=");
//            
//            Log.i("EVENTNAME", eventName);
//            Log.i("YEAR", year);
//            Log.i("WS", wintersemester);
//            Log.i("INFO", info);
//            
            
//            StringTokenizer tokensS = new StringTokenizer(tokens.nextToken(), "=");
//            tokensS.nextToken();
//            
//            String idString = tokensS.nextToken();
//            tokensS = new StringTokenizer(tokens.nextToken(), "=");
//            tokensS.nextToken();
//            
//            String eventNameString = tokensS.nextToken();
//            tokensS = new StringTokenizer(tokens.nextToken(), "=");
//            Log.i(NFCCHECKTOKENS, tokensS.nextToken());
//                        
//            String yearString = tokens.nextToken();
//            tokensS = new StringTokenizer(tokens.nextToken(), "=");
//            tokensS.nextToken();
//            
//            String wintersemesterString = tokens.nextToken();
//            tokensS = new StringTokenizer(tokens.nextToken(), "=");
//            tokensS.nextToken();
//            
//            String infoString = tokens.nextToken();
//            tokensS = new StringTokenizer(tokens.nextToken(), "=");
//            
//            Log.i("NFCEVENTID", idString);
//            Log.i("NFCEVENTNAME", eventNameString);
//            Log.i("NFCEVENTYEAR", yearString);
//            Log.i("NFCEVENTWS", wintersemesterString);
//            Log.i("NFCEVENTINFO", infoString);
            
            
            
            
            
//        	public Group fromString(String element) {
//        		super();
//        		StringTokenizer tokens = new StringTokenizer(element, ",");
//        		Group g = new Gr
//        		this.id = Integer.getInteger(tokens.nextToken());
//        	}
            
            
//            if (!groups.isEmpty())
//                Toast.makeText(context, "Groups ist nicht leer", Toast.LENGTH_LONG).show();
//            
//            Log.i("NFC", "personsrep = " + persons.toString());
//            LinkedList<GroupMembership> groupMemberships = (LinkedList<GroupMembership>) model.getGroupMemberships(currentEvent);
//            LinkedList<EventMembership> eventMemberships = (LinkedList<EventMembership>) model.getEventMemberships(currentEvent);
//            LinkedList<Group> groups = (LinkedList<Group>) model.getGroups(currentEvent);
//            LinkedList<Person> persons = (LinkedList<Person>) model.getPersons(eventMemberships);

//            Nfc nfc = new Nfc(nfcAdapter, context, model);
//            
//            Gson gson = new Gson();
//            LinkedList<Person> persons = (LinkedList<Person>) model.getPersons();
//            String payload = gson.toJson(persons);
//            
//            NdefRecord ndefRecord = nfc.createTextRecord("hallo", Locale.GERMAN, true);
//            Log.i("NFC", "record = " + ndefRecord.toString());
//            NdefMessage msg = new NdefMessage(ndefRecord);
//            Log.i("NFC", "records = " + msg.getRecords().toString());
//            NdefRecord[] records = msg.getRecords();
//            String data = records[0].getPayload().toString();
//            
//            
//            NdefRecord mimeRecord = NdefRecord.createMime("application/vnd.com.example.android.beam",
//            	    payload.getBytes(Charset.forName("US-ASCII")));
//            Log.i("NFC", "mimeRecord = " + mimeRecord.toString());
//            
//            
////            NdefRecord mimeRecord = new NdefRecord(
////            	    NdefRecord.TNF_MIME_MEDIA ,
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
	/**
	   * Returns the substring after the first occurrence of a delimiter. The
	   * delimiter is not part of the result.
	   * @param string    String to get a substring from.
	   * @param delimiter String to search for.
	   * @return          Substring after the last occurrence of the delimiter.
	   */
	  public static String substringAfter( String string, String delimiter )
	  {
	    int pos = string.indexOf( delimiter );

	    return pos >= 0 ? string.substring( pos + delimiter.length() ) : "";
	  }
}
