package com.dmitrikuznetsov.ttu.taxagent;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Application information is stored/retrieved with this class
 * 
 * 
 */
public class SharedStorage
{
	public static final String PREFS_NAME = "internal_preference";
	
	
	/**
	 * Server URL is stored here
	 */
	public static final String VALUE_STRING_SERVER_URL 				= "VALUE_STRING_SERVER_URL";
	
	
	
	/**
	 * Remembers last login code that was used
	 */
	public static final String VALUE_STRING_LAST_LOGIN_CODE 		= "VALUE_STRING_LAST_LOGIN_CODE";
	

	
	/**
	 * Last response from the server
	 */
	public static final String VALUE_STRING_SERVER_LAST_RESPONSE 	= "VALUE_STRING_SERVER_LAST_RESPONSE";
	
	
	Context	_context	= null;
	
	/**
	 * Storage needs to be created with context object 
	 * in order to use it for reading and writing
	 * 
	 * @param context Context for using storage object
	 */
	public SharedStorage(Context context)
	{
		_context = context;
	}
	
	
	/**
	 * Puts a new String value to the storage
	 * 
	 * @param ket 	Key for the storage object
	 * @param value Value to put to the storage
	 */
	public void putString(String key, String value)
	{
		  // We need an Editor object to make preference changes.
	      // All objects are from android.context.Context
	      SharedPreferences settings = _context.getSharedPreferences(PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString(key, value);

	      // Commit the edits!
	      editor.commit();
	}
	
	
	/**
	 * Value to get from a storage
	 * 	
	 * @param	key					Key for the storage object
	 * @param	defaultValue		Default value to write to the storage
	 * @return String value retrieved from a storage
	 */
	public String getString(String key, String defaultValue)
	{
		 SharedPreferences settings = _context.getSharedPreferences(PREFS_NAME, 0);
	     
		 return settings.getString( key , defaultValue );
	}
	
	
	/**
	 * Puts a new String value to the storage
	 * 
	 * @param 	ket 				Key for the storage object
	 * @param	value				Value t o write to the storage
	 */
	public void putInt(String key, int value)
	{
		  // We need an Editor object to make preference changes.
	      // All objects are from android.context.Context
	      SharedPreferences settings = _context.getSharedPreferences(PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putInt(key, value);

	      // Commit the edits!
	      editor.commit();
	}
	
	
	/**
	 * Value to get from a storage
	 * 
	 * @param	key					Key for the storage object
	 * @param	defaultValue		Default value to write to the storage
	 * @return int value retrieved from a storage
	 */
	public int getInt(String key, int defaultValue)
	{
		 SharedPreferences settings = _context.getSharedPreferences(PREFS_NAME, 0);
	     
		 return settings.getInt( key , defaultValue );
	}
	
	
	
	/**
	 * Puts a new Long value to the storage
	 * 
	 * @param 	ket 				Key for the storage object
	 * @param	value				Value to write to the storage
	 */
	public void putLong(String key, long value)
	{
		  // We need an Editor object to make preference changes.
	      // All objects are from android.context.Context
	      SharedPreferences settings = _context.getSharedPreferences(PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putLong(key, value);

	      // Commit the edits!
	      editor.commit();
	}
	
	
	/**
	 * Value to get from a storage
	 * 
	 * @param	key					Key for the storage object
	 * @param	defaultValue		Default value to write to the storage
	 * @return Long value retrieved from a storage
	 */
	public long getLong(String key, long defaultValue)
	{
		 SharedPreferences settings = _context.getSharedPreferences(PREFS_NAME, 0);
	     
		 return settings.getLong( key , defaultValue );
	}


	
	/**
	 * Clears any data that is stored right now
	 */
	public void clear()
    {
	
		  SharedPreferences settings = _context.getSharedPreferences(PREFS_NAME, 0);
	
		  SharedPreferences.Editor editor = settings.edit();
	      editor.clear();

	      // Commit the edits!
	      editor.commit();
    }
	
	
	/**
	 * Sets new server URL
	 * @param newServerURL
	 */
	public void setServerURL(String newServerURL)
	{
		putString( SharedStorage.VALUE_STRING_SERVER_URL , newServerURL);
	}
	
	
	/**
	 * Retrieves server URL
	 */
	public String getServerURL()
	{
		return getString( SharedStorage.VALUE_STRING_SERVER_URL , "http://dmitrikuznetsov.com/ttu/taxagent/backend/engine.php");
	}
	
	
	/**
	 * Sets new server last response
	 * @param newServerURL
	 */
	public void setLastServerResponse(String newServerResponse)
	{
		putString( SharedStorage.VALUE_STRING_SERVER_LAST_RESPONSE , newServerResponse);
	}
	
	
	/**
	 * Retrieves last server response
	 */
	public String getLastServerResponse()
	{
		return getString( SharedStorage.VALUE_STRING_SERVER_LAST_RESPONSE , "no server response");
	}
	
	
	/**
	 * Sets new code that was used for login last time
	 * @param newServerURL
	 */
	public void setLastLoginCode(String newLoginCode)
	{
		putString( SharedStorage.VALUE_STRING_LAST_LOGIN_CODE , newLoginCode);
	}
	
	
	/**
	 * Retrieves server URL
	 */
	public String getLastLoginCode()
	{
		return getString( SharedStorage.VALUE_STRING_LAST_LOGIN_CODE , "");
	}
	
}
