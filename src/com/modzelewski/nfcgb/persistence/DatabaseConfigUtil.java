package com.modzelewski.nfcgb.persistence;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
	public static final Class<?>[] classes = new Class[] { 
		com.modzelewski.nfcgb.model.Event.class, 
		com.modzelewski.nfcgb.model.Person.class, 
		com.modzelewski.nfcgb.model.EventMembership.class, 
		com.modzelewski.nfcgb.model.Group.class, 
		com.modzelewski.nfcgb.model.GroupMembership.class};

	public static void main(String[] args) throws SQLException, IOException {
		writeConfigFile("ormlite_config.txt", classes);
	}
}
