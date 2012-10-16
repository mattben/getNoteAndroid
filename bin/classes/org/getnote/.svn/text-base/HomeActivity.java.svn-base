package org.getnote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends ListActivity implements OnClickListener {
	
	// These are the global variables, PREFS_ are used for saving and getting, users preference from the system. 
	// I save the users, getNote, user name, password, account status in the MyPrefsFile which is a system file.
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_STATUS = "status";
    private static final String PREF_PASSWORD = "password";
    
    // Setting activity's welcome message
    TextView welcome;
    
    // Instantiating a Datebase helper class 
    private NotesDbAdapter mDbHelper;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     * Called when the activity is first created.    
     * This is the method that is called on start up to create what is
     * seen on the device.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting the page lay from /res/layout/home.xml
        setContentView(R.layout.home);    
        
        // Checking to see if this app has saved user preference for the user.
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
         String username = pref.getString(PREF_USERNAME, null);
        
        if(username == null){
        	username = "guest";
        }
        else{
        	// Most all strings are saved in the /res/values/strings.xml file
        	welcome = (TextView) this.findViewById(R.id.welcometext);
        	welcome.setText(username);
        }    
        
        
        
        // This will instantiate my database helper class, open the database connection and then call the fillData() method.
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        fillData();       
    }
    
    /*
     * Called every time the activity is the active display on the device. 
     * This will request all the note titles from the database and create 
     * a display that is a selectable list
     */
    private void fillData() {
        Cursor c = mDbHelper.fetchAllNotes();
        startManagingCursor(c);

        // A string array with all the note titles
        String[] from = new String[] { NotesDbAdapter.KEY_TITLE };
        
        // A interger array with the ids of the text, auto filled by the system/
        int[] to = new int[] { R.id.text1 };
        
        // Now create an adapter and set it to display using our row,
        // which is made from this page, the cursor of all the note titles,
        // from array and the to array. This is everything required to show a 
        // list of selectable items on the display
        
        SimpleCursorAdapter notes =
            new SimpleCursorAdapter(this, R.layout.notes_row, c, from, to);
        setListAdapter(notes);
    }
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     * Called once the app is running and the user is returning to this activity, 
     * making it the active display again.
     */
    protected void onResume(){
    	super.onResume();
    	
    	// Checking for user saved preferences
    	SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = pref.getString(PREF_USERNAME, null);
       
        // Setting the welcome message
        if(username == null){
        	username = "guest";
        }
    	welcome = (TextView) this.findViewById(R.id.welcometext);
    	welcome.setText(username);    	
    	
    	// This will instantiate my database helper class, open the database connection and then call the fillData() method.
    	mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        fillData();    	   	
    }
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     * Creating the menu you see when you click the menu key on the device
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        
        // This tells the system what the menu should look like and where to find it.
        // I have a menu layout saved under /res/menu/menu.xml
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     * Which menu button was selected and calling the correct method
     */
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case R.id.new_note:
            newNote();
            return true;
        case R.id.account:
        	updateAccount();
        	return true;
        case R.id.sync:	
        	syncNotes();
        	return true;
        case R.id.policies:
        	policies();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }

    }
    
    /*
     * This method will start the policies activity
     */
    private void policies() {
		// Intent is how I tell the system where it is and where I want it to go next	
		Intent i = new Intent(HomeActivity.this, PoliciesActivity.class);
		
		// Tells the system to switch to this activity
		startActivity(i);
		
	}

	/*
     * This method checks what the user has saved for there getNote account information 
     * and weather they are a current member, if so it will call the sync method, and if 
     * not it will suggest to them to create an account 
     */
	private void syncNotes(){
    	SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = pref.getString(PREF_USERNAME, null);
        String status = pref.getString(PREF_STATUS, null);
        
        // Does the user have a valid getNote account
        boolean check = Boolean.parseBoolean(status);

        if (check == true){
        	try {
        		// Valid account then sync
        		sync(username);
        	} 
        	catch (Exception e) {
        		e.printStackTrace();
        	} 
        	catch (Throwable e) {
        		e.printStackTrace();
        	}
        	// Refresh the page
        	fillData();
        }
        else{
        	// Not a valid account, they should create one
        	Toast.makeText(HomeActivity.this, R.string.ToastCreate, Toast.LENGTH_LONG).show();
        }
	}

	/*
	 * This method is my sync service. It will send all the notes on the phone to the server, which will update the 
	 * server with new or updated text notes, and then return the notes that the device need to save and or update
	 */
	private void sync(String username) throws Throwable, IOException {	
		
		BufferedReader in = null;
        try {
        	mDbHelper = new NotesDbAdapter(this);
            mDbHelper.open();
            
            //getting all user notes out of database
            Cursor c = mDbHelper.fetchAllNotes();
            startManagingCursor(c);            
            int totalNotes = c.getCount();  
            String postNotes = "";
            
            if(totalNotes > 0)
            {
            	c.moveToFirst();
            	do{
            		String id = c.getString(0);
            		String title = c.getString(1);
            		String body = c.getString(2);
            		String date = c.getString(3);  
            		
            		String file = title.substring(title.length() -3);
            		
            		//only want txt notes
            		if(file.equals("txt")){
            			//building json string (will do a php explode("[", this)
            			postNotes = postNotes + date + "-" + username + "-" + title + "[" + body + "["; 
            		}
            	}while(c.moveToNext());            	
            }   
            
        	int trim = postNotes.length();
        	
        	if(trim == 0 ){	        		
        		BufferedReader en = null;
                try {
                	// Start set up to ask system to make a http call for us
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    // Where the URL is that I want to call is at ( I wrote this php page as well for this project)
                    request.setURI(new URI("http://www.getnote.org/Android/json.php?user=" + username));
                    // Response is what is returned from the page, in this case a JSON string of notes
                    HttpResponse response = client.execute(request);
                    en = new BufferedReader
                    (new InputStreamReader(response.getEntity().getContent()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    String NL = System.getProperty("line.separator");
                    while ((line = en.readLine()) != null) {
                        sb.append(line + NL);
                    }
                    en.close();
                    String page = sb.toString();
                    if(page.equals("null\n")){
                    	int tmp = 0;
                    }
                    else{
                    	// Now we have the JSON string break it apart in to individual notes and save them
                    	JSONObject object = new JSONObject(page);                   
                    	JSONArray note = object.names();            
                    	JSONArray value = object.toJSONArray(note);            
                    	for(int i = 0; i < value.length(); i++){
                    		String file = note.getString(i);
                    		String[] parts = file.split("-");
                    		String date = parts[0];
                    		String filename = parts[2]+"-"+parts[3]+"-"+parts[4]+"-"+parts[5];
                    		String submitNote = value.getString(i);
                    	
                    		long id = NotesDbAdapter.createNote(filename, submitNote, date);
                    	}
                    }	
                } 
                finally {
                	if (in != null) {
                		try {
                			in.close();
                           	} 
                		catch (IOException e) {
                			e.printStackTrace();
                		}
                	}
                }        	
        	}
        	else{
        		postNotes = postNotes.substring(0, trim - 1);
        		//creating json object to post to server
        		JSONObject jsonObject = new JSONObject();
        		jsonObject.put("sync", postNotes);   
        	
        		// Start set up to ask system to make a http call for us
        		HttpClient client = new DefaultHttpClient();
        		HttpPost request = new HttpPost();
        		// Where the URL is that I want to call is at ( I wrote this php page as well for this project)
        		request.setURI(new URI("http://www.getnote.org/Android/sync.php"));
            
        		//creating post object
        		List<NameValuePair> nVP = new ArrayList<NameValuePair>(2);  
        		nVP.add(new BasicNameValuePair("json", jsonObject.toString()));  
            
        		request.setEntity(new UrlEncodedFormEntity(nVP));
        		// Response is what is returned from the page, in this case a JSON string of notes
        		HttpResponse response = client.execute(request);
        		in = new BufferedReader
        		(new InputStreamReader(response.getEntity().getContent()));
        		StringBuffer sb = new StringBuffer("");
        		String line = "";
        		String NL = System.getProperty("line.separator");
        		
        		while ((line = in.readLine()) != null) {
        			sb.append(line + NL);
        		}
            
        		in.close();
        		//this is what is returned to be saved to device db
        		String page = sb.toString();
        		//Toast.makeText(HomeActivity.this, page , Toast.LENGTH_LONG).show();
        		// Now we have the JSON string break it apart in to individual notes and save them
        		
        		if(page.equals("false\n")){
        			int temp = 0;
        		}
        		else{
        			JSONObject object = new JSONObject(page);                   
        			JSONArray note = object.names();            
        			JSONArray value = object.toJSONArray(note);            
        			for(int i = 0; i < value.length(); i++){
        				String file = note.getString(i);
        				String[] parts = file.split("-");
        				String date = parts[0];
        				String filename = parts[2]+"-"+parts[3]+"-"+parts[4]+"-"+parts[5];
        				String submitNote = value.getString(i);
            	
        				if(!NotesDbAdapter.isTitleSaved(filename)){
        					//false, this is a new note
        					long id = NotesDbAdapter.createNote(filename, submitNote, date);
        				}
        				else{   	//true, this note needs to be updated, first to find its id
        					//getting all user notes out of database
        					Cursor e = mDbHelper.fetchAllNotes();
        					startManagingCursor(e);            
        					int totalN = e.getCount();  
        					String tNotes = "";
        				
        					if(totalNotes > 0){
        						e.moveToFirst();
        						do{
        							long id = e.getLong(0);
        							String title = e.getString(1);                   		 
                    		
        							if(title.equals(filename)){
        								boolean updated = NotesDbAdapter.updateNote(id, filename, submitNote, date);
        							}
        						}while(e.moveToNext());            	
        					}            		
        				}
        			}
        			
        		}         		
        	} 
        	Toast.makeText(HomeActivity.this, R.string.ToastSync , Toast.LENGTH_LONG).show();
        }
        finally {
        	if (in != null) {
        		try {
        			in.close();
                   	} 
        		catch (IOException e) {
        			e.printStackTrace();
        		}
        	}
        } 
	}

	/*
	 * This method is called with the user clicks the account menu button
	 */
	private void updateAccount() {
		// Intent is how I tell the system where it is and where I want it to go next	
		Intent i = new Intent(HomeActivity.this, Login.class);
		
		// Tells the system to switch to this activity
		startActivity(i);
	}

	/*
	 * This method is called when the user clicks the new note menu button 
	 */
	private void newNote() {
		// Intent is how I tell the system where it is and where I want it to go next	
		Intent i = new Intent(HomeActivity.this, SelectionActivity.class);
		
		// Tells the system to switch to this activity
		startActivity(i);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 * This method is called when the user selects on of there notes from the selectable item list
	 */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        // Calls the noteSelected method with the id of the note that was selected
        noteSelected(id);
    }

    /*
     * This method is called once the user has selected a note from the selectable item list
     * and will determine what kind of note was selected and display it correctly for them 
     * along with any other options such as view or delete for photo notes
     */
	private void noteSelected(long id) {
		final long mId = id;
		// Call data base and return the rest of the information for that note
        Cursor c = NotesDbAdapter.fetchNote(id);
        int column = c.getColumnIndex("title");
        String filename = c.getString(column);
        int where = c.getColumnIndex("body");
        final String submitNote = c.getString(where);              
        
        String file = filename.substring(filename.length() -3);
       
        if(file.equals("txt")){
        	// The note selected was a text note and we need to start the create note activity,
        	// So the user can view, update or delete this note
        	Intent i = new Intent(HomeActivity.this, CreateNoteActivity.class);
        	i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        	startActivity(i);
        }
        else{
        	// The note selected was a photo note, we need to ask if the user wants to view or delete this note
        	
        	// Alert Dialog will create and call a pop up asking the user if they want to view of delete this note
            final AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
            alertDialog.setTitle(R.string.AlerDialogPhotoNote);
            alertDialog.setMessage("please choose");
            alertDialog.setButton("View", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	// If they select view we ask the system to start the activity that will show a image
                	Intent intent = new Intent();
                	intent.setAction(Intent.ACTION_VIEW);
                	intent.setDataAndType(Uri.parse(submitNote), "image/*");
                	startActivity(intent);
           
              } });
            alertDialog.setButton2("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	// They selected delete so close this dialog and open another asking if the are sure
                	alertDialog.cancel();
                    final AlertDialog alertDialog1 = new AlertDialog.Builder(HomeActivity.this).create();
                    alertDialog1.setTitle(R.string.AlerDialogPhotoNote);
                    alertDialog1.setMessage("are you sure you want to delete");
                    alertDialog1.setButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        	// They selected ok, call delete in the database class, and then inform the user is is deleted
                        	boolean del = NotesDbAdapter.deleteNote(mId);
                        	Toast.makeText(HomeActivity.this, "photo has been deleted", Toast.LENGTH_SHORT).show();
                        	// Refresh the screen
                        	fillData();            	               
                      } });
                    alertDialog1.setButton2("cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        	// They selected cancel, close the dialog
                        	alertDialog1.cancel();
                        	
                      } });
                    // Display second dialog (are they sure)
                	alertDialog1.show();
              } });
            
            // Display first dialog (what do they want to do)
        	alertDialog.show();

        	}
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * This method must be here, I don't have a button that gets clicked, but the selectable item list
	 * still requires it to be here
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}	
}
