package com.dmitrikuznetsov.dklib.fileio;

import java.io.File;
import java.io.IOException;


/**
 * Abstract base class for file related operations
 * 
 * @author dmitrikuznetsov
 *
 */
public abstract class AbstractFileInfo 
{
	/**
	 * Internal reference for the file object
	 */
	protected File 	_file = null;
	
	
	/**
	 * Default constructor that assigns initial file object
	 * 
	 * @param file
	 */
	public AbstractFileInfo(File file)
	{
		_file = file;
	}
	
	
	/**
	 * Default constructor that assigns initial file object
	 * 
	 * @param name	Name of the file or folder
	 */
	public AbstractFileInfo(String name)
	{
		_file = new File( name );
	}
	
	
	/**
	 * Retrieves absolute location to the path or directory
	 */
	public String getAbsoluteLocation()
	{
		return _file.getAbsolutePath();
	}
	
	/**
	 * By default retrieves absolute location to the file or directory
	 */
	@Override
	public String toString() 
	{
		return getAbsoluteLocation();
	}
	
	/**
	 * Deletes file or directory
	 * <p>
	 * By default if there is an exception it is thrown
	 */
	public void delete() throws IOException
	{
		delete(true);
	}
	
	/**
	 * Deletes file or directory
	 * 
	 * @param  errorIfFails	Show error message if operation fails
	 * 
	 * @throws IOException Exception is thrown if deleting file fails
	 */
	public void delete(boolean errorIfFails) throws IOException
	{
		if( !_file.delete() )
		{
			if( errorIfFails ) //show message only if it was requested
			{
				throw new IOException("Failed to delete = " + _file.getAbsolutePath() );
			}
		}
	}
	
	
	/**
	 * Checks if object exists or not.
	 * 
	 * @return  <ul>
	 * 				<li>True - exists	</li>
	 * 				<li>False - doesn't exist	</li>
	 * 			</ul>
	 */
	public boolean exists()
	{
		return _file.exists();
	}
	
	
	
	/**
	 * Creates new file or directory
	 * 
	 * @throws Exception Exception is thrown if it is not possible to create object
	 */
	public abstract void create() throws Exception;

}
