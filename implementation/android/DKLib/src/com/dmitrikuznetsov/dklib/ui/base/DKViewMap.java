package com.dmitrikuznetsov.dklib.ui.base;

import android.view.View;
import android.widget.ListView;


/**
 * Map is used to simplify creation of UI elements for activities
 * @author dmitrikuznetsov
 *
 */
public class DKViewMap 
{

	/**
	 * Resource ID of the element
	 */
	public int id;
	
	/**
	 * Actual item when it is found
	 */
	public View element;
	

	/**
	 * Constructor for creating map
	 * 
	 * @param elementId	Resource ID of the element
	 * @param view		View element to use
	 */
	public DKViewMap(int elementId, View view) 
	{
		id = elementId;
		element = view;
	}
}
