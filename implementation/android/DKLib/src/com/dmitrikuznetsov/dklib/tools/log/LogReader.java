package com.dmitrikuznetsov.dklib.tools.log;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.dmitrikuznetsov.dklib.fileio.FileInfo;
import com.dmitrikuznetsov.dklib.fileio.FileManager;
import com.dmitrikuznetsov.dklib.os.ProcessLauncher;
import com.dmitrikuznetsov.dklib.os.ProcessLauncher.ProcessLauncherResult;

/**
 * Reads from existing system or local log
 * 
 * @author dmitrikuznetsov
 *
 */
public class LogReader 
{
	/**
	 * Returns information about currently used log files, if they actually exist or not,
	 * can be checked by returned values. 
	 * <p>
	 * The files can be manually deleted if needed.
	 * 
	 * @param context	Context is required for retrieving initial application catalog
	 * 
	 * @return
	 * List of used files
	 * <ul>
	 * 	<li>0 - Warning log file</li>
	 * 	<li>1 - Error log file</li>
	 * </ul>
	 */
	public static FileInfo[] getLogFilesInfo(Context context)
	{
		FileInfo[] results = new FileInfo[2];
		
		results[0] = new FileInfo( FileManager.getApplicationDataDirectory(context).getAbsoluteLocation() + "/" + LogWriter.LOG_MESSAGE_TYPE_WARNING) ;
		results[1] = new FileInfo( FileManager.getApplicationDataDirectory(context).getAbsoluteLocation() + "/" + LogWriter.LOG_MESSAGE_TYPE_ERROR) ;
		
		return results;
	}
	
	
	/**
	 * Retrieves list of log records based on the specified log type
	 * 
	 * @param logType Type of log used, to retrieve data only from specific log, use values from LogRecord.LOG_TYPE_*
	 * 
	 * @return			List of records that were retrieved 
	 * @throws Exception Exception is thrown if retrieving log fails
	 */
	public static List<LogRecord> getLogRecords(Context context, int logType) throws Exception
	{
		switch(logType)
		{
			case LogRecord.LOG_TYPE_ERRORS:
				return getLocalLog( logType, getLogFilesInfo(context)[1] );
				
			
			case LogRecord.LOG_TYPE_WARNING:
				return getLocalLog( logType , getLogFilesInfo(context)[0] );
				
				
			case LogRecord.LOG_TYPE_SYSTEM:
				return getSystemLog();
				
				
			//unexpected
			default:
				throw new IllegalArgumentException("Unexpected log type = " + logType );
		}
	}
	
	
	/**
	 * Retrieves and parses log contents from local files
	 * 
	 * @param logType	Type of log used one value from {@link LogRecord}.LOG_TYPE_* enum
	 * @param file	Log fileinfo to retrieve data from
	 * @return		List of log records if it is possible to parse
	 * @throws Exception Exception is thrown if reading from file fails
	 */
	private static List<LogRecord> getLocalLog(int logType, FileInfo file) throws Exception
	{	
		return parseLogData(logType, new String( file.readFromFile() ) );
	}
	
	/**
	 * Parses data from log to the formatted structure
	 * <br/>
	 * Example input:
	 * <pre>
	 * 10-14 13:50:49.159 D/TelephonyProvider(  127): Setting numeric '310260' to be the current operator
	 * 10-14 13:50:49.300 I/TelephonyRegistry(   61): notifyDataConnection: state=1 isDataConnectivityPossible=true reason=simLoaded interfaceName=null networkType=3
	 * 10-14 13:50:49.339 D/dalvikvm(  197): GC_CONCURRENT freed 370K, 54% free 2615K/5639K, external 716K/1038K, paused 11ms+80ms
	 * 10-14 13:50:49.489 I/TelephonyRegistry(   61): notifyDataConnection: state=2 isDataConnectivityPossible=true reason=simLoaded interfaceName=/dev/omap_csmi_tty1 networkType=3
	 * 10-14 13:50:49.649 D/Tethering(   61): MasterInitialState.processMessage what=3
	 * 10-14 13:50:49.749 W/ActivityManager(   61): Activity idle timeout for HistoryRecord{406628f8 com.android.launcher/com.android.launcher2.Launcher}
	 * 10-14 13:50:49.759 D/PowerManagerService(   61): bootCompleted
	 * 10-14 13:50:49.759 D/dalvikvm(  135): GC_EXTERNAL_ALLOC freed 55K, 52% free 2761K/5639K, external 1567K/1572K, paused 561ms
	 * 10-14 13:50:49.849 D/VoldCmdListener(   29): volume mount /mnt/sdcard
	 * 10-14 13:50:49.849 I/Vold    (   29): /dev/block/vold/179:0 being considered for volume sdcard
	 * 10-14 13:50:49.849 D/Vold    (   29): Volume sdcard state changing 1 (Idle-Unmounted) -> 3 (Checking)
	 * 10-14 13:50:49.859 I/StorageNotification(  129): Media {/mnt/sdcard} state changed from {unmounted} -> {checking}
	 * 10-14 13:50:49.929 D/AndroidRuntime(  196): 
	 * </pre>
	 * 
	 * @param logType	Type of log used one value from {@link LogRecord}.LOG_TYPE_* enum
	 * @param data		Data from log to be parsed
	 * @return			List of log records
	 * @throws Exception Thrown when parsing log data fails
	 */
	protected final static List<LogRecord> parseLogData(int logType, String data) throws Exception
	{
		List<LogRecord> result = new ArrayList<LogRecord>();
		
		
		String logTime = "";
		int logMessageType = -1;
		String logTag = "";
		String logProcessId = "";
		String logMessage = "";
		
		int step = 0;
		char c = ' ';
		int cntr = 0;
		StringBuffer buffer = new StringBuffer();
		
		while(true)
		{
			//end should be reached
			if( data.length() <= cntr )
				return result;
			
			c = data.charAt(cntr);
			
			switch(step)
			{
				//read date
				case 0:
					if( c == ' ')
					{
						step++; // -> TIME
					}
					break;
					
				//read time
				case 1:
					if( c == ' ')
					{
						logTime = buffer.toString();
						buffer.setLength(0);
						step++; // -> LOG TAG
					}
					else
					{
						buffer.append(c);
					}
					break;
					
				//read log type
				case 2:
					//one character only
					c = Character.toLowerCase(c);
					
					if( c == 'd')
					{
						logMessageType = LogRecord.LOG_MESSAGE_TYPE_DEBUG;
					}
					else if ( c == 'w')
					{
						logMessageType = LogRecord.LOG_MESSAGE_TYPE_WARNING;
					}
					else if ( c == 'v')
					{
						logMessageType = LogRecord.LOG_MESSAGE_TYPE_VERBOSE;
					}
					else if ( c == 'e')
					{
						logMessageType = LogRecord.LOG_MESSAGE_TYPE_ERROR;
					}
					else if ( c == 'i')
					{
						logMessageType = LogRecord.LOG_MESSAGE_TYPE_INFO;
					}
					else
					{
						//maybe don't need to throw an error here ? who knows , just put info maybe
						logMessageType = LogRecord.LOG_MESSAGE_TYPE_INFO;
					}
					
					step ++;  // -> PROCESS ID
					//to skip /
					cntr++;
					buffer.setLength(0);
					
					break;
					
				//read log tag
				case 3:
					
					if( c == ' ')
					{
						//skip
					}
					else if ( c == '(')
					{
						logTag = buffer.toString();
						buffer.setLength(0);
						step++; // -> process id
					}
					else
					{
						buffer.append(c);
					}
					
					break;
					
				//read process id
				case 4:
					if( c == ')')
					{
						logProcessId = buffer.toString();
						buffer.setLength(0);
						step++; // -> message
						
						//to skip :_
						cntr = cntr + 2 ;
					}
					else if ( c == ' ')
					{
						//skip spaces
					}
					else
					{
						buffer.append(c);
					}
					
					break;
					
				//message
				case 5:
					if( logType == LogRecord.LOG_TYPE_ERRORS || logType == LogRecord.LOG_TYPE_WARNING )
					{
						//our own log, we can parse it and we know the message size 
						if( c == '{' || c == '}')
						{
							if( c == '}')
							{
								//done size is known
								int size = Integer.valueOf( buffer.toString() );
								
								//read remaining chars
								logMessage = data.substring( cntr + 1 , cntr + size + 1);
								
								//create new log record from the values we have parsed
								LogRecord record = new LogRecord(
										logType,
										logMessageType,
										logTime,
										logTag,
										Integer.valueOf(logProcessId),
										logMessage
										);
								
								result.add(record);
								
								//start from 0 again
								step = 0;
							}
							else
							{
								//skip char
							}
							
						}
						else
						{
							//read next char
							buffer.append(c);
						}
					}
					else
					{
						if( (c == '\n')||( data.length() <= (cntr + 1 )))
						{
							//if we reached the last character in string
							if( (c != '\n')&&( data.length() <= (cntr + 1 )) )
							{
								buffer.append(c);
							}
							
							//end of line - done
							logMessage = buffer.toString();
							buffer.setLength(0);
							
							//create new log record from the values we have parsed
							LogRecord record = new LogRecord(
									logType,
									logMessageType,
									logTime,
									logTag,
									Integer.valueOf(logProcessId),
									logMessage
									);
							
							result.add(record);
							
							//start from 0 again
							step = 0;
						}
						else
						{
							buffer.append(c);
						}
					}
					
					break;
					
				default:
					throw new Exception("Unknown log step = " + step);
			}
			
			//increase counter
			cntr++;
		}	
	
	}
	
	/**
	 * Retrieves and parses log contents from system (log cat) message
	 * 
	 * @return	List of log records
	 * @throws Exception Thrown when reading system log fails
	 */
	protected static List<LogRecord> getSystemLog() throws Exception
	{
		
		try
		{
			ProcessLauncher launcher = new ProcessLauncher();
			
			ProcessLauncherResult processResult = launcher.launch("logcat",
					new String[]{ 
						new String( "-d") , 
						new String( "-v") ,
						new String( "time")
					});
			
			if( processResult.getExitValue() != 0)
			{
				throw new Exception("logcat failed with exit code [" + processResult.getExitValue() + "] (" + processResult.getOutput().substring(0,200));
			}
			
			//all ok - parse the input and add result to log file
			String bufferResponse = processResult.getOutput();
			
			return parseLogData( LogRecord.LOG_TYPE_SYSTEM, bufferResponse);
		}
		catch(Exception ex)
		{
			throw new Exception("Failed to load system log" , ex);
		}	
	}

}
