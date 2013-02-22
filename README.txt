GroupBuilding App for Android

An Android app where you can drag'n'drop personal information to groups in specified events and sync them via Near Field Communication.

If you checkout and import you need to remove the Android Bootstrap Entry from the classpath of DatabaseConfigUtil.
1. Go to the Run Configurations of the DatabaseUtil class
2. Choose "Classpath"
3. Remove Android from the Bootstrap Entry. The Bootstrap Entry should be empty.
4. Done.