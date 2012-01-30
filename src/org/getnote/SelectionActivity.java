package org.getnote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

public class SelectionActivity extends Activity implements OnClickListener, AdapterView.OnItemSelectedListener{
	
	// These are the global variables, PREFS_ are used for saving and getting, users preference from the system. 
	// I save the users, getNote, user name, password, account status in the MyPrefsFile which is a system file.
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_FILENAME = "filename";

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    private static final String PREF_PASSWORD = "password";
    private String username = "";
    private String date = "";
    private String course = "";
    private String type = "";
    protected String filename="";
    private File values;
    private NotesDbAdapter mDbHelper;
    
    String[] items= {"CSCI000 - Personal Notes",
    				"CSCI101 - Introduction to Computer Science",
    				"CSCI102 - Living With Technology",
    				"CSCI111 - Programming and Algorithms I",
				    "CSCI144 - Introduction to UNIX/Linux",
				    "CSCI211 - Programming and Algorithms II",
				    "CSCI221 - Assembly Language Programming",
				    "CSCI301 - Computers Impact on Society",
				    "CSCI311 - Algorithms and Data Structures",
				    "CSCI313 - Mind in the Machine - Honors",
				    "CSCI315 - Programming Languages",
				    "CSCI317 - Linear Programming Applications",
				    "CSCI320 - Computer Architecture",
				    "CSCI340 - Operating Systems",
				    "CSCI344 - Shell Programming",
				    "CSCI346 - Introduction to Computer Networks and Network Mana",
				    "CSCI351 - Numerical Methods Programming",
				    "CSCI380 - Machines, Brains, and Minds",
				    "CSCI381 - Language, Intelligence, and Computation",
				    "CSCI389 - Industry Internship",
				    "CSCI400 - Computer Science Activity",
				    "CSCI430 - Software Engineering 1",
				    "CSCI431 - Software Engineering 2",
				    "CSCI444 - Fundamental UNIX System Administration",
				    "CSCI465 - Web Programming Fundamentals",
				    "CSCI490 - Directed Programming Experience",
				    "CSCI498 - Topics in Computer Science",
				    "CSCI499 - Honors Research Project/Thesis",
				    "CSCI511 - Object-Oriented Programming",
				    "CSCI515 - Compiler Design",
				    "CSCI533 - Object-Oriented Analysis & Design",
				    "CSCI540 - Systems Programming",
				    "CSCI546 - Advanced Network Management",
				    "CSCI547 - Advanced Computer Networks",
				    "CSCI550 - Theory of Computing",
				    "CSCI566 - Computer Graphics Programming",
				    "CSCI567 - Graphical User Interfaces",
				    "CSCI568 - Digital Image Processing",
				    "CSCI569 - Advanced Computer Graphics",
				    "CSCI580 - Artificial Intelligence",
				    "CSCI583 - Expert Systems and Applications",
				    "CSCI585 - Robotics and Machine Intelligence",
				    "CSCI598 - Advanced Topics in Computer Science",
				    "CSCI611 - Distributed Computing",
				    "CSCI619 - Topics in Programming Language Theory",
				    "CSCI620 - Computer Architecture",
				    "CSCI629 - Topics in Computer Architecture",
				    "CSCI630 - Software Engineering - Grad",
				    "CSCI639 - Topics in Software Engineering - Grad",
				    "CSCI640 - Operating Systems - Grad",
				    "CSCI649 - Topics in Networking",
				    "CSCI650 - Design and Analysis of Algorithms",
				    "CSCI659 - Topics in Computer Theory",
				    "CSCI669 - Topics in Computer Graphics",
				    "CSCI679 - Topics in Database Systems",
				    "CSCI682 - Topics in Artificial Intelligence",
				    "CSCI693 - Research Methods in Computer Science",
				    "CSCI697 - Independent Study",
				    "CSCI698 - Seminar in Advanced Topics",
				    "CSCI699 - Masters Project",
				    "CSCI700 - Masters Thesis"};
    
    String[] types = {	"lecture",
				    	"discussion",
				    	"activity",
				    	"lab",
				    	"study_group",
				    	"home_work" };

    
	Button buttonTextNote;
	Button buttonPixNote;
	Spinner courseSpinner;
	Spinner typeSpinner;
	
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
        setContentView(R.layout.input);
        //setting up the page spinner are the selection wheels for class and type
        courseSpinner = (Spinner) findViewById(R.id.course);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.courses, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setOnItemSelectedListener(this); 
        courseSpinner.setAdapter(adapter);
        
        
        typeSpinner = (Spinner) findViewById(R.id.type);
        ArrayAdapter<CharSequence> adaptor = ArrayAdapter.createFromResource(this, R.array.types, android.R.layout.simple_spinner_item);
        adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setOnItemSelectedListener(this);
        typeSpinner.setAdapter(adaptor);
        
        buttonTextNote = (Button)findViewById(R.id.buttonTextNote);
        buttonPixNote = (Button)findViewById(R.id.buttonPixNote);
        
        buttonTextNote.setOnClickListener(this);
        buttonPixNote.setOnClickListener(this);
        
        
    }
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     * Called to find out what button the user clicked
     */
	@Override
	public void onClick(View arg0) {
		// mharris23@mail.csuchico.edu-2011.9.26-CSCI490-lecture-.txt
		// user-date-course-type-.file
		
		 // Checking to see if this app has saved user preference for the user.
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor e = settings.edit();
		
		e.putString("filename", filename);
		e.commit();
        
        DatePicker lecdate = (DatePicker)findViewById(R.id.lectureDate);
        String day = Integer.toString(lecdate.getDayOfMonth());
        String month = Integer.toString(lecdate.getMonth() + 1);
        String year = Integer.toString(lecdate.getYear());
        date = year+"."+month+"."+day;
        filename = date+"-"+course+"-"+type;
        // filename = username+"-"+date+"-"+course+"-"+type+"-.txt";      
        
        
		switch (arg0.getId()){
    	case R.id.buttonTextNote:
    			// They clicked text note
    			takeTextNote(filename);
        	break;
		case R.id.buttonPixNote:
			 // They clicked photo note pop up dialog asking camera or images
			 final AlertDialog alertDialog = new AlertDialog.Builder(SelectionActivity.this).create();
	            alertDialog.setTitle(R.string.AlerDialogPhotoNote);
	            alertDialog.setMessage("add photo");
	            alertDialog.setButton("camera", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                	// They clicked camera
	                	takePhotoNote(filename);
	              } });
	            alertDialog.setButton2("images", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// They clicked images
						getPhotoNote(filename);						
					} });
	            
	        	alertDialog.show();
			break;
		}		
	}
	
	/*
	 * This method ask the system what apps can show the user their images and to start that activity
	 */
	private void getPhotoNote(String filename){
		filename = filename + "-.jpg";	
		if(!NotesDbAdapter.isTitleSaved(filename)){
			// false= then we can save this note, the filename is unique
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor e = settings.edit();
	
			e.putString("filename", filename);
			e.commit();
		
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			
			// Setting up my return code as 17
			startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), 17);
		}
		else{
    		// true=then this note is in DB already
    		Toast.makeText(SelectionActivity.this, R.string.ToastNotUnique, Toast.LENGTH_LONG).show();
    	}
	}
	
	/*
	 * This method ask the system what apps can take pitchers and to start that activity
	 */
	private void takePhotoNote(String filename){
		filename = filename + "-.jpg";	
		if(!NotesDbAdapter.isTitleSaved(filename)){
    		// false= then we can save this note, the filename is unique
			
			File f = new File(Environment.getExternalStorageDirectory() + "/getNote");
		
			if(!f.exists()){
				Toast.makeText(this, f.toString(), Toast.LENGTH_SHORT).show();
				f.mkdir();	
			}

			values = new File (Environment.getExternalStorageDirectory() + "/getNote/" + filename);	
		
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor e = settings.edit();
		
			e.putString("filename", filename);
			e.commit();
		
			Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE );
			
			// Setting up my return code as 15
			cam.putExtra( MediaStore.EXTRA_OUTPUT, Uri.fromFile(values) );
			startActivityForResult(cam, 15);		
		}
		else{
    		// true=then this note is in DB already
    		Toast.makeText(SelectionActivity.this, R.string.ToastNotUnique, Toast.LENGTH_LONG).show();
    	}
	}
	
	/*
	 * This method will call the CreateNoteActivity so the user can take a new note
	 */
	private void takeTextNote(String filename) {
		filename = filename + "-.txt";
    	if(!NotesDbAdapter.isTitleSaved(filename)){
    		// false= then we can save this note, the filename is unique
        
    		Intent i = new Intent(SelectionActivity.this, CreateNoteActivity.class);
			Bundle b = new Bundle();
			b.putString("filename", filename);
			i.putExtras(b);
    		startActivity(i);
    		finish();
    	}
    	else{
    		// true=then this note is in DB already
    		Toast.makeText(SelectionActivity.this, R.string.ToastNotUnique, Toast.LENGTH_LONG).show();
    	}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 * This method is called when returning from the systems camera or image activity
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		int result = resultCode;
		int request = requestCode;
		String submitNote = "";
		
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String filename = pref.getString(PREF_FILENAME, null);
		
		//15 was the request code for returning from the system camera activity
		if (request == 15){
			if (result == -1){
				
				File path =  new File (Environment.getExternalStorageDirectory() + "/getNote/" + filename);
				submitNote = "file://" + path.toString();
			}
			else{
				Toast.makeText(SelectionActivity.this, R.string.ToastPhotoNotSaved , Toast.LENGTH_LONG).show();
			}	
		}
		// 17 was the request code from returing from the system image activity
		else if (request == 17){
			if(result == -1){
				Uri choose = data.getData();
				submitNote = choose.toString();
			}
			else{
				Toast.makeText(SelectionActivity.this, R.string.ToastPhotoNotSaved , Toast.LENGTH_LONG).show();
			}
		}
		if(result == -1){
			mDbHelper = new NotesDbAdapter(this);
			mDbHelper.open();
			
			String date = Integer.toString(Calendar.DATE);
			
			long id = NotesDbAdapter.createNote(filename, submitNote, date);
			Toast.makeText(SelectionActivity.this, R.string.ToastPhotoNoteSaved, Toast.LENGTH_SHORT).show();
		}
		finish();
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
	 * This method is called when the user selects a class and type
	 */
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		switch (arg0.getId()){
		case R.id.course:
			course = items[arg2];
			course=course.substring(0, course.indexOf(" "));
			break;
		case R.id.type:
			type = types[arg2];
			break;
		}	
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
	 * This is called if the user does not seleced anything which implies they wanted the first thing in the list
	 */
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		switch (arg0.getId()){
		case R.id.course:
			course = items[0];
			//Changed this was
			//course=course
			//????
			course=course.substring(0, course.indexOf(" "));
			break;
		case R.id.type:
			type = types[0];
			break;
		}
	}

}
