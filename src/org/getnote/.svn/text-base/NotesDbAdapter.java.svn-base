package org.getnote;

import java.sql.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotesDbAdapter {
	// These are the global variables, KEY_s are used for each attribute I will be saving in the table

	public static final String KEY_DATE = "date";
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private static SQLiteDatabase mDb;

    // If there is not already a getNote database on the device, this will be used to create one
    private static final String DATABASE_CREATE =
        "create table notes (_id integer primary key autoincrement, "
        + "title text not null, body text not null, date text not null);";

    // Naming the datebase, and the table
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    /*
     * User the systems datebase helper class to create a datebase if needed
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        
        /*
         * (non-Javadoc)
         * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
         * systems create method for creating a new database
         */
        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }
        
        /*
         * (non-Javadoc)
         * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
         * This is a system method for updating the datebase if the users device has updated it SQLite version
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    /*
     * This method is setting up datebase adapter, so we can use said database
     */
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /*
     * This method is calling open on the database
     */
    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /*
     * This method calls close on the datebase
     */
    public void close() {
        mDbHelper.close();
    }
   
    /*
     * This method is the SQLite query for saving a new note
     */
    public static long createNote(String title, String body, String date) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_DATE, date);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    /*
     * This method is the SQLite query for deleting a note
     */
    public static boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /*
     * This method is the SQLite query that will return all the notes in the database
     */
    public Cursor fetchAllNotes() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_BODY, KEY_DATE}, null, null, null, null, null);
    }
    
    /*
     * This method is the SQLite query for returning a note, if the id is know
     */
    public static Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_TITLE, KEY_BODY, KEY_DATE}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    /*
     * This method is the SQLite query for updating an existing note
     */
    public static boolean updateNote(long rowId, String title, String body, String date) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        args.put(KEY_DATE, date);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }   
    
    /*
     * This method is called to make sure a note's filename the user is trying to currently save is not stored
     */
    public static boolean isTitleSaved(String title){    	
    	Cursor mCursor = null;
    	try{
    		mCursor = mDb.query(false, DATABASE_TABLE,     	
    					new String[]{KEY_TITLE}, KEY_TITLE + "=?",
    					new String[]{title}, null, null, null, null);   
    		//mCursor = mDb.
    		
    		while (mCursor.moveToNext()){
    			return true;
    		} 
    	}
    	catch (SQLException sqle){
    		Log.e(TAG, sqle.toString());
    		
    	}
    	finally{
    		if(mCursor != null && mCursor.isClosed()){
    			mCursor.close();
    	}
    }
		return false;
}
}
