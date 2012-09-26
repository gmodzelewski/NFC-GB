package com.melitta.nfcgb.persistence;

import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.melitta.nfcgb.EventData;
import com.melitta.nfcgb.EventMembershipData;
import com.melitta.nfcgb.GroupData;
import com.melitta.nfcgb.PersonData;

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
