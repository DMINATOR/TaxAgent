package com.dmitrikuznetsov.dklib.data.sql;

/**
 * Defines columns that are found in the table
 *  
 * @see <a href="http://www.sqlite.org/datatype3.html">SQLite data types</a>
 * @author dmitrikuznetsov
 *
 */
public class ColumnsBase 
{
	/**
	 * Name of a column
	 */
	public String Name = null;
	
	
	/**
	 * Type of a column,
	 * let's say it is primary key
	 */
	int		Type =	ColumnsBase.COLUMN_TYPE_PK;
	
	
	/**
	 * Indicates if variable is nullable type or not - by default it is
	 */
	boolean isNull = true;
		
		
	/**
	 * Primary key index
	 */
	public static final int COLUMN_TYPE_PK		=	0;
	
	
	/**
	 * Column is stored as a text
	 */
	public static final int	COLUMN_TYPE_TEXT	=	1;
	
	
	/**
	 * Column is stored as integer
	 */
	public static final int COLUMN_TYPE_INTEGER	= 	2;
	
	
	/**
	 * Column is stored as real (floating point number)
	 */
	public static final int COLUMN_TYPE_REAL	= 	3;
	
	
	/**
	 * Default constructor for column
	 * 
	 * @param name			Name of the column
	 * @param columnType	Type of the column one value from {@link ColumnsBase}.COLUMN_TYPE_* integer
	 */
	public ColumnsBase(String name, int columnType)
	{
		this( name,  columnType, true );
	}
	
	/**
	 * Default constructor for column
	 * 
	 * @param name			Name of the column
	 * @param columnType	Type of the column one value from {@link ColumnsBase}.COLUMN_TYPE_* integer
	 * @param isNull		Indicates if variable can be null or not (Doesn't apply for primary key, which is always NOT NULL by default)
	 */
	public ColumnsBase(String name, int columnType, boolean isNull)
	{
		this.Name 	= name;
		this.Type 	= columnType;
		this.isNull	= isNull;
		
		//check if it is primary key
		if( Type == COLUMN_TYPE_PK )
		{
			isNull = false;
		}
	}
	
	/**
	 * Generates string based on the column properties
	 * 
	 * @throws Exception Exception is thrown if script generation fails
	 */
	public String toScript() throws Exception
	{
		String result = Name;
		result += " ";
		
		if( Type == COLUMN_TYPE_PK )
		{
			result += "INTEGER PRIMARY KEY AUTOINCREMENT"; //cannot be null anyway
		}
		else
		{

			switch( Type )
			{

			case COLUMN_TYPE_TEXT:
				result += "TEXT ";
				break;

			case COLUMN_TYPE_INTEGER:
				result += "INTEGER ";
				break;

			case COLUMN_TYPE_REAL:
				result += "REAL ";
				break;

			default:
				throw new Exception("Unknown column type = " + Type);

			}
			
			//add null specification
			if( isNull )
			{
				result += "NULL";
			}
			else
			{
				result += "NOT NULL";
			}
		}
		
		return result;
	}
}
