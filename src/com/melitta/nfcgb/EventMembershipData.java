package com.melitta.nfcgb;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;

/**
 * A simple demonstration object we are creating and persisting to the database.
 */
public class EventMembershipData implements Serializable {
	private static final long serialVersionUID = 8900677463451178699L;
	
	// id is generated by the database and set on the object automagically
	@DatabaseField(generatedId = true)
	int id;
	/**
	 * foreign key to event.id
	 */
	// TODO: make it to foreign key
	@DatabaseField
	int event_id;
	/**
	 * foreign key to person.id
	 */
	// TODO: make it to foreign key
	@DatabaseField
	int person_id;
	/**
	 * foreign key to group.id
	 */
	// TODO: make it to foreign key
	@DatabaseField
	int group_id;

	// needed by ormlite
	EventMembershipData() {
	}

	public EventMembershipData(int event_id, int person_id, int group_id) {
		this.event_id = event_id;
		this.person_id = person_id;
		this.group_id = group_id;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(id);
		sb.append(", ").append("event_id=").append(event_id);
		sb.append(", ").append("person_id=").append(person_id);
		sb.append(", ").append("group_id=").append(group_id);
		return sb.toString();
	}
}
