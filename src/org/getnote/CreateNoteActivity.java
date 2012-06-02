package org.getnote;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateNoteActivity extends Activity implements OnClickListener{
	
	// These are the global variables
	TextView fileNameHeader;
	EditText editSubmitNote;
	Button buttonSubmitNote;
	Button buttonDeleteNote;
	String filename;
	String body;
	boolean repeat;
	private Long mRowId;
	
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
      // Setting the page lay from /res/layout/submit.xml 
        setContentView(R.layout.submit);
        
         editSubmitNote = (EditText)findViewById(R.id.editSubmitNote);
         buttonSubmitNote = (Button)findViewById(R.id.buttonSubmitNote);
         buttonDeleteNote = (Button)findViewById(R.id.buttonDeleteNote);
         fileNameHeader = (TextView)findViewById(R.id.textViewFilename);
         
         buttonSubmitNote.setOnClickListener(this);
         buttonDeleteNote.setOnClickListener(this);
         
         // Bundle is the things that where passed from the previous activity to this one
         Bundle b = getIntent().getExtras();
         filename = b.getString("filename");
         
         fileNameHeader.setText(filename);
         
         mRowId = null;
         Bundle extras = getIntent().getExtras();           
         
         if (extras.getLong(NotesDbAdapter.KEY_ROWID) != 0) {
        	 // If there is a filename and filebody in the extras then set the page with that information
        	 Long mId = extras.getLong(NotesDbAdapter.KEY_ROWID);
        	 Cursor update = NotesDbAdapter.fetchNote(mId);
        	 mRowId = update.getLong(0);
             filename = update.getString(1);            
             body = update.getString(2);             
             
             if (filename != null){
            	 fileNameHeader.setText(filename);
             }
             if (body != null) {
            	 editSubmitNote.setText(Html.fromHtml(body));
             }
             repeat = true;
         }
         else{
        	 repeat = false;
         }
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     * Depending what button is pressed this will determine what method will be called
     */
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()){
		case R.id.buttonSubmitNote:		
			// User is trying to save a note
			String submitNote = editSubmitNote.getText().toString();			
			
			Calendar c=new GregorianCalendar();
			Date dateD = new Date(c.getTimeInMillis());
			String date = new SimpleDateFormat("yyyy.MM.dd").format(dateD);
			
			if(!submitNote.equals("")){
				if (!repeat){		
					// This was a new note, so note was saved
					long id = NotesDbAdapter.createNote(filename, submitNote, date);
					Toast.makeText(CreateNoteActivity.this, R.string.ToastSaved, Toast.LENGTH_SHORT).show();
				}
				else{
					// A note that has been updated
					boolean updated = NotesDbAdapter.updateNote(mRowId, filename, submitNote, date);
					Toast.makeText(CreateNoteActivity.this, R.string.ToastUpdated, Toast.LENGTH_SHORT).show();
					//Toast.makeText(CreateNoteActivity.this, date, Toast.LENGTH_SHORT).show();
				}
				// This method call tells the system we are done with this activity, and to return to the previous one
				finish();
			}
			else{
				// User can not save a empty note
				Toast.makeText(CreateNoteActivity.this, R.string.ToastEmptyNote, Toast.LENGTH_SHORT).show();
			}			
			break;		
		case R.id.buttonDeleteNote:
			// User is trying to delete a note, show the dialog pop up to make sure they really want to delete
            final AlertDialog alertDialog = new AlertDialog.Builder(CreateNoteActivity.this).create();
            alertDialog.setTitle(R.string.AlerDialogPhotoNote);
            
            // Why I didn't save this next string in the /res/values/strings.xml and then call
            // AlertDialog.setMessage("R.string.DialogDeleted"); this 
            // Can't be used with newest API setMessage must take a char array not a pointer (int)
            alertDialog.setMessage("are you sure you want to delete");
            alertDialog.setButton("ok", new DialogInterface.OnClickListener() {
            	// They click ok, then delete and let them know
                public void onClick(DialogInterface dialog, int which) {
       			 Bundle extras = getIntent().getExtras();			 
    	         if (extras.getLong(NotesDbAdapter.KEY_ROWID) != 0) {
    	        	 Long mId = extras.getLong(NotesDbAdapter.KEY_ROWID);
    	        	 boolean del = NotesDbAdapter.deleteNote(mId);
    	        	 Toast.makeText(CreateNoteActivity.this, R.string.ToastDeleted, Toast.LENGTH_SHORT).show();	        	 
    	         }
    	         else{
    	        	 Toast.makeText(CreateNoteActivity.this, R.string.ToastNot, Toast.LENGTH_SHORT).show();	        	 
    	         }
    	       // This method call tells the system we are done with this activity, and to return to the previous one
    	         finish();           
              } });
            alertDialog.setButton2("cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	alertDialog.cancel();
                	
              } });
            
        	alertDialog.show();
	        break;
		}
	}
}
