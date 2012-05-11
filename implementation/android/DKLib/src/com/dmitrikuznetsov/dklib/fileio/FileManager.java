package com.dmitrikuznetsov.dklib.fileio;

import android.content.Context;

/**
 * Manages all files on the device
 * 
 * @author dmitrikuznetsov
 *
 */
public class FileManager 
{
	
	/**
	 * Retrieves directory where application data files are located
	 * 
	 * @param 	Applcation context parameter is needed to retrieve actual data directory
	 * 
	 * @return	Path to the main application data directory
	 */
	public static DirectoryInfo getApplicationDataDirectory(Context context)
	{
		return new DirectoryInfo( context.getFilesDir() );
	}
}
