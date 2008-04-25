package assignment2.web;

import assignment2.sip.SipServer;
import assignment2.SipSpeaker;
import assignment2.util.UrlDecoder;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.util.logging.Logger;


public class HttpServer {
    public static final Logger logger = Logger.getLogger(HttpServer.class.getName());


    public HttpServer() {
    }

    public void init(String iter, int port) {
        StartServe server = new StartServe(iter, port);
        server.start();
    }

    class ConnectionThread
            extends Thread {
        Socket client;
        int counter;

        public ConnectionThread(Socket cl, int c) {
            client = cl;
            counter = c;
        }

        public void run() {
            try {
                String destIP = client.getInetAddress().toString(); // client ip
                int destport = client.getPort(); // client port
                // Time time = new Time();
                logger.info("connect to " + destIP + ":" + destport);
                PrintStream outstream = new PrintStream(client.getOutputStream());
                BufferedReader instream = new BufferedReader(new InputStreamReader(
                        client.getInputStream()));
                String inline = instream.readLine(); // read request
//                logger.info("receive:" + inline);
                if (isGet(inline)) { // if it is a get request
                    outstream.print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
                            "        \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                            "<head>\n" +
                            "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-15\"/>\n" +
                            "    <title>Message Management for SipSpeaker</title>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "<h4>Message Management for SipSpeaker</h4>\n" +
                            "\n" +
                            "<form action=\"action\" method=\"post\">\n" +
                            "    <table border=\"1\" cellpadding=\"10\" cellspacing=\"10\">\n" +
                            "        <tr>\n" +
                            "            <td width=\"200\"><i>Current Message:</i><br/>");
                    outstream.print(SipSpeaker.getMessageText());
                    outstream.print("</td>\n" +
                            "            <td><input type=\"submit\" value=\"Delete\" name=\"delete\"/></td>\n" +
                            "        </tr>\n" +
                            "        <tr>\n" +
                            "            <td><i>New Message: </i><br/><input type=\"text\" name=\"message\"/></td>\n" +
                            "            <td><input type=\"submit\" value=\"Change\" name=\"change\"/></td>\n" +
                            "        </tr>\n" +
                            "    </table>\n" +
                            "\n" +
                            "    <br/>\n" +
                            "\n" +
                            "</form>\n" +
                            "</body>\n" +
                            "</html>");
                } else if (isPost(inline)) {
                    while (true) {
                        inline = instream.readLine();
                        if (inline.startsWith("delete=Delete")) {
                            // delete current message
                            SipSpeaker.setMessageText("");
                            returnHTML("Message has been deleted, so the default wav will be played instead!", outstream);
                            break;
                        }
                        if (inline.endsWith("change=Change")) {
                            // change the message
                            String[] strs = inline.split("&");
                            String msg = strs[0].split("=")[1];
                            if (msg.trim().equals("")) {
                                returnHTML("Can't input an empty message!", outstream);
                            } else {
                                msg = UrlDecoder.decode(msg);
                                SipSpeaker.setMessageText(msg);
                                returnHTML("An new message has been set: " + msg, outstream);
                            }
                            break;
                        }
                    }
                }
                client.close();
            }
            catch (IOException e3) {
                logger.severe(e3.getMessage());
            }

        }


        /**
         * return the result
         *
         * @param message
         * @param outStream
         */
        private void returnHTML(String message, PrintStream outStream) {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><title>iMail by Biiblesoft</title><META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\"></head><body>");
            sb.append("</h1><p>");
            sb.append(message);
            sb.append("</p>");
            sb.append("<form>\n" +
                    "<input type=\"button\" value=\"Back\" onclick=\"history.back();\">\n" +
                    "</form>");

            outStream.println("HTTP/1.0 200 OK");
            outStream.println("MIME_version:1.0");
            outStream.println("Content_Type:text/html");
            outStream.println("Content_Length:" + sb.length());
            outStream.println("");
            outStream.println(sb.toString());
            outStream.flush();
        }

        /**
         * if it is a GET
         *
         * @param s String
         * @return boolean
         */
        boolean isGet(String s) {
            if (s.length() > 0) {
                if (s.substring(0, 3).equalsIgnoreCase("GET")) {
                    return true;
                }
            }
            return false;
        }

        boolean isPost(String s) {
            if (s.length() > 0) {
                if (s.substring(0, 4).equalsIgnoreCase("POST")) {
                    return true;
                }
            }
            return false;
        }


    }


    class StartServe
            extends Thread {
        ServerSocket server = null;
        Socket client = null;
        String iter;
        int port;
        int counter;

        public StartServe(String iter, int port) {
            this.iter = iter;
            this.port = port;
        }

        public void close() {
            try {
                client.close();
                server.close();
            }
            catch (Exception eee) {
                logger.severe(eee.getMessage());
            }
        }

        public void run() {
            try {
                server = new ServerSocket(port, 0, InetAddress.getByName(iter));
                logger.info("http_server: istening request at " + iter + ":" + port);
                for (; ;) {
                    client = server.accept(); // accept request
                    new ConnectionThread(client, counter).start();
                    counter++;
                }
            }
            catch (Exception e1) {
                logger.severe(e1.getMessage());
            }

        }
    }

    StartServe startServe = null;


}






