package com.dmitrikuznetsov.dklib.data.sql;

import com.dmitrikuznetsov.dklib.tools.log.LogWriter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Single SQL table
 * 
 * @author dmitrikuznetsov
 *
 */
public abstract class SQLTable 
{
	/**
	 * Reference to the manager that is used to 
	 * manage this table
	 */
	protected SQLManagerAbstractBase 		_manager = null;
	
	
	/**
	 * Table name that will be used for matching the table in database
	 */
	protected String 						_tableName = "DefaultTable"; 
	
	
	/**
	 * List of columns that table is going to have
	 */
	protected ColumnsBase[] 				_columns = null;
	
	
	/**
	 * Every table has ID as primary key and first row
	 */
	public static final String COLUMN_ID		= "id";
	//new ColumnsBase(COLUMN_ID 		, ColumnsBase.COLUMN_TYPE_PK),

	/**
	 * Creates new table from {@link SQLManager}
	 * 
	 * @param manager	Manager that is used for managing this table
	 */
	public SQLTable(SQLManagerAbstractBase manager, ColumnsBase[] columns)
	{
		_manager = manager;
		
		_columns = columns;
		
		//set table name
		_tableName = getTableName();
	}
	
	/**
	 * Creation function that will be called when database is created first time
	 * <p>
	 * By default automatically generates the script
	 * 
	 * @param db				Android SQL operation object that will execute this operation
	 * @throws Exception 		Exception is thrown if sql table generation fails
	 */
	public void onCreate(SQLiteDatabase db) throws Exception
	{
		db.execSQL( generateCreationScript()  ); //just call SQL script that is generated
	}

	
	/**
	 * Upgrade function that will be called once upgrade is needed
	 * 
	 * @param db	Android SQL operation object that will execute this operation
	 * 
	 * @throws Exception 		Exception is thrown if sql table generation fails
	 */
	public void onUpgrade(SQLiteDatabase db)
	{
		
	}
	
	
	/**
	 * Creates a table if it is not created yet
	 * @throws Exception Exception is thrown if table creation fails
	 */
	public void create() throws Exception
	{
		_manager._scriptMaster.createTable( this );
	}
	
	/**
	 * Drops - deletes table from a database
	 * @throws SQLExecutionFailedException   Exception is thrown if drop operation fails
	 */
	public void drop() throws SQLExecutionFailedException
	{
		LogWriter.writeDebug( "[SQL] - Drop table:" + _tableName);
		_manager._scriptMaster.dropTable(_tableName);
	}
	
	
	/**
	 * Checks if specified table already exists
	 * 
	 * @return 	<ul>
	 * 				<li>True - table is created and exists</li>
	 * 				<li>False - table isn't created and doesn't exist</li>
	 * 			</ul>
	 * 
	 * @throws Exception Exception is thrown if retrieving table status fails
	 */
	public boolean exists() throws Exception
	{
		return _manager._scriptMaster.exists(_tableName);
	}
	
	/**
	 * Retrieves table name
	 * 
	 * @return Name of the table to retrieve
	 */
	public abstract String getTableName();
	
	
	
	/**
	 * Adds values to the table row
	 * 
	 * @param values	Actual values to add
	 * 
	 * @return 			ID of the row after insert
	 * 
	 * @throws SQLExecutionFailedException 	Exception throws if insert operation fails
	 */
	public long add(ContentValues values) throws SQLExecutionFailedException
	{
		return _manager._scriptMaster.insert(_tableName, values);
	}
	
	
	/**
	 * Adds values to the table row, but using specified database instead
	 * 
	 * @param db		Database
	 * @param values	Actual values to add
	 * 
	 * @return 			ID of the row after insert
	 * 
	 * @throws SQLExecutionFailedException 	Exception throws if insert operation fails
	 */
	public long add(SQLiteDatabase db, ContentValues values) throws SQLExecutionFailedException
	{
		return _manager._scriptMaster.insert(db, _tableName, values);
	}
	
	
	/**
	 * Update existing row with new data
	 * 
	 * @param rowID		ID of the row to update
	 * @param values	Values to use for updating row data
	 * 
	 * @return			Number of rows updated
	 * @throws SQLExecutionFailedException  Exception is thrown if update of table row failed
	 */
	public int updateRow(long rowID, ContentValues values) throws SQLExecutionFailedException 
	{
		return _manager._scriptMaster.update(_tableName, rowID, values);
	}
	

	/**
	 * Update existing rows with new data
	 * 
	 * @param whereClause	Where clause to use for finding the matching rows to update
	 * @param values		Values to use for updating row data
	 * 
	 * @return			Number of rows updated
	 * @throws SQLExecutionFailedException  Exception is thrown if update of table row failed
	 */
	public int updateAllRow(String whereClause, ContentValues values) throws SQLExecutionFailedException 
	{
		return _manager._scriptMaster.update(_tableName, whereClause, values);
	}
	
	/**
	 * Get specific row of the table
	 * 
	 * @param rowID	ID of the row to retrieve
	 * @return		Cursor for the single SQL row
	 * 
	 * @throws SQLExecutionFailedException  Exception thrown if retrieving row fails
	 */
	public Cursor getRow(long rowID) throws SQLExecutionFailedException
	{
		return _manager._scriptMaster.getRow(_tableName, rowID);
	}
	
	
	/**
	 * Get all existing rows
	 * 
	 * @return		Cursor for all the rows
	 * 
	 * @throws SQLExecutionFailedException  Exception thrown if retrieving row fails
	 */
	public Cursor getRows() throws SQLExecutionFailedException
	{
		return _manager._scriptMaster.getRows(_tableName);
	}
	
	
	/**
	 * Executes SQL script
	 * directly
	 * 
	 * @param sql	SQL script to execute
	 * 
	 * @return	Cursor with a list of rows
	 * @throws SQLExecutionFailedException  Exception is thrown if executing script fails
	 */
	protected Cursor executeSQL(String sql) throws SQLExecutionFailedException
	{
		return _manager._scriptMaster.execSQLForResult(sql);
	}

	
	/**
	 * Get rows that match where criteria
	 * 
	 * @param	where	Where criteria to match
	 * 
	 * @return		Cursor for all the rows
	 * 
	 * @throws SQLExecutionFailedException  Exception thrown if retrieving row fails
	 */
	public Cursor getRowsWhere(String where) throws SQLExecutionFailedException
	{
		return _manager._scriptMaster.getRowsWhere(_tableName, where);
	}


	
	/**
	 * Deletes specific row
	 * 
	 * @param rowID		ID of the row to delete
	 * @return			Number of rows that were deleted
	 * 
	 * @throws SQLExecutionFailedException  Exception is thrown if row deletion fails
	 */
	public int deleteRow(long rowID) throws SQLExecutionFailedException
	{
		return _manager._scriptMaster.deleteRow(_tableName, rowID);
	}

	/**
	 * Deletes all existing rows
	 * 
	 * @return			Number of rows that were deleted
	 * 
	 * @throws SQLExecutionFailedException  Exception is thrown if row deletion fails
	 */
	public int deleteAllRows() throws SQLExecutionFailedException
	{
		return _manager._scriptMaster.deleteRow(_tableName, "1=1");
	}
	
	
	/**
	 * Dynamically generates SQL creation script from table name and columns
	 * 
	 * @return	Generated SQL script
	 * 
	 * @throws Exception 	Exception is thrown if automatic column generation fails
	 */
	public String generateCreationScript() throws Exception
	{
		/*
		 * Example:
		 * 
		 * CREATE TABLE tbl_countries ("
        	+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
        	+ "country_name TEXT);";
		 */
		
		if( _tableName == null ) throw new Exception("Table name is not specified!");
		if( _columns.length == 0 ) throw new Exception("Columns not specified!");
		
		String result = "CREATE TABLE IF NOT EXISTS " + _tableName + " (" ;
		
		//add primary key by default
		result += "id INTEGER PRIMARY KEY AUTOINCREMENT ";
		
		//loop through the list of columns
		for(int i = 0; i < _columns.length; i++)
		{
			//add separator
			result += ",";
			
			result += _columns[i].toScript();
		}
		
		result += ");";
		
		return result;
	}
}
