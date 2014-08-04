package com.zimbra;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 */

/**
 * @author kartik
 *
 */
public class SimpleHTTPServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		int portnum = Integer.parseInt(args[0]);
		System.out.println("listening on port "+portnum);
		String fileloc = args[1];
		System.out.println("server upload dir "+fileloc);
		try {
			ServerSocket socket=new ServerSocket(portnum);
			while(true)
	        {
	            Socket inSocket=socket.accept();
	            FileUpload request=new FileUpload(inSocket);
	            request.process(fileloc);
	        }
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
