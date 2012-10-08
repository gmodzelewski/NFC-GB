package com.modzelewski.nfcgb.persistence;

import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.modzelewski.nfcgb.EventData;
import com.modzelewski.nfcgb.EventMembershipData;
import com.modzelewski.nfcgb.GroupData;
import com.modzelewski.nfcgb.PersonData;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
	public static final Class<?>[] classes = new Class[] { EventData.class, PersonData.class, EventMembershipData.class, GroupData.class};

	public static void main(String[] args) throws SQLException, IOException {
		writeConfigFile("ormlite_config.txt", classes);
	}
}
