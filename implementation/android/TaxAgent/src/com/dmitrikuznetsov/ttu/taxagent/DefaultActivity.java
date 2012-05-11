package com.dmitrikuznetsov.ttu.taxagent;



import org.json.JSONObject;

import com.dmitrikuznetsov.dklib.tools.localization.Translate;
import com.dmitrikuznetsov.dklib.tools.log.LogWriter;
import com.dmitrikuznetsov.dklib.ui.base.DKActivityBase;
import com.dmitrikuznetsov.dklib.ui.base.DKDialog;
import com.dmitrikuznetsov.dklib.ui.debug.DebugListActivity;
import com.dmitrikuznetsov.ttu.taxagent.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DefaultActivity extends DKActivityBase {

	@Override
	public int getDefaultLayout() {
		return R.layout.main;
	}
	
	Button 		btnLogin = null;
	Button 		btnSettings = null;
	EditText	textLoginCode = null;
	
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
			textLoginCode = (EditText)this.getElementById(R.id.editTextCode);
			
			//register events:
			btnLogin = (Button)this.getElementById(R.id.btn_login);
			btnLogin.setOnClickListener(listenerLoginClicked);
		        
		        
			btnSettings = (Button)this.findViewById(R.id.btn_settings);
			btnSettings.setOnClickListener(listenerSettingsClicked);
		        
		} 
		catch (Exception e)
		{
			DKDialog.ShowMessage(this,
					Translate.translate("Error") , 
					Translate.translate("Loading default activitiy failed!"),
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
	    			textLoginCode.setText(  storage.getLastLoginCode() );
	    			
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
     * Listener called when Login button was clicked
     */
    OnClickListener listenerLoginClicked = new OnClickListener()
    {
	
        public void onClick(View arg0)
        {
        	
        	//try to connect
        	try
        	{
        		String loginCode = textLoginCode.getText().toString();
        		
        		//remember the code first:
        		storage.setLastLoginCode( loginCode );
        		
        		
    			HTTPConnection connection = new HTTPConnection(  storage.getServerURL() );
    			JSONObject response = connection.connectJSON( Engine.login( loginCode ) );
    			storage.setLastServerResponse( response.toString() );
    			
    			if( !response.getString("result").equals("OK") )
    			{
    				throw new Exception("Invalid response from server = " + response.getString("data") );
    			}
    			
    			//read results
    			JSONObject data = response.getJSONArray("data").getJSONObject(0);
    			
    			int type = data.getInt("Type");
    			int id = data.getInt("ID");
    			
    			//check for only individual or business = tax payer
    			if( (type == 1) || (type == 2) )
    			{
    				//all ok open up the tax payer info page:
    				Intent intent = new Intent();
    				intent.putExtra( TaxYearsActivity.EXTRA_INT_TAX_PAYER_ID, id );
        			intent.setClass(DefaultActivity.this, TaxYearsActivity.class);
        		    startActivity(intent);
    			}
    			else
    			{
    				throw new Exception("This code is not Tax Payer!");
    			}
    			
        	}
        	catch(Exception ex)
	    	{
	    		DKDialog.ShowMessage(DefaultActivity.this,
						Translate.translate("Error") , 
						Translate.translate("Connection failed: " + ex.getMessage()),
						Translate.translate("OK"),
						Translate.translate("Cancel"));
				
				LogWriter.writeException(DefaultActivity.this, ex.getMessage(), ex);
				
				storage.setLastServerResponse( ex.toString() );
			
	    	}

			
        }
    };
    
    
    /*
     * Listener called when Settings button was clicked
     */
    OnClickListener listenerSettingsClicked = new OnClickListener()
    {
	
        public void onClick(View arg0)
        {
			 Intent intent = new Intent();
			 intent.setClass(DefaultActivity.this, SettingsActivity.class);
		     startActivity(intent);
        }
    };
}