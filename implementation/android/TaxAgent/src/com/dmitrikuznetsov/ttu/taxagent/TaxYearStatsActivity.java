package com.dmitrikuznetsov.ttu.taxagent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.dmitrikuznetsov.dklib.tools.localization.Translate;
import com.dmitrikuznetsov.dklib.tools.log.LogWriter;
import com.dmitrikuznetsov.dklib.ui.base.DKActivityBase;
import com.dmitrikuznetsov.dklib.ui.base.DKDialog;


/**
 * Shows stats for the selected tax year
 * 
 * @author dmitrikuznetsov
 *
 */
public class TaxYearStatsActivity  extends DKActivityBase
{
	
	public static final String 	EXTRA_INT_TAX_PAYER_ID			=	"EXTRA_INT_TAX_PAYER_ID";
	public static final String 	EXTRA_INT_TAX_YEAR_ID			=	"EXTRA_INT_TAX_YEAR_ID";
	public static final String 	EXTRA_INT_TAX_YEAR_VALUE		=	"EXTRA_INT_TAX_YEAR_VALUE";
	
	/**
	 * ID of tax payer, passed
	 */
	public int TaxPayerID = -1;
	
	/**
	 * ID of tax year
	 */
	public int TaxYearID = -1;
	
	
	/**
	 * Income of this tax payer
	 */
	public double TaxIncome = 0;
	
	
	/**
	 * Found year value
	 */
	public int YearValue = -1;
	
	/**
	 * Storage for storing settings
	 */
	SharedStorage storage = null;
	
	//Array of tax years
	JSONArray array = null;
	
	
	TextView tbTaxYearIncome = null;
	TextView tbTaxYearStats = null;
	TextView tbTaxYearTax = null;
	ListView tableTaxProgramStats = null;
	
	/**
	 * Indicates that this activity was opened first
	 */
	boolean	firstRun = true;
	
	@Override 
	public int getDefaultLayout() 
	{
		return R.layout.taxyearstatslayout;
	}
	
	
	/**
	 * Called when activity is created
	 */
	public void onCreated()
	{
		try 
		{
			//find ui elements on the form:
			tbTaxYearIncome 		= (TextView)this.getElementById(R.id.tbTaxYearIncome);
			tbTaxYearStats 			= (TextView)this.getElementById(R.id.tbTaxYearStats);
			tbTaxYearTax 			= (TextView)this.getElementById(R.id.tbTaxYearTax);
			tableTaxProgramStats	= (ListView)this.getElementById(R.id.listViewTaxPayerProgramStats);
			
			//tbTaxYearIncome				= (ListView)this.getElementById(R.id.listViewTaxPayerYears);
	
			//lvTaxYearGrid.setOnItemClickListener( clickListener );
			
			TaxPayerID = getIntent().getExtras().getInt( TaxYearStatsActivity.EXTRA_INT_TAX_PAYER_ID );
			TaxYearID = getIntent().getExtras().getInt( TaxYearStatsActivity.EXTRA_INT_TAX_YEAR_ID );
			YearValue = getIntent().getExtras().getInt( TaxYearStatsActivity.EXTRA_INT_TAX_YEAR_VALUE );

			
			//specify changes
			//lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items ));
		
			
			tbTaxYearStats.setText("Stats for year: " + YearValue);
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
    		//Get TaxPayer Income first:
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
			TaxIncome = data.getDouble("Income");
			tbTaxYearIncome.setText( String.valueOf(TaxIncome) + " \u20AC" );
			//Get Tax stats:
			
    		
    		response = connection.connectJSON( Engine.TaxProgramStatsGrid( TaxYearID, TaxPayerID ) );
    		storage.setLastServerResponse( response.toString() );
    		
    		//retrieve the array of items
    		array = response.getJSONArray("data");
    		
    		double totalTaxRate = 0;
    		
    		for(int i = 0; i < array.length(); i++)
    		{
    			JSONObject obj = array.getJSONObject(i);
    			
    			totalTaxRate += obj.getDouble("CalculatedTaxAmount"); 
    		}
    		
    		DecimalFormat df = new DecimalFormat("#.##");

    		double taxRatePercent = (totalTaxRate / TaxIncome ) * 100; ;
			
    		//show result
    		tbTaxYearTax.setText( df.format(taxRatePercent) + " % (" +  totalTaxRate + " \u20AC )" );
    		
    		//only after array is loaded - show the list
    		adapter = new TaxYearProgramsAdapter(this);
			tableTaxProgramStats.setAdapter(adapter );

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
	  * Adapter used for showing list of programs and their stats
	  */
	TaxYearProgramsAdapter adapter = null;
	
	/**
	  * Adapter class for showing result of selected programs
	  * 
	  * @author Dmitri Kuznetsov
	  *
	  */
	 private class TaxYearProgramsAdapter extends BaseAdapter 
	 {
	
		/**
		 * Context that owns this adapter
		 */
		private Context _context = null;

		/**
		 * View that is inflated from XML
		 */
		LayoutInflater _inflater = null;

		public TaxYearProgramsAdapter(Context context) throws Exception
		{
			_context 	= context;
			
			_inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if( _inflater == null )
			{
				throw new Exception("Couldn't create layout inflater!");
			}
			
		}

		public int getCount()
		{
			return array.length();
		}

		@Override
		public boolean areAllItemsEnabled()
		{
			return false;
		}

		public Object getItem(int position)
		{
			try 
			{
				return array.get(position);
			} catch (JSONException ex) 
			{
				LogWriter.writeException(_context, "Failed to get Item!", ex);
				return null;
			}
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			if ( (array == null) || (array.length() == 0) || (position >= array.length()))
			{
				TextView tv = new TextView(_context);
				tv.setText("No items.");

				return tv;
			}
			else
			{

					JSONObject currentObject = (JSONObject)getItem(position);
				
					View _inflatedView = _inflater.inflate( R.layout.statsgridlayout, null);
					
					TextView tvID			= (TextView) _inflatedView.findViewById( R.id.textview_stats_id_value);
					TextView tvInstitution	= (TextView) _inflatedView.findViewById( R.id.textview_stats_institutionname);
					TextView tvProgram		= (TextView) _inflatedView.findViewById( R.id.textview_stats_programname);
					TextView tvMinYears		= (TextView) _inflatedView.findViewById( R.id.textview_stats_minimumyears_value);
					TextView tvYearsLeft	= (TextView) _inflatedView.findViewById( R.id.textview_stats_yearsremaining_value);	
					TextView tvYearsTaxRate	= (TextView) _inflatedView.findViewById( R.id.textview_stats_taxrate_value);	
					
					try
					{
						tvID.setText( currentObject.getString("ID")  );
						tvInstitution.setText( currentObject.getString("Institution")  );
						tvProgram.setText( currentObject.getString("Name")  );
						tvYearsLeft.setText( currentObject.getString("YearsLeft")  );
						tvMinYears.setText( currentObject.getString("MinimumYears")  );
						
						double taxAmount = currentObject.getDouble("CalculatedTaxAmount");
						double percent = (taxAmount / TaxIncome ) * 100;
						
						DecimalFormat df = new DecimalFormat("#.##");

						
						tvYearsTaxRate.setText( df.format(percent) + " % (" +  taxAmount + " \u20AC )" );
					}
					catch(Exception ex)
					{
						LogWriter.writeException(_context, "Failed to show Item!", ex);
					}
					
					
					return _inflatedView;
				
			}

		}
	 }
	
}
