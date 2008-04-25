/*
 * Created on 2005-jan-23
 * This file is created as assignment 1. WebMail in the course 2G1333
 * See the attached README file for instructions on starting
 * Author: Mikael Rudholm 
 */

/**
 * @author Mikael Rudholm
 */
public class HTTPRequest {
	public String cmd;

	public String path;

	public String protocol;

	public int majorInt;

	public int minorInt;

	public String major;

	public String minor;

	public String connection;

	public String userAgent;

	public String accept;

	public String acceptEncoding;

	public String acceptCharset;

	public String acceptLanguage;

	public String host;
	
	public int contentLength;
	
	public String contentType;
	
	public String referer;
	
	public String pragma;
	
	public String cacheControl;
	
	public WebFormData [] webFormData;

}