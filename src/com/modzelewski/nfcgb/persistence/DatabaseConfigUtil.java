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
		com.modzelewski.nfcgb.model.EventData.class, 
		com.modzelewski.nfcgb.model.PersonData.class, 
		com.modzelewski.nfcgb.model.EventMembershipData.class, 
		com.modzelewski.nfcgb.model.GroupData.class, 
		com.modzelewski.nfcgb.model.GroupMembershipData.class};

	public static void main(String[] args) throws SQLException, IOException {
		writeConfigFile("ormlite_config.txt", classes);
	}
}
