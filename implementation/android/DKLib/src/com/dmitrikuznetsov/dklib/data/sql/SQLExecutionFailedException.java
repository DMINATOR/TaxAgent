package com.dmitrikuznetsov.dklib.data.sql;

import android.content.ContentValues;


/**
 * Exception is returned if SQL execution fails
 * 
 * @author dmitrikuznetsov
 *
 */
public class SQLExecutionFailedException extends Exception 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5625571140755707006L;

	/**
	 * SQL script that was executed if any
	 */
	private String				_sqlScript = null;
	
	/**
	 * Name of the table
	 */
	private String				_tableName = null;
	
	/**
	 * Related SQL content values
	 */
	private ContentValues		_values = null;
	
	
	/**
	 * Default constructor for the error
	 * 
	 * @param tableName		Name of the table
	 * @param ex			Inner exception
	 */
	public SQLExecutionFailedException(String tableName, Exception ex) 
	{
		super(ex);
		_tableName = tableName;
	}


	/**
	 * Default constructor for the error
	 * 
	 * @param ex			Inner exception
	 * @param sqlScript		SQL script to use for execution
	 */
	public SQLExecutionFailedException(Exception ex, String sqlScript) 
	{
		super(ex);
		_sqlScript = sqlScript;
	}
	
	
	/**
	 * Custom constructor, that includes additional info - table name and values
	 * 
	 * @param tableName		Name of the table
	 * @param ex			Inner exception
	 * @param values		Values used
	 */
	public SQLExecutionFailedException(String tableName, Exception ex,
			ContentValues values) 
	{
		super(ex);
		_tableName = tableName;
		_values = values;
	}


}
