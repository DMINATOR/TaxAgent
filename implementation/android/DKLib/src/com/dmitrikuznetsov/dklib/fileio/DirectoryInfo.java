package com.dmitrikuznetsov.dklib.fileio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * General information about directory
 * 
 * @author dmitrikuznetsov
 *
 */
public class DirectoryInfo extends AbstractFileInfo
{

	public DirectoryInfo(File directory) {
		super(directory);
	}

	public DirectoryInfo(String directory) {
		super(directory);
	}
	
	
	/**
	 * Creates new directory, including complete
	 * path to this directory
	 */
	public void create()
	{
		_file.mkdirs();
	}
	
	/**
	 * Get list of directories
	 * 
	 * @return	List of directories
	 */
	public ArrayList<DirectoryInfo> getDirectories()
	{
		String[] dirs = _file.list();
		
		ArrayList<DirectoryInfo> result = new ArrayList<DirectoryInfo>(); //don't forget to add absolute location!
		
		if( dirs != null )
		{
			for(int i = 0; i < dirs.length; i++ )
			{
				File file = new File(_file.getAbsolutePath() + "/" + dirs[i]);
				
				//add only directories
				if( file.isDirectory() )
				{
					result.add( new DirectoryInfo(file) );
				}
			}
		}
		
		//return list of directories
		return result;
	}
	
	/**
	 * Get list of files in current directory
	 * 
	 * @return	List of directories
	 */
	public ArrayList<FileInfo> getFiles()
	{
		String[] files = _file.list();
		
		ArrayList<FileInfo> result = new ArrayList<FileInfo>();
		
		if( files != null )
		{
			for(int i = 0; i < files.length; i++ )
			{
				File file = new File(_file.getAbsolutePath() + "/" +files[i]); //don't forget to add absolute location!
				
				//add only directories
				if( !file.isDirectory() )
				{
					result.add( new FileInfo(file) );
				}
			}
		}
		
		//return list of directories
		return result;
	}
	
	
	/**
	 * Deletes all the files in current directory
	 * 
	 * @param  errorIfFails	Show error message if operation fails
	 * 
	 * @throws IOException Exception is thrown if deleting directory fails
	 */
	@Override
	public void delete(boolean errorIfFails) throws IOException
	{
		//first get a list of files
		ArrayList<FileInfo> files = getFiles();
		
		//now delete them
		for(int i = 0; i < files.size(); i++)
		{
			files.get(i).delete(errorIfFails);
		}
		
		//now get a list of folders and delete them recursively
		ArrayList<DirectoryInfo> dirs = getDirectories();
		
		//now delete them
		for(int i = 0; i < dirs.size(); i++)
		{
			dirs.get(i).delete(errorIfFails);
		}
		
		//delete directory itself
		super.delete(errorIfFails);
	}
}
