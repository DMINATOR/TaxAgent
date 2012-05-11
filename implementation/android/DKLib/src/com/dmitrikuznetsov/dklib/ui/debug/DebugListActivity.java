package com.dmitrikuznetsov.dklib.ui.debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.dmitrikuznetsov.dklib.R;
import com.dmitrikuznetsov.dklib.tools.localization.Translate;
import com.dmitrikuznetsov.dklib.tools.log.LogWriter;
import com.dmitrikuznetsov.dklib.ui.base.DKActivityBase;
import com.dmitrikuznetsov.dklib.ui.base.DKDialog;
import com.dmitrikuznetsov.dklib.ui.base.DKViewMap;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DebugListActivity extends DKActivityBase
{
	/**
	 * List of debug items when loaded
	 */
	ListView list = null;
	
	
	@Override
	public int getDefaultLayout()
	{
		return R.layout.debug_list;
	}

	/**
	 * Load ui data here
	 */
	@Override
	public void onCreated()
	{
		//find our list
		try 
		{
			getElementsByIds(new DKViewMap[]{
				new DKViewMap(R.id.listview, list)
			});
			
			//now iterate through supported activities and create a list of strings
			List<DKActivityBase> activities =	getDebugActivities();
			
			String[] items = new String[ activities.size() ];
			
			//fill
			for(int i = 0; i < activities.size(); i++)
			{
				items[i] = activities.get(i).getClass().getName();
			}
			
			//create adapter for the list
			list.setAdapter( new ArrayAdapter<String>(this,
	                android.R.layout.simple_list_item_1, items));
		} 
		catch (Exception e) 
		{
			DKDialog.ShowMessage(this,
					Translate.translate("Error") , 
					Translate.translate("Loading debug activitiy failed!"),
					Translate.translate("OK"),
					Translate.translate("Cancel"));
			
			LogWriter.writeException(this, e.getMessage(), e);
		}
	}
	
	/**
	 * Returns list of available debug activities
	 * 
	 * @return
	 */
	public List<DKActivityBase>	getDebugActivities()
	{
		List<DKActivityBase> list = new LinkedList<DKActivityBase>();
		
		//Add new activities here
		list.add( new LogReaderActivity() );
		list.add( new DialogSampleActivity() );
		
		return list;
	}
}
