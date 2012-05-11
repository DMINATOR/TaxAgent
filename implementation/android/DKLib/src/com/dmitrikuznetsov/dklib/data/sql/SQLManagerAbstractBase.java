package com.dmitrikuznetsov.dklib.data.sql;


import java.util.ArrayList;

import com.dmitrikuznetsov.dklib.tools.log.LogWriter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Abstract class, required to be inherited in order to use database operations.
 * 
 * @author dmitrikuznetsov
 *
 */
public abstract class SQLManagerAbstractBase
{

	
	/**
	 * Main database object that is used for database operations
	 */
	DictionaryOpenHelper 	_helper = null;
	
	
	
	/**
	 * Context is required for storing local copy of database object
	 */
	Context				 	_context = null;
	

	/**
	 * Master that will be used for executing SQL scripts
	 */
	SQLScriptMaster			_scriptMaster = null;
	
	
	/**
	 * Changes in SQL database are stored in version history.
	 */
	ArrayList<String> 		_versionHistory = new ArrayList<String>();
	
	
	/**
	 * List of SQL tables that manager knows about and can operate with them.
	 */
	ArrayList<SQLTable> 	_tables	= new ArrayList<SQLTable>();
	
	
	
	
	/**
	 * Default constructor, requires context
	 * 
	 * @param context	Context that is used for database operations
	 */
	public SQLManagerAbstractBase(Context context)
	{
		this._context = context;
		
		//add tables first
		addTables(_tables);
		
		//generate version history
		addHistoryInfo(_versionHistory);
		
		
		//create helper from the instance
		this._helper = new DictionaryOpenHelper();
		
		//create script master
		this._scriptMaster = new SQLScriptMaster(this._helper);
	}

	/**
	 * Closes database and any open objects
	 */
	public void close()
	{
		
		if( _scriptMaster != null )
		{
			_scriptMaster.close();
			_scriptMaster = null;
		}
		
		if( _helper != null )
		{
			_helper.close();
			_helper = null;
		}
	}
	
	/**
	 * Required method, the history information needs to be provided.
	 * Every change in database, needs to have appropirate change in database
	 * 
	 * @param history History where changes needs to be added
	 */
	protected abstract void addHistoryInfo(ArrayList<String> history);
	
	
	/**
	 * Adds tables to the list, so manager can operate with them.
	 * 
	 * @param tables List of SQL tables that will be managed
	 */
	protected abstract void addTables(ArrayList<SQLTable> tables);
	
	
	/**
	 * Retrieves name of a database
	 * <p>
	 * This method needs to be overriden in order to use custom database name
	 *  
	 * @return Database name 'Default' for default name
	 */
	public String getDatabaseName()
	{
		return "Default";
	}

	
	/**
	 * Retrieves context 
	 * 
	 * @return Currently used context
	 */
	public Context  getContext()
	{
		return _context;
	}
	
	/**
	 * Deletes database and re-creates it again.
	 * <p>
	 * There is no way to completelly delete database, only to clear its contents
	 * <p>
	 * If manual delete is required - remove the database file manually then
	 * @throws Exception Exception is thrown if clearing database fails
	 */
	public void clearDatabase() throws Exception
	{
		_helper.close();
		
		_context.deleteDatabase(getDatabaseName());
		
		SQLiteDatabase db = null;
		
		try
		{
			//create helper from the instance
			this._helper = new DictionaryOpenHelper();
			
			//create script master
			this._scriptMaster = new SQLScriptMaster(this._helper);
			
			//just get writable database - to create !
			db = _helper.getWritableDatabase();
		}
		catch(Exception ex)
		{
			throw new Exception("[SQL] - Failed to create SQL database" , ex);
		}
		finally
		{
			if( db != null )
			{
				db.close();
				db = null;
			}
		}
	}
	
	/**
	 * Helper for SQL operations on Android.
	 * 
	 * @author dmitrikuznetsov
	 *
	 */
	public class DictionaryOpenHelper extends SQLiteOpenHelper 
	{

	    DictionaryOpenHelper() 
	    {
	        super( _context, getDatabaseName(), null, _versionHistory.size() + 1 ); //always bigger than 0!
	    }

	    @Override
	    public void onCreate(SQLiteDatabase db) 
	    {
	    	
	    	try
	    	{
	    		LogWriter.writeInfo(  "[SQL] - Creating new database = " + getDatabaseName() );
	    		
	    		//create this table
	            for(int i = 0; i < _tables.size(); ++i )
	            {
	            	LogWriter.writeDebug( "[SQL] - Create table:" +_tables.get(i).getTableName() );
	            	_tables.get(i).onCreate(db);
	            }
	    	}
	    	catch(Exception ex)
	    	{
	    		//just log - there is not much we can do if it fails
	    		LogWriter.writeException(_context, "[SQL] - Failed to create database", ex);
	    	}

	    }

		@Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
			try
	    	{
	    		LogWriter.writeInfo( "[SQL] - Upgrading database = " + getDatabaseName());
	    		

	    		//upgrade all the tables
	            for(int i = 0; i < _tables.size(); ++i )
	            {
	            	LogWriter.writeDebug( "[SQL] - Upgrade table:" +_tables.get(i).getTableName() );
	            	_tables.get(i).onUpgrade(db);
	            }
	    	}
	    	catch(Exception ex)
	    	{
	    		//just log - there is not much we can do if it fails
	    		LogWriter.writeException(_context, "[SQL] - Failed to upgrade database", ex);
	    	}
        }
	}
	

}
