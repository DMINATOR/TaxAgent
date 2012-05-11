package com.dmitrikuznetsov.dklib.tools.log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.dmitrikuznetsov.dklib.constants.Constants;
import com.dmitrikuznetsov.dklib.fileio.FileInfo;
import com.dmitrikuznetsov.dklib.fileio.FileManager;

import android.content.Context;
import android.util.Log;

/**
 * Custom logger, that writes messages both to system log
 * and to external file
 * 
 * @author dmitrikuznetsov
 *
 */
public class LogWriter 
{
	/**
	 * Maximum file size of the log to keep
	 */
	public static final long	MAX_FILE_SIZE			= 100000;
	
	
	/**
	 * Log tag to use
	 */
	public static final String 	LOG_TAG					= "DKLIB";
	
	
	/**
	 * Warning messages are stored in this file
	 */
	public static final String	LOG_WARNING_FILENAME	= "warnings_log.txt";
	
	
	
	/**
	 * Errors messages are stored in this file
	 */
	public static final String	LOG_ERRORS_FILENAME		= "errors_log.txt";
	
	
	/**
	 * Indicates that normal info message needs to be written
	 */
	public static final int		LOG_MESSAGE_TYPE_INFO		= 0;
	
	
	/**
	 * Indicates that debug message needs to be written to the log
	 */
	public static final int		LOG_MESSAGE_TYPE_DEBUG		= 1;
	
	
	/**
	 * Indicates that warning message needs to be written
	 */
	public static final int		LOG_MESSAGE_TYPE_WARNING	= 2;
	

	/**
	 * Indicates that error message needs to be written
	 */
	public static final int		LOG_MESSAGE_TYPE_ERROR		= 3;
	

	
	
	/**
	 * Writes message to the log, raw function, check if file is need to be deleted or not
	 * 
	 * @param context		Context used for writing to the file (if null - nothing will be written to the file)
	 * @param messageType	Type of message that needs to be written one value from {@link LogWriter}.LOG_MESSAGE_TYPE_* enum
	 * @param message		Message to write to the log
	 */
	protected static void write(Context context, int messageType, String message)
	{
		//file to use for writing
		FileInfo info = null;
		
		//for warnings and errors we write to different output source - physical log text file
		if( (messageType == LogWriter.LOG_MESSAGE_TYPE_WARNING)||(messageType == LogWriter.LOG_MESSAGE_TYPE_ERROR) )
		{
			if( context != null )
			{
				if( messageType == LogWriter.LOG_MESSAGE_TYPE_WARNING )
				{
					info = new FileInfo( FileManager.getApplicationDataDirectory(context) + "/" + LogWriter.LOG_MESSAGE_TYPE_WARNING);
				}
				else
				{
					info = new FileInfo( FileManager.getApplicationDataDirectory(context) + "/" + LogWriter.LOG_MESSAGE_TYPE_ERROR);	
				}

				//check if size is too big
				if( info.getFileSize() > LogWriter.MAX_FILE_SIZE )
				{
					//just delete the file now
					try
					{
						info.delete(false);
					}
					catch(Exception ex)
					{
						//basically this doesn't happen, but who knows
						System.err.println("Failed to delete log =" + ex.toString() );
					}
				}

				//append new block of data to this file
				try
				{
					//append additional information for checking later
					//should be in format mm-dd hh:mm:ss.mmm T/(pid): Message
					Calendar cal = new GregorianCalendar();
					cal.setTime( new Date() );
					StringBuffer temp = new StringBuffer();
					
					//format message for the log
					
					//month:
					temp.append( cal.get( Calendar.MONTH ) );
					temp.append( "-");
					temp.append( cal.get( Calendar.DAY_OF_MONTH ) );
					temp.append( " ");
					
					//day:
					temp.append( cal.get( Calendar.HOUR_OF_DAY ) );
					temp.append( ":");
					temp.append( cal.get( Calendar.MINUTE ) );
					temp.append( ":");
					temp.append( cal.get( Calendar.SECOND ) );
					temp.append( ".");
					temp.append( cal.get( Calendar.MILLISECOND ) );
					temp.append( " ");
					
					if( messageType == LogWriter.LOG_MESSAGE_TYPE_WARNING )
					{
						temp.append( "W");
					}
					else
					{
						temp.append( "E");
					}
					temp.append( "/");
					temp.append( LogWriter.LOG_TAG );
					temp.append( "(");
					temp.append(  android.os.Process.myPid() );
					temp.append( "): ");
					
					//also include message length, so it can be used later to quickly parse it
					temp.append( "{");
					temp.append( message.length() ); 
					temp.append( "}");
					
					temp.append( message );
					
					info.append( temp.toString().getBytes() );
				}
				catch(Exception ex)
				{
					//who knows when it can happen - but not much we can do about it
					System.err.println("Failed to append to the log =" + ex.toString() );
				}
			}
		}
		
		//and write to the usual log
		switch(messageType)
		{
			case LogWriter.LOG_MESSAGE_TYPE_DEBUG:
				Log.d( LogWriter.LOG_TAG , message);
				break;
				
				
			case LogWriter.LOG_MESSAGE_TYPE_INFO:
				Log.i( LogWriter.LOG_TAG , message);
				break;
				
				
			case LogWriter.LOG_MESSAGE_TYPE_WARNING:
				Log.w( LogWriter.LOG_TAG , message);
				break;
				
			default:
			case LogWriter.LOG_MESSAGE_TYPE_ERROR:
				Log.e( LogWriter.LOG_TAG , message);
				break;
		}
	}
	
	
	/**
	 * Writes info message to the system log.
	 * 
	 * @param message		Message to write to the log
	 */
	public static void writeInfo( String message)
	{
		write( null , LogWriter.LOG_MESSAGE_TYPE_INFO , message );
	}
	
	
	/**
	 * Writes debug message to the system log.
	 * 
	 * @param message		Message to write to the log
	 */
	public static void writeDebug( String message)
	{
		write( null , LogWriter.LOG_MESSAGE_TYPE_DEBUG , message );
	}
	
	/**
	 * Writes error message to the system log and to the physical file for future reference
	 * 
	 * @param context		Context used for writing to the file (if null - nothing will be written to the file)
	 * @param message		Message to write to the log
	 */
	public static void writeError(Context context, String message)
	{
		write(context , LogWriter.LOG_MESSAGE_TYPE_ERROR , message );
	}
	
	/**
	 * Writes warning message to the system log and to the physical file for future reference
	 * 
	 * @param context		Context used for writing to the file (if null - nothing will be written to the file)
	 * @param message		Message to write to the log
	 */
	public static void writeWarning(Context context, String message)
	{
		write(context , LogWriter.LOG_MESSAGE_TYPE_WARNING , message );
	}
	
	
	
	/**
	 * Writes exception details recursively
	 * 
	 * @param level		Initial exception level. 0 - base exception, 1 - inner exception
	 * @param t			Throwable error message to parse and retrieve details from
	 * @param bufferOut	Output is written to the buffer
	 */
	private static void writeExceptionDetails(int level, Throwable t, StringBuffer bufferOut)
	{
		//write general exception message details
		if( level > 0 )
		{
			bufferOut.append("  [");
			bufferOut.append( level );
			bufferOut.append("]");
		}
		
		bufferOut.append("(");
		bufferOut.append( t.getClass().toString() );
		bufferOut.append(") Message = ");
		bufferOut.append( t.getMessage() );
		bufferOut.append( Constants.LF );
		
		//write stack trace
		StackTraceElement[] elements = t.getStackTrace();
		
		for(int i = 0; i < elements.length; i++ )
		{
			StackTraceElement element = elements[i];
			
			if( level > 0 )
			{
				bufferOut.append( "  " );
			}
			
			bufferOut.append("at [");
			bufferOut.append( i );
			bufferOut.append("]");
			
			bufferOut.append("(");
			bufferOut.append( element.getFileName() );
			bufferOut.append(")");

			bufferOut.append(" - ");
			bufferOut.append( element.getMethodName());
			bufferOut.append(" (");
			bufferOut.append( element.getLineNumber());
			bufferOut.append(")");
			
			bufferOut.append( Constants.LF );
		}
		
		Throwable inner = t.getCause();
		
		//write inner exception
		if( inner != null )
		{
			writeExceptionDetails( level+1 , inner , bufferOut );
		}
	}
	
	/**
	 * Writes and formats exception message and writes it to the log
	 * 
	 * @param context	Context that will be used for writing to the log file
	 * @param message	Message that will be written
	 * @param ex		Exception that will be formatted and written
	 */
	public static void writeException(Context context, String message, Exception ex)
	{
		StringBuffer result = new StringBuffer();
		
		result.append("[Exception] = ");
		result.append(message);
		result.append( Constants.LF );
		
		//write only if it is not null
		if( ex != null )
		{
			writeExceptionDetails( 0 , ex , result );
		}
		
		//finally write it to the log
		write(context , LogWriter.LOG_MESSAGE_TYPE_ERROR , result.toString() );
	}
	
	
	/**
	 * Writes exception to the log and throws it further towards the stack
	 * 
	 * @param context		Context that will be used for writing to the log file
	 * @param message		Message that will be written
	 * @param ex			Exception that will be formatted and written
	 * 
	 * @throws Exception 	Exception is thrown futher
	 */
	public static void writeAndThrowException(Context context, String message, Exception ex) throws Exception
	{
		writeException( context, message, ex);
		
		throw ex;
	}
	

}
