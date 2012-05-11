package com.dmitrikuznetsov.dklib.fileio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.dmitrikuznetsov.dklib.constants.Constants;

/**
 * Single file to work with
 * 
 * @author dmitrikuznetsov
 *
 */
public class FileInfo extends AbstractFileInfo
{
	
	public FileInfo(File file) {
		super(file);
	}

	public FileInfo(String file) {
		super(file);
	}
	

	/**
	 * Appends data to the file
	 * 
	 * @param data			Data to be append to the file
	 * @throws Exception 	Exception is thrown if appending to the file fails
	 */
	public void append(byte[] data) throws Exception
	{
		writeFileRaw(data, true);
	}
	
	
	/**
	 * Retrieves current file size
	 * 
	 * @return	Size of the file in bytes, 0 bytes if file doesn't exist
	 */
	public long getFileSize()
	{
		return _file.length();
	}
	
	/**
	 * Writes data to the file (either creates a new file or appends to it, based on arguments)
	 * 
	 * @param data			Data to be added to the file
	 * @param append		Is the file data needs to be appended
	 * @throws Exception    Exception is thrown if writing to the file fails
	 */
	protected void writeFileRaw(byte[] data, boolean append) throws Exception
	{
		FileOutputStream writer = null;
		
		try
		{
				
			writer = new FileOutputStream(_file, append);
			
			int bytesWritten = 0;
			int currentPos = 0;
			
			while(true)
			{
				
				//write to the buffer by specified size
				bytesWritten = Math.min( data.length, Constants.MAX_BUFFER_SIZE );
				
				writer.write(data, currentPos , bytesWritten );
				
				
				//continue writing until it is written or not
				if( (bytesWritten+currentPos) >= data.length )
				{
					break;
				}
				else
				{
					currentPos += bytesWritten;
				}

			}
			
			writer.flush(); //commit any changes
			
		}
		catch(Exception ex)
		{
			throw new Exception("Failed to write to the file = " + _file.getAbsolutePath() , ex );
		}
		finally
		{
			if( writer != null )
			{
				writer.close();
				writer = null;
			}
		}
	}
	
	
	/**
	 * Reads all the data of the file as byte array
	 * 
	 * @return	File data that was read
	 * @throws Exception  Exception is thrown if reading from file fails
	 */
	public byte[] readFromFile() throws Exception
	{
		FileInputStream 		reader = null;
		ByteArrayOutputStream 	baos = new ByteArrayOutputStream();
		
		try
		{
				
			reader = new FileInputStream(_file);
			
			byte[] buffer = new byte[ Constants.MAX_BUFFER_SIZE ];
			int bytesRead = 0;
			
			while(true)
			{
				bytesRead = reader.read(buffer, 0, Constants.MAX_BUFFER_SIZE );
				
				if( bytesRead == -1)
					break;
				
				baos.write( buffer , 0 , bytesRead );
			}
			
			return baos.toByteArray();
		}
		catch(Exception ex)
		{
			throw new Exception("Failed to read from file = " + _file.getAbsolutePath() );
		}
		finally
		{
			if( reader != null )
			{
				reader.close();
				reader = null;
			}
			
			if( baos != null )
			{
				baos.close();
				baos = null;
			}
		}
	}
	
	
	/**
	 * Moves file from source to destination - basically the same as copyTo with delete source parameter
	 * 
	 * @param destinationFilename	Destination filename
	 * @throws Exception            Throws exception if copying file to destination catalog fails
	 */
	public void moveTo(FileInfo destinationFilename ) throws Exception
	{
		copyTo( destinationFilename, true ); //delete the source
	}
	
	
	
	/**
	 * Copies file contents to destination filename
	 * 
	 * @param destinationFilename	Destination filename
	 * @throws Exception            Throws exception if copying file to destination catalog fails
	 */
	public void copyTo(FileInfo destinationFilename ) throws Exception
	{
		copyTo( destinationFilename, false ); //don't delete source
	}
	
	
	
	/**
	 * Copies file contents to destination filename
	 * 
	 * @param destinationFilename	Destination filename
	 * @throws Exception            Throws exception if copying file to destination catalog fails
	 */
	public void copyTo(FileInfo destinationFilename, boolean deleteSource) throws Exception
	{
		 FileInputStream in = null;
	     FileOutputStream out = null;
	     
	     if( _file.getAbsolutePath().equals( destinationFilename._file.getAbsolutePath() ))
	     {
	    	 throw new Exception("Source and destination files are the same!");
	     }
	     
	     try 
	     {
	            in = new FileInputStream( _file.getAbsoluteFile() );
	            out = new FileOutputStream( destinationFilename._file.getAbsoluteFile());
	            
	            byte[] buffer = new byte[ Constants.MAX_BUFFER_SIZE ];
	            int bytesRead =  0;
	            
	            while ( true ) 
	            {
	            	bytesRead = in.read(buffer, 0 , Constants.MAX_BUFFER_SIZE);
	            	
	            	//EOF found ?
	            	if( bytesRead == -1)
	            		break;
	            	
	                out.write(buffer, 0 , bytesRead );
	            }

	     } 
	     catch(Exception ex)
	     {
	    	 throw new Exception("Failed to copy file from " + _file.getAbsolutePath() + " to " + destinationFilename._file.getAbsolutePath() );
	     }
	     finally 
	     {
	        if (in != null) 
	        {
	           in.close();
	           in = null;
	           
	           if( deleteSource )
	           {
	        	   delete(false);
	           }
	        }
	        
	        if (out != null) 
	        {
	           out.close();
	           out = null;
	        }
	     }
	}
	
	

	/**
	 * Creates new empty file if it doesn't exist
	 * 
	 * By default doesn't overwrite a file but gives an error message
	 * 
	 * @throws Exception Exception is thrown if it is not possbile to create a file
	 */
	public void create() throws Exception
	{
		create(false);
	}
	
	
	/**
	 * Creates new empty file if it doesn't exist
	 * 
	 * @param 	overwrite	Indicates that file needs to be overwritten
	 * 
	 * @throws Exception Exception is thrown if it is not possbile to create a file
	 */
	public void create(boolean overwrite) throws Exception
	{
		if( _file.exists() )
		{
			if( !overwrite )
			{	
				throw new Exception("Cannot create new file =" + _file.getAbsolutePath() + " it already exists!");
			}
			else
			{
				//just delete file then
				_file.delete();
			}
		}
		
		//try to create new file
		if( !_file.createNewFile() )
		{
			throw new Exception("Failed to create new file = " + _file.getAbsolutePath() );
		}
	}
	
	
	/**
	 * Creates new file with initial data
	 * 
	 * @param 	overwrite	Indicates that file needs to be overwritten
	 * @param 	initialData Initial data that will be written to the file
	 * @throws Exception    Exception is thrown if it is not possbile to create a file
	 */
	public void create(byte[] initialData, boolean overwrite) throws Exception
	{
		writeFileRaw(initialData, false);
	}
}
