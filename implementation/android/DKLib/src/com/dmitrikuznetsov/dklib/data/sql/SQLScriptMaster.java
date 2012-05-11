package com.dmitrikuznetsov.dklib.data.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dmitrikuznetsov.dklib.data.sql.SQLManagerAbstractBase.DictionaryOpenHelper;


/**
 * Executes specific SQL scripts
 * 
 * @author dmitrikuznetsov
 *
 */
public class SQLScriptMaster 
{
	
	/**
	 * Reference for the helper that will perform actual SQL operations
	 */
	private DictionaryOpenHelper _helper = null;
	

	
	/**
	 * Current writable DB - only one copy per object!
	 */
	SQLiteDatabase 				 _writableDB = null;
	
	/**
	 * Current readable DB - only one copy per object!
	 */
	SQLiteDatabase 				 _readableDB = null;
	
	
	/**
	 * Default constructor to use for the master, requires
	 * reference to the DictionaryOpenHelper
	 */
	SQLScriptMaster(DictionaryOpenHelper helper)
	{
		_helper = helper;
	}
	
	
	/**
	 * Closes database and related data
	 */
	public void close()
	{
		if( _writableDB != null )
		{
			_writableDB.close();
			_writableDB = null;
		}
		
		if( _readableDB != null )
		{
			_readableDB.close();
			_readableDB = null;
		}
	}
	
	
	/**
	 * Params:
	 * <ul>
	 * 	<li>Tablename - Name of the table to delete</li>
	 * </ul>
	 * 
	 * Drops table if it exists
	 */
	private static final String DROP_TABLE_IF_EXISTS			= "DROP TABLE IF EXISTS %s";
	
	
	/**
	 * Params:
	 * <ul>
	 * 	<li>Tablename - Name of the table to delete</li>
	 * 	<li>Columns   - List of columns and their types</li>
	 * </ul>
	 * 
	 * @see <a href="http://www.sqlite.org/lang_createtable.html">SQlite table create</a>
	 * 
	 * Create table if it doesn't exist
	 */
	private static final String CREATE_TABLE_IF_NOT_EXISTS		= "CREATE TABLE IF NOT EXISTS %s %s";
	
	
	/**
	 * Selects from a list of tables and checks if the table exists or not
	 * 
	 * Params:
	 * <ul>
	 * 	<li>Tablename - Name of the table to delete</li>
	 * </ul>
	 */
	private static final String SELECT_TABLE_IF_EXISTS 			= "SELECT 1 FROM sqlite_master WHERE type='table' AND name = '%s'";
	
	
	/**
	 * Selects single row from SQL table
	 * 
	 * Params:
	 * <ul>
	 * 	<li>Tablename - Name of the table</li>
	 *  <li>RowID - ID of the row to find</li>
	 * </ul>
	 */
	private static final String SELECT_TABLE_ROW				= "SELECT * FROM %s WHERE [" + SQLTable.COLUMN_ID + "] = %s";
	
	
	

	/**
	 * Selects all rows from SQL table
	 * 
	 * Params:
	 * <ul>
	 * 	<li>Tablename - Name of the table</li>
	 * </ul>
	 */
	private static final String SELECT_ALL_ROWS				= "SELECT * FROM %s";
	
	
	/**
	 * Selects all rows from SQL table with additional where criteria
	 * 
	 * Params:
	 * <ul>
	 * 	<li>Tablename - Name of the table</li>
	 *  <li>Where 	  - Where criteria to match</li>
	 * </ul>
	 */
	private static final String SELECT_ROWS_WHERE			=  "SELECT * FROM %s WHERE %s";
	
	
	
	/**
	 * Drops table if it exists
	 * 
	 * @param tableName	Name of the table to delete
	 * 
	 * @throws SQLExecutionFailedException 
	 */
	public void dropTable(String tableName) throws SQLExecutionFailedException
	{
		execSQL(String.format( DROP_TABLE_IF_EXISTS, tableName));
	}
	
	
	/**
	 * Create table if it exists
	 * 
	 * @param table	Table to create
	 * @throws Exception Exception is called when table creation script generation fails
	 */
	public void createTable(SQLTable table) throws Exception
	{
		execSQL( table.generateCreationScript() );
	}
	
	
	/**
	 * Creates and returns reference to the current writable DB
	 * 
	 * @return Reference to the database object
	 */
	private SQLiteDatabase getWritableDB()
	{	
		_writableDB = _helper.getWritableDatabase();
		
		return _writableDB;
	}
	
	
	/**
	 * Creates and returns reference to the current readable DB
	 * 
	 * @return Reference to the database object
	 */
	private SQLiteDatabase getReadableDB()
	{
		_readableDB = _helper.getReadableDatabase();
		
		return _readableDB;
	}
	
	
	/**
	 * Checks if specified table already exists
	 * 
	 * @param tableName	Table to check
	 * 
	 * @return 	<ul>
	 * 				<li>True - table is created and exists</li>
	 * 				<li>False - table isn't created and doesn't exist</li>
	 * 			</ul>
	 * @throws Exception Exception is thrown if retrieving table status fails
	 */
	public boolean exists(String tableName) throws Exception
	{
		Cursor cursor = null;
		
		try 
		{
			cursor = execSQLForResult(String.format( SELECT_TABLE_IF_EXISTS, tableName));
			
			if( cursor.getCount() == 1 )
			{
				//table is found!
				return true;
			}
			else
			if( cursor.getCount() == 0 )
			{
				//table is not found
				return false;
			}
			else
			{
				//something else ? this is not normal
				throw new Exception("Unexpected count result =" + cursor.getCount() );
			}
		} 
		catch (SQLExecutionFailedException e) 
		{
			throw e;
		} 
		catch (Exception e) 
		{
			throw e;
		}
		finally
		{
			if( cursor != null )
			{
				cursor.close();
				cursor = null;
			}
		}

	}
	
	/**
	 * Executes specified SQL script for result
	 * 
	 * @param 	sqlScript			SQL script to execute
	 * 	
	 * @return	cursor				Cursor with data (WARNING: Cursor needs to be manually closed after it is not used anymore!)
	 * 
	 * @throws SQLExecutionFailedException 	Exception to throw if execution fails
	 */
	protected Cursor execSQLForResult(String sqlScript) throws SQLExecutionFailedException
	{
		
		 /**
		  * Retrieves writable database
		  */
		 SQLiteDatabase db = getWritableDB();
		 
		 /**
		  * Cursor is retrieved with results, the cursor needs to be closed manually
		  */
		 Cursor			cursor = null;
		 
		 try
		 {
			 if( (sqlScript == null )||(sqlScript.length() < 5))
			 {
				 //something is wrong with the script
				 throw new Exception("Invalid SQL String");
			 }
			 
			 
			 cursor = db.rawQuery(sqlScript, null);
			 
			 //return cursor:
			 return cursor;
		 }
		 catch(Exception ex)
		 {
			 throw new SQLExecutionFailedException(ex, sqlScript);
		 }
	}
	
	
	
	/**
	 * Executes specified SQL script which doesn't return any results
	 * 
	 * @param sqlScript						SQL script to execute
	 * @throws SQLExecutionFailedException 	Exception to throw if execution fails
	 */
	protected void execSQL(String sqlScript) throws SQLExecutionFailedException
	{
		
		 /**
		  * Retrieves readable database
		  */
		 SQLiteDatabase db = getReadableDB();
		 
		 
		 try
		 {
			 if( (sqlScript == null )||(sqlScript.length() < 5))
			 {
				 //something is wrong with the script
				 throw new Exception("Invalid SQL String");
			 }

			 db.execSQL(sqlScript);
		 }
		 catch(Exception ex)
		 {
			 throw new SQLExecutionFailedException( ex, sqlScript);
		 }
		 finally
		 {
			 //don't close it, it will be cached
		 }
	}


	/**
	 * Adds records to the table
	 * 
	 * @param tableName	Name of the table where to add new data
	 * @param values	Data to insert
	 * 
	 * @return			ID of the row after insert
	 * 
	 * @throws SQLExecutionFailedException 	Throws exception when insert operation fails
	 */
	public long insert(String tableName, ContentValues values) throws SQLExecutionFailedException 
	{
		 /**
		  * Retrieves writable database
		  */
		 SQLiteDatabase db = getWritableDB();
		 
		 return insert(db, tableName, values);
	}
	
	
	/**
	 * Inserts values using existing database
	 * 
	 * @param db		Databaes
	 * @param tableName	Name of the table where to add new data
	 * @param values	Data to insert
	 * 
	 * @return			ID of the row after insert
	 * 
	 * @throws SQLExecutionFailedException 	Throws exception when insert operation fails
	 */
	public long insert(SQLiteDatabase db, String tableName, ContentValues values) throws SQLExecutionFailedException 
	{
		 try
		 {
			 return db.insertOrThrow(tableName, null, values);
		 }
		 catch(Exception ex)
		 {
			 throw new SQLExecutionFailedException(tableName , ex , values);
		 }
	}


	/**
	 * Retrieves only specific row
	 * 
	 * @param tableName		Name of the table to match
	 * @param rowID			ID of the row to find
	 * 
	 * @return		Cursor with a row if there is any
	 * 
	 * @throws SQLExecutionFailedException 	Exception is thrown if SQL script execution fails
	 */
	public Cursor getRow(String tableName, long rowID) throws SQLExecutionFailedException 
	{
		return execSQLForResult( String.format( SELECT_TABLE_ROW , tableName , rowID));
	}


	/**
	 * Retrieves all the remaining rows
	 * 
	 * @param tableName		Name of the table to match
	 * @return				Cursor with a row if there is any
	 * 
	 * @throws SQLExecutionFailedException 	Exception is thrown if SQL script execution fails
	 */
	public Cursor getRows(String tableName) throws SQLExecutionFailedException 
	{
		return execSQLForResult( String.format( SELECT_ALL_ROWS , tableName ));
	}
	
	
	/**
	 * Retrieves all the rows that match specified WHERE criteria
	 * 
	 * @param tableName		Name of the table to match
	 * @param where			Where clause
	 * 
	 * @return				Cursor with a row if there is any
	 * 
	 * @throws SQLExecutionFailedException 	Exception is thrown if SQL script execution fails
	 */
	public Cursor getRowsWhere(String tableName, String where) throws SQLExecutionFailedException 
	{
		return execSQLForResult( String.format( SELECT_ROWS_WHERE , tableName, where ));
	}
	

	/**
	 * Updates existing table row with new data
	 * 
	 * @param tableName	Name of the table to update
	 * @param rowID		ID of the row to update
	 * @param values	Values to replace existing data with
	 * 
	 * @return			Number of rows that were updated
	 * @throws SQLExecutionFailedException  Exception is thrown if update of existing table fails
	 */
	public int update(String tableName, long rowID, ContentValues values) throws SQLExecutionFailedException 
	{
		return update(tableName, "["+ SQLTable.COLUMN_ID+"]=" + rowID , values);
	}
	
	
	/**
	 * Updates table rows with the custom where clause
	 * 
	 * @param tableName		Name of the table to update
	 * @param whereClause	Where selection clause, to use for selecting which rows to update
	 * @param values		Values to replace existing data with
	 * 
	 * @return								Number of rows that were updated
	 * @throws SQLExecutionFailedException  Exception is thrown if update of existing table fails
	 */
	public int update(String tableName, String whereClause, ContentValues values) throws SQLExecutionFailedException 
	{
		try
		{
			SQLiteDatabase db = getWritableDB();
			
			return db.update(tableName, values, whereClause, null);
		}
		catch(Exception ex)
		{
			throw new SQLExecutionFailedException(tableName , ex,  values);
		}
	}


	/**
	 * Deletes specific rows from the table, that match Where clause
	 * 
	 * @param tableName		Name of the table
	 * @param whereClause	Where selection clause, to use for selecting which rows to delete
	 * 
	 * @return				Number of rows that were deleted
	 * 
	 * @throws SQLExecutionFailedException Exception is thrown if row deletion fails
	 */
	public int deleteRow(String tableName, String whereClause) throws SQLExecutionFailedException 
	{
		try
		{
			SQLiteDatabase db = getWritableDB();
			
			return db.delete(tableName, whereClause, null);
		}
		catch(Exception ex)
		{
			throw new SQLExecutionFailedException(tableName , ex);
		}
	}


	/**
	 * Deletes specific rows from the table by ID
	 * 
	 * @param tableName		Name of the table
	 * @param whereClause	Where selection clause, to use for selecting which rows to delete
	 * 
	 * @return				Number of rows that were deleted
	 * 
	 * @throws SQLExecutionFailedException Exception is thrown if row deletion fails
	 */
	public int deleteRow(String tableName, long rowID) throws SQLExecutionFailedException
	{
		return deleteRow( tableName , "["+ SQLTable.COLUMN_ID+"]=" + rowID );
	}


	
}
