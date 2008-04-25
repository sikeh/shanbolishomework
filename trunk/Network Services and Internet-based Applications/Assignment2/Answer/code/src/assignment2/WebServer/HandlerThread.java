
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * Created on Jan 6, 2005
 * This file is created as assignment 1. WebMail in the course 2G1333
 * See the attached README file for instructions on starting
 * Author: Mikael Rudholm 
 */

/**
 * @author Mikael Rudholm
 */
public class HandlerThread extends Thread {
	private Socket socket;

	// Thread cunstructor
	HandlerThread(Socket socket) throws IOException {
		this.socket = socket;
	}

	public void run() {
		PrintWriter out = null;
		BufferedReader in = null;
		char[] inBuff = new char[10000];
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			String inputLine="", outputLine;
			ServerHandlerProtocol shp = new ServerHandlerProtocol();
			int charsRead = 0;
			
			while (in.ready()){
				charsRead = in.read(inBuff, 0, 10000);
				inputLine += String.valueOf(inBuff,0, charsRead); 
			}
			
			String proccessedLine;
			int inLineLength = inputLine.split("(\r\n|\r|\n)").length;
			for (int i = 0; i < inLineLength; i++) {
				proccessedLine = inputLine.split("(\r\n|\r|\n)")[i];
				shp.processInput(proccessedLine);
				System.out.println(proccessedLine);
			}

			outputLine = shp.getOutput();
			out.print(outputLine);

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}