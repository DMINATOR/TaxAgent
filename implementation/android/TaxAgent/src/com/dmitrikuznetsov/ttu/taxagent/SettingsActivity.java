package com.dmitrikuznetsov.ttu.taxagent;

import org.json.JSONObject;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dmitrikuznetsov.dklib.tools.localization.Translate;
import com.dmitrikuznetsov.dklib.tools.log.LogWriter;
import com.dmitrikuznetsov.dklib.ui.base.DKActivityBase;
import com.dmitrikuznetsov.dklib.ui.base.DKDialog;

public class SettingsActivity extends DKActivityBase {

	@Override
	public int getDefaultLayout() {
		return R.layout.settingslayout;
	}
	
	EditText 	textServerURL 	= null;
	Button		btnSave 		= null;
	Button		btnTest 		= null;
	Button		btnTestJSON 	= null;
	TextView	lastServerResponse = null;
	
	/**
	 * Indicates that this activity was opened first
	 */
	boolean	firstRun = true;
	
	/**
	 * Storage for storing settings
	 */
	SharedStorage storage = null;
	
	public void onCreated()
	{
		try 
		{
			//general ui elements:
			textServerURL = (EditText)this.getElementById(R.id.text_view_server_url);
			
			lastServerResponse = (TextView)this.getElementById(R.id.text_view_last_server_response);
			
			//register events:
			btnSave = (Button)this.getElementById(R.id.btn_save);
			btnSave.setOnClickListener(listenerSaveClicked);
		        
		        
			btnTest = (Button)this.findViewById(R.id.btn_test);
			btnTest.setOnClickListener(listenerTestClicked);
		        
			
			btnTestJSON = (Button)this.findViewById(R.id.btn_test_json);
			btnTestJSON.setOnClickListener(listenerTestJSONClicked);
		} 
		catch (Exception e)
		{
			DKDialog.ShowMessage(this,
					Translate.translate("Error") , 
					Translate.translate("Loading settings activitiy failed!"),
					Translate.translate("OK"),
					Translate.translate("Cancel"));
			
			LogWriter.writeException(this, e.getMessage(), e);
		}
	}
	
	
	protected void onPause() 
	{
	    super.onPause();
	    	
	    //destroy storage
	    storage = null;
	}
	    
	protected void onResume() 
	{
	    	super.onResume();
	    	
	    	try
	    	{
	    		storage = new SharedStorage(this);
	    		
	    		//populate data but only for the first time !
	    		if( firstRun )
	    		{
	    			//populate with the values
	    			textServerURL.setText(  storage.getServerURL() );

	    			lastServerResponse.setText( storage.getLastServerResponse() );
	    			
	    			firstRun = false;
	    		}
	    	}
	    	catch(Exception ex)
	    	{
	    		DKDialog.ShowMessage(this,
						Translate.translate("Error") , 
						Translate.translate("Reading settings activitiy failed!"),
						Translate.translate("OK"),
						Translate.translate("Cancel"));
				
				LogWriter.writeException(this, ex.getMessage(), ex);
	    	}

	 }
	    
	
	/*
     * Listener called when Save button was clicked
     */
    OnClickListener listenerSaveClicked = new OnClickListener()
    {
	
        public void onClick(View arg0)
        {
			//save new URL:
        	storage.setServerURL( textServerURL.getText().toString() );
        }
    };
    
    
    /*
     * Listener called when Test button was clicked
     */
    OnClickListener listenerTestClicked = new OnClickListener()
    {
	
        public void onClick(View arg0)
        {
        	//try to connect
        	try
        	{
    			HTTPConnection connection = new HTTPConnection(  storage.getServerURL() );
    			JSONObject response = connection.connectJSON( Engine.testConnection());
    			storage.setLastServerResponse( response.toString() );
    			
    			if( !response.getString("result").equals("OK") )
    			{
    				throw new Exception("Invalid response from server = " + response.getString("data") );
    			}
        	}
        	catch(Exception ex)
	    	{
	    		DKDialog.ShowMessage(SettingsActivity.this,
						Translate.translate("Error") , 
						Translate.translate("Connection failed: " + ex.getMessage()),
						Translate.translate("OK"),
						Translate.translate("Cancel"));
				
				LogWriter.writeException(SettingsActivity.this, ex.getMessage(), ex);
				
				storage.setLastServerResponse( ex.toString() );
			
	    	}
        	finally
        	{
        		lastServerResponse.setText( storage.getLastServerResponse() );
        	}

        }
    };
    
    
    /*
     * Listener called when Test JSON button was clicked
     */
    OnClickListener listenerTestJSONClicked = new OnClickListener()
    {
	
        public void onClick(View arg0)
        {
        	//try to connect
        	try
        	{
    			HTTPConnection connection = new HTTPConnection(  storage.getServerURL() );
    			JSONObject response = connection.connectJSON( Engine.testJSON());
    			storage.setLastServerResponse( response.toString() );
    			
    			if( !response.getString("result").equals("OK") )
    			{
    				throw new Exception("Invalid response from server = " + response.getString("data") );
    			}
        	}
        	catch(Exception ex)
	    	{
	    		DKDialog.ShowMessage(SettingsActivity.this,
						Translate.translate("Error") , 
						Translate.translate("Connection failed: " + ex.getMessage()),
						Translate.translate("OK"),
						Translate.translate("Cancel"));
				
				LogWriter.writeException(SettingsActivity.this, ex.getMessage(), ex);
				
				storage.setLastServerResponse( ex.toString() );
			
	    	}
        	finally
        	{
        		lastServerResponse.setText( storage.getLastServerResponse() );
        	}

        }
    };
}