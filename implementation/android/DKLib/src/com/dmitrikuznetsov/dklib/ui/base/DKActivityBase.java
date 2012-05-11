package com.dmitrikuznetsov.dklib.ui.base;


import java.util.List;
import java.util.logging.Logger;

import com.dmitrikuznetsov.dklib.R;
import com.dmitrikuznetsov.dklib.tools.log.LogWriter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public abstract class DKActivityBase extends Activity implements IDKActivity
{
	protected DKViewMap viewMap = null;
	
	
	public abstract int getDefaultLayout();
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView( getDefaultLayout() );
        
        onCreated();
    }
    

    
	/**
	 * Starts specified activity
	 * @param  context 		Context that is used for starting activity
	 * @param  cls         	Class of DKActivityBase to start
	 * @throws Exception  	Exception is thrown if starting activity fails
	 */
	public static void start(Context context, Class<?> cls) throws Exception
	{
		try
		{
			Intent myIntent = new Intent(context, cls);
			context.startActivity(myIntent);
		}
		catch(Exception ex)
		{
			LogWriter.writeException(context, "Failed to start activity = " + ex.getMessage(), ex);
			throw ex; //throw further
		}
	}
	
	//helper methods:
	
	/**
	 * Retrieves ui element by specified id
	 * @param id
	 * @throws Exception  Exception is thrown if view is not found
	 */
	public View getElementById(int id) throws Exception
	{
		View view = super.findViewById(id);
		
		if( view == null )
		{
			LogWriter.writeWarning( this, "View not found, by Id = " + id);
			throw new Exception("View not found, by Id = " + id);
		}
		
		return view;
	}
	
	
	/**
	 * Simplifies loading multiple elements at the same time, every element must have a valid
	 * reference so it will be assigned to, when element is found
	 * 
	 * @param elements Map of elements
	 * @throws Exception  Exception is thrown if one element from the list is not found
	 */
    protected void getElementsByIds(DKViewMap[] elements) throws Exception
    {
    	for(int i = 0; i < elements.length; i++)
    	{
    		elements[i].element = getElementById( elements[i].id );
    	}
    }
	
	//events:
	
	/**
	 * Occurs right after the activity is created
	 */
	public void onCreated()
	{
		
	}
}
