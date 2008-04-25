package assignment2.WebServer;/*
 * Created on Jan 6, 2005
 * 
 * This file is created as assignment 1. WebMail in the course 2G1333
 * See the attached README file for instructions on starting 
 * Author: Mikael Rudholm 
 */

/**
 * @author Mikael Rudholm
 */

import assignment2.WebServer.HTTPRequest;

import java.io.*;
import java.net.URLDecoder;

public class ServerHandlerProtocol {

	private static int numOfClients = 0;

	private int state;

	private static int DATANEXT = 1;

	private int myID;

	private HTTPRequest httpreq;

	ServerHandlerProtocol() {
		/*
		 * Assigns the first process the id 0 and then increment the
		 * numOfClients
		 */
		myID = numOfClients++;
		httpreq = new HTTPRequest();
		state = 0;
	}

	public String processInput(String theInput) {
		String theOutput = null;

		if (theInput != null) {
			if (theInput.startsWith("GET")) {
				httpreq.cmd = "GET";
				httpreq.path = theInput.split(" ")[1];
				httpreq.protocol = theInput.split(" ")[2].split("/")[0];
				httpreq.major = theInput.split(" ")[2].split("/")[1];
				httpreq.minor = theInput.split(" ")[2].split("/")[1];

			} else if (theInput.startsWith("POST")) {
				httpreq.cmd = "POST";
				httpreq.path = theInput.split(" ")[1];
				httpreq.protocol = theInput.split(" ")[2].split("/")[0];

				httpreq.major = theInput.split(" ")[2].split("/")[1];
				httpreq.minor = theInput.split(" ")[2].split("/")[1];

			} else if (theInput.startsWith("Connection:")) {
				httpreq.connection = theInput.substring(11).trim();

			} else if (theInput.startsWith("User-Agent:")) {
				httpreq.userAgent = theInput.substring(11).trim();

			} else if (theInput.startsWith("Accept:")) {
				httpreq.accept = theInput.substring(7).trim();

			} else if (theInput.startsWith("Accept-Encoding:")) {
				httpreq.acceptEncoding = theInput.substring(16).trim();

			} else if (theInput.startsWith("Accept-Charset:")) {
				httpreq.acceptCharset = theInput.substring(15).trim();

			} else if (theInput.startsWith("Accept-Language:")) {
				httpreq.acceptLanguage = theInput.substring(16).trim();

			} else if (theInput.startsWith("Host:")) {
				httpreq.host = theInput.substring(5).trim();
			} else if (theInput.equals("")) {
				state = DATANEXT;
			} else if ((state == DATANEXT) && (httpreq.cmd.equals("POST"))) {
				//  Process data
				int numOfElements = theInput.split("(&|=)").length;
				httpreq.webFormData = new WebFormData[numOfElements / 2];
				int j = 0;
				for (int i = 0; i < numOfElements; i += 2) {
					httpreq.webFormData[j] = new WebFormData();
					// use URLDecoder to decode a string with encoding
					// application/x-www-form-urlencoded
					httpreq.webFormData[j].name = theInput.split("(&|=)")[i];
					try {
						httpreq.webFormData[j].value = URLDecoder.decode(
								theInput.split("(&|=)")[i + 1], "ISO-8859-1");
					} catch (UnsupportedEncodingException e) {
						System.err.println("An unsupported encoding was used.");
					}
					j++;
				}
			}

		}
		return theOutput;
	}

	public String getOutput() {
		String webFile = "";
		String inputLine = "";
		String httpResponse = "";
		BufferedReader in = null;

		// Here we have the hardcoded chooseing of pagaes
		// For everything accept the correct POST request with the correct path
		// we return the form
		if (httpreq.cmd.equals("POST") && httpreq.path.equals("/postmail")) {
			// Here should be a call to a function handling something for /postmail
			//	webFile = PostMail.mail(httpreq);
			webFile = "No postmail function implemented";
		} else {
			try {
				in = new BufferedReader(new FileReader("webmail-form.htm"));
			} catch (FileNotFoundException e) {
				System.out.println("Couldn't find file");
			}
			try {
				while ((inputLine = in.readLine()) != null) {
					webFile += inputLine + "\r\n";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		httpResponse = "HTTP/1.0 200 OK\r\n";
		httpResponse += "Content-Type: text/html\r\n";
		httpResponse += "Content-Length: " + webFile.length() + "\r\n";
		httpResponse += "Server: Java MR MultiThreaded Server\r\n";
		httpResponse += "Connection: Close\r\n";
		httpResponse += "\r\n";
		httpResponse += webFile;

		return httpResponse;
	}

}