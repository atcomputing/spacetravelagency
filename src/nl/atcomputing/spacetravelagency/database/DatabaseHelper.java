package nl.atcomputing.spacetravelagency.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "Destinations";

	public static final class DepartureInfo implements BaseColumns {
		private DepartureInfo() {}

		public static final String TABLE_NAME = "DepartureInfo";
		public static final String ORDER_ID = "orderId";
		public static final String DEPARTURE_TIME = "departureTime";
		public static final String DEPARTURE_LOCATION = "departureLocation";
	}

	public static final class Destinations 
	implements BaseColumns {
		private Destinations() {}

		public static final String TABLE_NAME = "Bookings";
		public static final String STAR_SYSTEM = "starSystem";
		public static final String DESTINATION = "destination";
		public static final String DESTINATION_ID = "destinationid";
		public static final String DISTANCE = "distance";
		public static final String PRICE = "price";
	}

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	private static final String CREATE_TABLE_DESTINATIONS = "CREATE TABLE IF NOT EXISTS " +
			Destinations.TABLE_NAME + " ("
			+ Destinations._ID + " INTEGER PRIMARY KEY,"
			+ Destinations.STAR_SYSTEM + " TEXT,"
			+ Destinations.DESTINATION + " TEXT,"
			+ Destinations.DESTINATION_ID + " INTEGER,"
			+ Destinations.DISTANCE + " INTEGER,"
			+ Destinations.PRICE + " INTEGER"
			+ ");";

	private static final String CREATE_TABLE_DEPARTUREINFO = "CREATE TABLE IF NOT EXISTS " +
			DepartureInfo.TABLE_NAME + " ("
			+ DepartureInfo._ID + " INTEGER PRIMARY KEY,"
			+ DepartureInfo.ORDER_ID + " TEXT,"
			+ DepartureInfo.DEPARTURE_TIME + " TEXT,"
			+ DepartureInfo.DEPARTURE_LOCATION + " TEXT"
			+ ");";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_DEPARTUREINFO);
		db.execSQL(CREATE_TABLE_DESTINATIONS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if( oldVersion > newVersion ) {
			onDowngrade(db, oldVersion, newVersion);
		} else {
			for( int i = oldVersion; i < newVersion; i++ ) {
				switch( i )
				{
				case 1: //Going from version 1 to 2
					db.execSQL("DROP TABLE IF EXISTS " + Destinations.TABLE_NAME);
					db.execSQL(CREATE_TABLE_DESTINATIONS);
					break;
				case 2: //Going from version 2 to 3
					db.execSQL(CREATE_TABLE_DEPARTUREINFO);
					break;
				}
			}
		}
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for( int i = oldVersion; i > newVersion; i-- ) {
			switch( i ) {
			case 2: //Going from version 2 to 1
				//Nothing to be done
				break;
			case 3:  //Going from version 3 to 2
				db.execSQL("DROP TABLE IF EXISTS " + DepartureInfo.TABLE_NAME);
				break;
			}
		}
	}


}