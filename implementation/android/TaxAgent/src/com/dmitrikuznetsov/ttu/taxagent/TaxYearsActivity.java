package com.dmitrikuznetsov.ttu.taxagent;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dmitrikuznetsov.dklib.tools.localization.Translate;
import com.dmitrikuznetsov.dklib.tools.log.LogWriter;
import com.dmitrikuznetsov.dklib.ui.base.DKActivityBase;
import com.dmitrikuznetsov.dklib.ui.base.DKDialog;

public class TaxYearsActivity extends DKActivityBase
{
	
	public static final String 	EXTRA_INT_TAX_PAYER_ID		=	"EXTRA_INT_TAX_PAYER_ID";
	
	
	public static final int 	RETURN_INT_MANAGE		=	4;
	
	/**
	 * ID of tax payer, passed
	 */
	public int TaxPayerID = -1;
	
	//Tax payer information
	JSONObject taxPayerInfo = null;
	
	//Array of tax years
	JSONArray array = null;
	
	/**
	 * Storage for storing settings
	 */
	SharedStorage storage = null;
	
	
	/**
	 * Indicates that this activity was opened first
	 */
	boolean	firstRun = true;
	
	
	@Override 
	public int getDefaultLayout() 
	{
		return R.layout.taxyearslayout;
	}
	
	TextView textTaxPayerName 			= null;
	TextView textTaxPayerCreationDate 	= null;
	TextView textTaxPayerIncome 		= null;
	TextView textTaxPayerType 			= null;
	
	ListView lvTaxYearGrid 				= null;
	
	/**
	 * Called when activity is created
	 */
	public void onCreated()
	{
		try 
		{
			//find ui elements on the form:
			textTaxPayerName 			= (TextView)this.getElementById(R.id.textTaxPayerName);
			textTaxPayerCreationDate 	= (TextView)this.getElementById(R.id.textTaxPayerDateOfCreation);
			textTaxPayerIncome 			= (TextView)this.getElementById(R.id.textTaxPayerIncome);
			textTaxPayerType			= (TextView)this.getElementById(R.id.textTaxPayerType);
			
			lvTaxYearGrid				= (ListView)this.getElementById(R.id.listViewTaxPayerYears);
	
			lvTaxYearGrid.setOnItemClickListener( clickListener );
			
			TaxPayerID = getIntent().getExtras().getInt( TaxYearsActivity.EXTRA_INT_TAX_PAYER_ID );
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
	    			loadInfo();	
	    			
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
	
	/**
	 * Loads detailed tax payer info
	 */
	public void loadInfo()
	{
		//try to connect
    	try
    	{	
    		
			HTTPConnection connection = new HTTPConnection(  storage.getServerURL() );
			JSONObject response = connection.connectJSON( Engine.getTaxPayerInfo( TaxPayerID ) );
			storage.setLastServerResponse( response.toString() );
			
			if( !response.getString("result").equals("OK") )
			{
				throw new Exception("Invalid response from server = " + response.getString("data") );
			}
			
			//read results
			JSONObject data = response.getJSONArray("data").getJSONObject(0);
			
			//Fill the activity with results
			taxPayerInfo = data;
			
			textTaxPayerName.setText( taxPayerInfo.getString("Name") );
			textTaxPayerCreationDate.setText( "Date of creation: " + taxPayerInfo.getString("DateOfCreation") + " " );
			textTaxPayerIncome.setText( "Income: " +  taxPayerInfo.getString("Income") + " \u20AC" );
			
			//tax payer type
			int type = taxPayerInfo.getInt("Type");
			
			if( type == 0 )
			{
				//individual
				textTaxPayerType.setText("Individual");
			}
			else
				if( type == 1 )
				{
					//business
					textTaxPayerType.setText("Business");
				}
				else
				{
					//not expected!
					throw new Exception("Unexpected tax payer type: " + type);
				}
			
			loadTaxYears();
    	}
    	catch(Exception ex)
    	{
    		DKDialog.ShowMessage( this,
					Translate.translate("Error") , 
					Translate.translate("Connection failed: " + ex.getMessage()),
					Translate.translate("OK"),
					Translate.translate("Cancel"));
			
			LogWriter.writeException( this, ex.getMessage(), ex);
			
			storage.setLastServerResponse( ex.toString() );
		
    	}
	}
	
	/**
	 * Loads tax payer tax years information
	 * @throws Exception 
	 * @throws JSONException 
	 */
	public void loadTaxYears() throws JSONException, Exception
	{
		HTTPConnection connection = new HTTPConnection(  storage.getServerURL() );
		JSONObject response = connection.connectJSON( Engine.TaxYearGrid( TaxPayerID ) );
		storage.setLastServerResponse( response.toString() );
		
		//retrieve the array of items
		array = response.getJSONArray("data");
		
		List<String> items = new ArrayList<String>();
		
		for(int i = 0; i < array.length(); i++ )
		{
			JSONObject subObject = array.getJSONObject(i);
			
			int state = subObject.getInt("State");
			
			items.add( String.valueOf(subObject.getInt("Year")) + " - " + Engine.getYearStateForAction(state));
		}

		//specify changes
		lvTaxYearGrid.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items ));
		
	}
	
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 
	  
	  switch(requestCode) { 
	    case (RETURN_INT_MANAGE) : { 
	      if (resultCode == Activity.RESULT_OK) { 
	    	  //just reload the grid
	    	  try 
	    	  {
	    		  Toast.makeText( this ,"Refreshing tax year list!", Toast.LENGTH_LONG );
	    		  
	    		  storage = new SharedStorage(this);
	    		  
	    		  loadTaxYears();
	    		  

	    	    	
	    	  } 
	    	  catch (Exception ex) 
	    	  {
	    		  DKDialog.ShowMessage( this,
	    					Translate.translate("Error") , 
	    					Translate.translate("Connection failed: " + ex.getMessage()),
	    					Translate.translate("OK"),
	    					Translate.translate("Cancel"));
	    			
	    			LogWriter.writeException( this, ex.getMessage(), ex);
	    			
	    			storage.setLastServerResponse( ex.toString() );
	    	  }
	      } 
	      break; 
	    } 
	  } 
	 
	}
	
	
	/**
	 * Occurs when one of the tax years has been clicked
	 */
	public OnItemClickListener clickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			//get the year that was clicked on:
			try 
			{
				JSONObject subObject = array.getJSONObject(arg2);
				
				int state = subObject.getInt("State");
				
				if( state == 1 )
				{
					//this is manage page for the year that is still under submission
					Intent intent = new Intent();
					intent.putExtra( TaxProgramsManageActivity.EXTRA_INT_TAX_PAYER_ID, TaxPayerID );
					intent.putExtra( TaxProgramsManageActivity.EXTRA_INT_TAX_YEAR_ID, subObject.getInt("ID") );
					intent.putExtra( TaxProgramsManageActivity.EXTRA_INT_TAX_YEAR_VALUE, subObject.getInt("Year") );
	    			intent.setClass( TaxYearsActivity.this, TaxProgramsManageActivity.class);
	    		    startActivityForResult(intent, TaxYearsActivity.RETURN_INT_MANAGE );
				}
				else
				{
					//all ok open up the tax payer stats page:
					Intent intent = new Intent();
					intent.putExtra( TaxYearStatsActivity.EXTRA_INT_TAX_PAYER_ID, TaxPayerID );
					intent.putExtra( TaxYearStatsActivity.EXTRA_INT_TAX_YEAR_ID, subObject.getInt("ID") );
					intent.putExtra( TaxYearStatsActivity.EXTRA_INT_TAX_YEAR_VALUE, subObject.getInt("Year") );
	    			intent.setClass( TaxYearsActivity.this, TaxYearStatsActivity.class);
	    		    startActivity(intent);
				}
				
				
			} 
			catch (JSONException ex) 
			{
				DKDialog.ShowMessage( TaxYearsActivity.this,
						Translate.translate("Error") , 
						Translate.translate("Failed to perform click: " + ex.getMessage()),
						Translate.translate("OK"),
						Translate.translate("Cancel"));
			}
		}
	};
	
	 
}

