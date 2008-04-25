
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/*
 * Created on Jan 6, 2005
 * This file is created as assignment 1. WebMail in the course 2G1333
 * See the attached README file for instructions on starting 
 * Author: Mikael Rudholm 
 */

/**
 * @author Mikael Rudholm
 */

public class MultiThreadedServer {
	private static boolean listen;
	
	public static void main(String[] args) {
		int myPort = 8888;
		listen = true;
		ServerSocket serverSocket = null;
		
			
		try {
			serverSocket = new ServerSocket(myPort);
		} catch (IOException e) {
			System.out.println("Could not listen to port " + myPort);
			System.exit(-1);
		}
		System.out.println("SERVER: Started sucessfully!");
		System.out.println("SERVER: Now waiting for requests.");

		// set timeout so that we can cleanly shutdown the server
		try {
			serverSocket.setSoTimeout(5000);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (listen) {
			try {
				Socket clientSocket = serverSocket.accept();
				System.out.println("SERVER: Accepted client connection! Server:"+clientSocket.getLocalAddress().getHostName()+" Client:"+clientSocket.getInetAddress().getCanonicalHostName());
				HandlerThread handler = new HandlerThread(clientSocket);
				handler.setPriority(handler.getPriority() + 1);
				handler.start();
			}
			catch (SocketTimeoutException e)
			{
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// close the server socket
		
		System.out.println("SERVER: Now shuting down!");
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	} // end of public static void main
	
	public static void shutdownServer(){
		listen = false;
	}
}

