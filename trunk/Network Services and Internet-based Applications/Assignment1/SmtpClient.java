package assignment1.smtp;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Random;

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
//    private static String smtpServer = "mx.kth.se";

//    public static void main(String[] args) {
//        int SMTPPort = 25;
//        Socket client = null;
//        try {
//            client = new Socket(SmtpClient.smtpServer, SMTPPort);
//            InputStream is = client.getInputStream();
//            BufferedReader sockin = new BufferedReader(new InputStreamReader(is));
//            OutputStream os = client.getOutputStream();
//            PrintWriter sockout = new PrintWriter(os, true);
//            System.out.println("S:" + sockin.readLine());
//            sockout.println("helo tslab.ssvl.kth.se");
//            System.out.println("S:" + sockin.readLine());
//            sockout.println("mail from: " + "<" + SmtpClient.sender + ">");
//            System.out.println("S:" + sockin.readLine());
//            sockout.println("rcpt to: " + "<" + SmtpClient.receiver + ">");
//            System.out.println("S:" + sockin.readLine());
//            sockout.println("data");
//            String s = new String("subject: all??".getBytes(), Charset.forName("ISO-8859-15"));
//            sockout.println(s);
//            sockout.println(".");
//            sockout.println(".");
//            sockout.println("rset");
//            sockout.println("quit");
//        } catch (IOException e) {
//            System.out.println(e.toString());
//        } finally {
//            try {
//                if (client != null) {
//                    client.close();
//                }
//            } catch (IOException e) {
//            }
//        }

    //    }

    public static void main(String[] args) {

        try {
            findSmtpServer("");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void send(String from, String to, String subject, String message) {
        Socket client = null;
        try {
            client = new Socket(SmtpClient.smtpServer, 25);
            InputStream is = client.getInputStream();
            BufferedReader sockin = new BufferedReader(new InputStreamReader(is));
            OutputStream os = client.getOutputStream();
            PrintWriter sockout = new PrintWriter(os, true);
            System.out.println("S:" + sockin.readLine());
            sockout.println("helo tslab.ssvl.kth.se");
            System.out.println("S:" + sockin.readLine());
            sockout.println("mail from: " + "<" + from + ">");
            System.out.println("S:" + sockin.readLine());
            sockout.println("rcpt to: " + "<" + to + ">");
            System.out.println("S:" + sockin.readLine());
            sockout.println("data");
            String s = new String(subject.getBytes(), Charset.forName("ISO-8859-15"));
            sockout.println("subject: " + s);
            sockout.println(message);
            sockout.println(".");
            sockout.println("rset");
            sockout.println("quit");
        } catch (IOException e) {
            System.out.println(e.toString());
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
            }
        }
    }

    private static void findSmtpServer(String emailAddr) throws IOException, SocketException {
        byte[] query0 = {
                0x00, 0x01, 0x01, 0x00, 0x00, 0x01, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x03, 0x6b, 0x74, 0x68,
                0x02, 0x73, 0x65, 0x00, 0x00, 0x0f, 0x00, 0x01};

        byte[] query1 = {
                0x00, 0x01, 0x01, 0x00, 0x00, 0x01, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x03
        };
        byte[] query2 = {
                0x00, 0x00, 0x0f, 0x00, 0x01
        };


        byte[] query = (new String(query1) + new String("kth.se") + new String(query2)).getBytes();

        System.out.println(query0);
        System.out.println(query1);

        DatagramPacket packet = new DatagramPacket(query, query.length, InetAddress.getByName("kth.se"), 53);
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);
        socket.close();

        String s = "kth.se";
//        for (byte b : query0.getBytes()) {
//            System.out.printf("%h%n", b);
//        }

        for (byte b : s.getBytes()) {
            System.out.printf("%h%n", b);
        }
    }

    public static void send(Email email) {
        //To change body of created methods use File | Settings | File Templates.
    }
}