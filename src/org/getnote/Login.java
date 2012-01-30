package org.getnote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener {
	// These are the global variables, PREFS_ are used for saving and getting, users preference from the system. 
	// I save the users, getNote, user name, password, account status in the MyPrefsFile which is a system file.
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_STATUS = "status";
    private static boolean FIRST = false;
	
	EditText username;
	EditText password;
	Button submit;
	
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
        setContentView(R.layout.login); 

        // Checking if user, already saved account information
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String email = pref.getString(PREF_USERNAME, null);
        String pass = pref.getString(PREF_PASSWORD,null);
        String status = pref.getString(PREF_STATUS, null);
        
        
        if(email != null){
        	username = (EditText) this.findViewById(R.id.editTextUsername);
        	username.setText(email);
        	
        	if(status.equals("false")){
        		FIRST = false;
        	}
        	else{
        		FIRST = true;
        	}
        }
        if(pass != null){
        	password = (EditText) this.findViewById(R.id.editTextPassword);
        	password.setText(pass);
        }
        
        submit = (Button) this.findViewById(R.id.buttonSubmitLogin);
        submit.setOnClickListener(this);
    }    

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     * This method is called when the user clicks the submite button
     */
	@Override
	public void onClick(View v) {
        username = (EditText) this.findViewById(R.id.editTextUsername);
        password = (EditText) this.findViewById(R.id.editTextPassword);
        String un = username.getText().toString();
        String pw = password.getText().toString(); 
        
        // User can't leave the username field empty
        if(un.equals("")){
			Toast.makeText(Login.this, "No username entered", Toast.LENGTH_LONG).show();
		}		
        // User can't leave the password field empty
        else if (pw.equals("")){
			Toast.makeText(Login.this, "No password entered", Toast.LENGTH_LONG).show();
		}
		else{		
			boolean check = false;
			
			// Call isUser to check if this account info is a valid getNote user
			try {
				check = isUser(un, pw);
			} 
			catch (Exception e1) {
				e1.printStackTrace();
			} 			
			
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor e = settings.edit();
			
			e.putString("username", un);
			e.putString("password", pw);
			e.putString("status", Boolean.toString(check));
			e.commit();
			
			if (check == true && !FIRST){
				Toast.makeText(Login.this, "Account info saved - Valid User - Text Notes Downloaded", Toast.LENGTH_LONG).show();
				try {
					// They are a valid user, lets call getNote!
					getnote(un);
				} 
				catch (Exception e1) {					
					e1.printStackTrace();
				}
			}
			else if(FIRST){
				Toast.makeText(Login.this, R.string.ToastGoodAcc, Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(Login.this, R.string.ToastBadAcc, Toast.LENGTH_LONG).show();
			}	
			// This method call tells the system we are done with this activity, and to return to the previous one
			finish();
		}
	}

	/*
	 * This method is what the application is all about, getting the users notes off the server and saving them to 
	 * there device this only happens the first time you save an account on the device, as to prevent a user having
	 * all there friends sign in back to back, to store all their friends notes on there device. At this time only 
	 * the users notes can be stored on the phone to prevent cheating.
	 */
	private void getnote(String un) throws URISyntaxException, ClientProtocolException, IOException, JSONException {
		String username = un;
		BufferedReader in = null;
        try {
        	// Start set up to ask system to make a http call for us
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            // Where the URL is that I want to call is at ( I wrote this php page as well for this project)
            request.setURI(new URI("http://www.getnote.org/Android/json.php?user=" + username));
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

	/*
	 * This method is called to ask the server if the users account information is a valid getNote user
	 */
	private boolean isUser(String un, String pw) throws URISyntaxException, ClientProtocolException, IOException {
		String username = un;
		String password = pw;
		
		BufferedReader in = null;
        try {
        	// Start set up to ask system to make a http call for us
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            // Where the URL is that I want to call is at ( I wrote this php page as well for this project)
            request.setURI(new URI("http://www.getnote.org/Android/isuser.php?user=" + username + "&pass=" + password));
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
            String page = sb.toString();
            
            try {
            	// Now we have the JSON string break it apart to see if the user is a valid getNote user
				JSONObject object = (JSONObject) new JSONTokener(page).nextValue();
				String user = object.getString("user");
				boolean check = Boolean.parseBoolean(user);
				return check;				
			} catch (JSONException e) {
				e.printStackTrace();
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
		return false;
	}			 	
}


