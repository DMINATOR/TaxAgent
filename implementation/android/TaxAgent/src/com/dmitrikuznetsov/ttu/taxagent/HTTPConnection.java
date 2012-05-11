package com.dmitrikuznetsov.ttu.taxagent;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.json.JSONObject;

import com.dmitrikuznetsov.dklib.tools.log.LogWriter;


/**
 * Makes HTTP Connection with a server
 * 
 * @see <a href="http://www.androidsnippets.org/snippets/36/index.html">example</a>
 * 
 * @author Dmitri Kuznetsov
 *
 */
public class HTTPConnection
{
	
	/**
	 * URL is stored here
	 */
	private String url = null;
	
	/**
	 * Defaul constructor
	 * 
	 * @param URL	URL where connection should be made
	 */
	public HTTPConnection(String URL)
	{
		url = URL;
	}
	
	/**
	 * Makes actual connection with a server
	 * 
	 * @param values Actual values to send to the server
	 * 
	 * @return HTTP Response from the server
	 * @throws Exception Exception is thrown if HTTP response fails
	 */
	public HttpResponse connect(JSONObject jsonObject) throws Exception
	{
		// Create a new HttpClient and Post Header   
	    HttpClient httpclient = new DefaultHttpClient();   
	    HttpPost httppost = new HttpPost(url);   
	  
	    try 
	    {   
	        // Add your data   
	    	/*
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);   
	        nameValuePairs.add(new BasicNameValuePair("id", "12345"));   
	        nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));   
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));   */
	    	//httppost.setEntity(new UrlEncodedFormEntity(values));
	    	
	    	//ByteArrayEntity data = new ByteArrayEntity(jsonObject.toString().getBytes("UTF8"));
	    	
	    	//httppost.setEntity(data );
	    	
	    	/*
	    	String json = jsonObject.toString();
	    	
	    	httppost.setHeader("Content-type", "application/json");

	        StringEntity se = new StringEntity( json ); 
	        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	        httppost.setEntity(se); */
	    	
	    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);   
	    	nameValuePairs.add(new BasicNameValuePair("json", jsonObject.toString())); 
	    	
	    	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    	   
	        // Execute HTTP Post Request   
	        HttpResponse response = httpclient.execute(httppost);   

	           
	        return response;
	    } 
	    catch (Exception e) 
	    {   
	        throw new Exception("Failed to create HTTP POST (" + e.getClass().getName() + ") = " +  e.getMessage() , e);
	    }   
	}
	
	/**
	 * Get response from a server as String
	 * 
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	public static String getResponse(HttpResponse response) throws Exception
	{
		 HttpEntity entity 		= response.getEntity();
		 
		 if( entity != null )
		 {
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) 
			{
				 byte[] result = new byte[(int) entity.getContentLength()];
				 
	             if(entity.isStreaming()) 
	             {
	            	DataInputStream is = new DataInputStream(entity.getContent()); // Comment: 6
	            	is.readFully(result);
	             }
	             
	             String res = new String(result);
	             return res;
			} 
			else 
			{
				throw new Exception("Error Code: " + statusCode);
			}
				
			
		 }
		 
		 
		 //return null - any other case
		 return null;
	}
	
	public JSONObject connectJSON(JSONObject jsonObject) throws Exception
	{
		String responseText = "";
		
		try
		{
			LogWriter.writeInfo("[JSONConnect]: REQUEST ("+url+"): " + jsonObject.toString());
			HttpResponse response = connect(jsonObject);
			responseText = HTTPConnection.getResponse(response);
			JSONObject object = new JSONObject( responseText );
			
			LogWriter.writeInfo("[JSONConnect]: RESPONSE:" + object.toString());
			
			return object;
		}
		catch(Exception ex)
		{
			LogWriter.writeDebug("Response: " + responseText );
			throw new Exception("Failed to connect with JSON!" , ex );
		}
	}
}
