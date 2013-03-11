package com.modzelewski.nfcgb.model;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * A simple demonstration object we are creating and persisting to the database.
 * @author Georg
 */
public class Person implements Serializable {
	private static final long serialVersionUID = 2074439586321283138L;

	// id is generated by the database and set on the object automagically
	@DatabaseField(generatedId = true)
	private
	int id;
	/**
	 * foreign key to event.id
	 */
	// TODO: make it to foreign key
//	@DatabaseField
//	int event_id;
	@DatabaseField (index = true)
	private
	String name;
	@DatabaseField
	private
	String email;

	// needed by ormlite
	Person() {
	}	

	public Person(String name, String email) {
		this.setName(name);
		this.setEmail(email);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(getId());
		sb.append(", ").append("name=").append(getName());
		sb.append(", ").append("email=").append(getEmail());
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}