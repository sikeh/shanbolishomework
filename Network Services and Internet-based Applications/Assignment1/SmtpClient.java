package assignment1.smtp;

import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sike Huang
 * Date: 2008-3-29
 * Time: 20:17:18
 * To change this template use File | Settings | File Templates.
 */
public class SmtpClient {
    private static String sender = "sikeh@kth.se";
    private static String receiver = "sike.huang@gmail.com";
    private static String smtpServer = "ik2213.ssvl.kth.se";
    
    public static void main(String[] args) {
        try {
            send("123@hello.fi", "sike.huang@yahoo.com", "hello", "hello, world!");
        } catch (SmtpExection smtpExection) {
            smtpExection.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void send(String from, String to, String subject, String message) throws SmtpExection {
        try {
            String smtpServer = findSmtpServer(to).get(0);
            send(from, to, subject, message, smtpServer);
        } catch (NamingException e) {
            throw new SmtpExection(e.getMessage());
        }
    }

    public static void send(String from, String to, String subject, String message, String smtpServer) throws SmtpExection {
        Socket socket = null;
        try {
            socket = new Socket(smtpServer, 25);
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
            String response = reader.readLine();
            if (!response.startsWith("220")) {
                throw new SmtpExection("invalid header");
            }
            writer.println("helo tslab.ssvl.kth.se");
            response = reader.readLine();
            if (!response.startsWith("250")) {
                throw new SmtpExection("invalid domain name");
            }
            writer.println("mail from: " + "<" + from + ">");
            response = reader.readLine();
            if (!response.startsWith("250")) {
                throw new SmtpExection("sender rejected");
            }
            writer.println("rcpt to: " + "<" + to + ">");
            response = reader.readLine();
            if (!response.startsWith("250")) {
                throw new SmtpExection("receiver rejected");
            }
            writer.println("data");
            // ignore the message printed below -> 354 End data with <CR><LF>.<CR><LF>
            System.out.println("S: " + reader.readLine());
            writer.println("from: " + "<" + from + ">");
            writer.println("to: " + "<" + to + ">");
//            writer.println("date: Tue, 15 Jan 1985 16:02:43 -0500");
            writer.println("date: " + new Date());
            writer.println("subject: " + subject);
            writer.println("mime-version: 1.1");
            writer.println("content-type: text/plain; charset=ISO-8859-1");
            // quoted-printable ? used to encode arbitrary octet sequences into a form that satisfies the rules of 7bit.
            // Designed to be efficient and mostly human readable when used for text data consisting primarily of US-ASCII characters
            // but also containing a small proportion of bytes with values outside that range. 
            writer.println("content-transfer-encoding: quoted-printable");
            writer.println(message);
            writer.println(".");
            writer.println("rset");
            writer.println("quit");
            System.out.println("S: " + reader.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<String> findSmtpServer(String emailAddr) throws NamingException {
        String hostName = emailAddr.split("@")[1];
        Hashtable hashtable = new Hashtable();
        hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        DirContext dirContext = new InitialDirContext(hashtable);
        Attributes attributes = dirContext.getAttributes(hostName, new String[]{"MX"});
        Attribute attr = attributes.get("MX");
        if (attr == null) {
            throw new NamingException("no mx found");
        }
        NamingEnumeration nameingEnumeration = attr.getAll();
        List<String> list = new ArrayList<String>();
        while (nameingEnumeration.hasMore()) {
            String line = (String) nameingEnumeration.next();
            // 10 mx.kth.se.
            String[] str = line.split(" ");
            String smtp = str[1].substring(0, (str[1].length() - 1));
            list.add(smtp);
        }
        return list;
    }

}