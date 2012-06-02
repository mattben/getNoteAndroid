package org.getnote;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PoliciesActivity extends Activity implements OnClickListener {
	Button back;
	TextView policies;
	TextView longP;

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
        setContentView(R.layout.policies); 
        back = (Button) this.findViewById(R.id.buttonback);
        back.setOnClickListener(this);
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	}

}
