package com.dmitrikuznetsov.dklib.os;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Used to launch local OS process
 */
public class ProcessLauncher 
{
	/**
	 * Result after launching process
	 * @author dmitrikuznetsov
	 *
	 */
	public class ProcessLauncherResult
	{
		/**
		 * Exit value of the process when it is terminated,
		 * by default value = -1
		 */
		private int exitValue = -1;
		
		
		/**
		 * Result of process execution - data retrieved
		 */
		private StringBuffer output = new StringBuffer();
		
		
		/**
		 * Return exit value after termination
		 * @return
		 */
		public int getExitValue()
		{
			return exitValue;
		}
		
		
		/**
		 * Return output result after termination
		 * @return
		 */
		public String getOutput()
		{
			return output.toString();
		}
	}
	
	/**
	 * Launches the specified process
	 * 
	 * @param name 			Name of the process to launch
	 * @param arguments		(optional) arguments to pass to the process
	 * @throws Exception 	Exception is thrown if launching process fails
	 * 
	 * @return				Result of process execution
	 */
	public ProcessLauncherResult launch(String name, String[] arguments) throws Exception
	{
		ProcessLauncherResult result = new ProcessLauncherResult();
		
		//the array is immutable, for convinience let's add to array list
		List<String> args = new ArrayList<String>();
		args.add(name);
		args.addAll( Arrays.asList(arguments));
		  
		ProcessBuilder builder = new ProcessBuilder()
	       .command(args)
	       .redirectErrorStream(true);

	    Process process = null;
	       
		try
		{
			 process =  builder.start();

		     InputStream in = process.getInputStream();

		     byte[] buffer = new byte[1024];
		     int bytesRead = 0;
		     
		     while( (bytesRead = in.read(buffer)) != -1 )
		     {
		    	 result.output.append( new String( buffer , 0 , bytesRead) );
		     }
		     
		     result.exitValue = process.waitFor();

		}
		catch(Exception ex)
		{
			throw new Exception("Failed to launch process: " + name, ex);
		}
		finally
		{
			if( process != null )
			{
				result.exitValue = process.exitValue();
				process.destroy();
				process = null;
			}
		}
		
		return result;
	}
}
