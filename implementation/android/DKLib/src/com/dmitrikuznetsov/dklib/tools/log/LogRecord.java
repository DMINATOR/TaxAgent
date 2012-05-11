package com.dmitrikuznetsov.dklib.tools.log;


/**
 * Single record from the log
 * 
 * @author dmitrikuznetsov
 *
 */
public class LogRecord 
{
	
	/**
	 * Indicates this log contains local warning messages
	 */
	public static final int		LOG_TYPE_WARNING		= 0;
	

	/**
	 * Indicates this log contains local error messages
	 */
	public static final int		LOG_TYPE_ERRORS			= 1;
	
	
	/**
	 * Indicates this log contains system messages from logtag
	 */
	public static final int		LOG_TYPE_SYSTEM			= 2;
	
	
	
	/**
	 * Type of log message - warning 'W'
	 */
	public static final int 	LOG_MESSAGE_TYPE_WARNING	= 0;
	
	/**
	 * Type of log message - error 'E'
	 */
	public static final int		LOG_MESSAGE_TYPE_ERROR		= 1;
	
	/**
	 * Type of log message - info 'I'
	 */
	public static final int		LOG_MESSAGE_TYPE_INFO		= 2;
	
	/**
	 * Type of log message - debug 'D'
	 */
	public static final int		LOG_MESSAGE_TYPE_DEBUG		= 3;
	
	/**
	 * Type of log message - warning 'V'
	 */
	public static final int		LOG_MESSAGE_TYPE_VERBOSE	= 4;
	
	
	/**
	 * Type of log, undefined by default
	 */
	private int 				_logType = -1;
	
	
	/**
	 * Type of message stored by the log
	 */
	private int 				_logMessageType = -1;
	
	/**
	 * Time of the message as string hh:mm.ss.mmm
	 */
	private String				_logTime = "";
	
	/**
	 * Log tag to identify message writer
	 */
	private String				_logTag = "";
	
	
	/**
	 * Process ID associated with this log message
	 */
	private int					_processId = -1;
	
	
	/**
	 * Actual message that was written to the log
	 */
	private String				_message = "";
	
	
	/**
	 * Creates new log record message
	 * 
	 * @param logType			Type of log to use, one value from LOG_TYPE_* enum
	 * @param logMessageType	Log message type, one value from LOG_MESSAGE_TYPE_* enum
	 * @param logTime			Time of the message log
	 * @param logTag			Log tag associated with this message
	 * @param processId			Process ID that wrote this message
	 * @param message			Message itself
	 */
	public LogRecord(int logType, int logMessageType, String logTime, String logTag, int processId, String message)
	{
		_logType 			= logType;
		_logMessageType 	= logMessageType;
		_logTime			= logTime;
		_logTag				= logTag;
		_processId			= processId;
		_message			= message;
	}
	
	
	/**
	 * Returns log record type
	 * 
	 * @return Type of log to use, one value from LOG_TYPE_* enum
	 */
	public int getLogType()
	{
		return _logType;
	}

	/**
	 * Returns log message type
	 * 
	 * @return Log message type, one value from LOG_MESSAGE_TYPE_* enum
	 */
	public int getLogMessageType()
	{
		return _logMessageType;
	}
	
	
	/**
	 * Returns log time
	 * 
	 * @return Log time associated with this message
	 */
	public String getLogTime()
	{
		return _logTime;
	}
	
	
	/**
	 * Returns log tag
	 * 
	 * @return Log tag associated with this message
	 */
	public String getLogTag()
	{
		return _logTag;
	}
	
	
	/**
	 * Returns process Id
	 * 
	 * @return Process ID that wrote this message
	 */
	public int getProcessId()
	{
		return _processId;
	}
	
	
	/**
	 * Returns Message
	 * 
	 * @return Message itself
	 */
	public String getMessage()
	{
		return _message;
	}
}
