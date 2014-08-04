/**
 * 
 */
package com.zimbra;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * @author kartik
 *
 */
public class FileUpload
{

	    private Socket clientConn;
	    
	    private int contentLength=-1;
	    
	    private String uploadFileName;
	    
	    public FileUpload(Socket clientConn) throws Exception
	    {
	        this.clientConn=clientConn;
	        
	    }
	    
	    public void process(String fileloc) throws Exception
	    {
	    	//BufferedReader din=new BufferedReader(new InputStreamReader(clientConn.getInputStream()));
	        DataInputStream din = new DataInputStream(clientConn.getInputStream());
	        DataOutputStream dout = new DataOutputStream(clientConn.getOutputStream());
	        contentLength=parseContentLength();
	        System.out.println("File content length "+contentLength);
	        boolean success=false;
	        
	        String ServerLine= null;
            String StatusLine=null;
            
	        if(contentLength != -1)
	        {
	            
	            File targetdir = new File(fileloc);
	            if (targetdir.exists() && targetdir.isDirectory()){
	            	
	            	success=writeToFile(targetdir,uploadFileName,din);
	            }
	            else{
	            	targetdir.mkdir();
	            	success=writeToFile(targetdir,uploadFileName,din);
	            }
	            //check if directory is present???
	            
	            if (success){
	            	ServerLine="Simple HTTP Server";
	            	StatusLine="HTTP/1.0 200 OK";
	            }
	            else{
	            	ServerLine="Simple HTTP Server";
	            	StatusLine="HTTP/1.0 500 OK";  //internal server error
	            }
	            dout.write(StatusLine.getBytes());
            	dout.write( ServerLine.getBytes());
	            
	        }
	        else
	        {
	        	ServerLine="Simple HTTP Server";
            	StatusLine="HTTP/1.0 400 OK";  //client error
            	dout.write(StatusLine.getBytes());
            	dout.write( ServerLine.getBytes());
	        }
	        clientConn.close();
	    }

	private int parseContentLength() 
	{	 
		int postDataLen=-1;
		try 
		{
			BufferedReader breader = new BufferedReader(new InputStreamReader(clientConn.getInputStream()));
			String request=breader.readLine();
			System.out.println(request);
			StringTokenizer st = new StringTokenizer(request);
			String header=st.nextToken().trim();
			System.out.println("HTTP Request type "+header);
			uploadFileName=st.nextToken().substring(1);
			System.out.println("File to be uploaded: "+uploadFileName);
			
			if (header.equalsIgnoreCase("POST"))
			{
				 while ((request = breader.readLine()) != null && (request.length() != 0)) 
				 {
		                System.out.println("HTTP-HEADER: " + request);
		                if (request.indexOf("Content-Length:") > -1) 
		                {
		                    postDataLen = new Integer(
		                    		request.substring(
		                    				request.indexOf("Content-Length:") + 16,
		                    				request.length())).intValue();
		                }
		         }
				 
			}	
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		return postDataLen;
		
	}

	private boolean writeToFile(File targetdir, String fileName, DataInputStream din) 
	{
		boolean uploadSuccess=false;
		try {
			FileOutputStream fout = new FileOutputStream(
					targetdir.getAbsolutePath() + File.separator + fileName);
			
			BufferedInputStream bin = new BufferedInputStream(din);
			byte[] buffer = new byte[1024];
			while (bin.read(buffer) != -1) 
			{
				fout.write(buffer);
			}

			fout.close();
			uploadSuccess=true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		return uploadSuccess;
	}

}
