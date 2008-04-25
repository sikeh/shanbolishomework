package assignment2.web;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class IP {

// public static void main(String arg[])
// {
//  IP ip=new IP();
//  System.out.println("The LocalHost's IP is: "+ip.getLocalHostIP ());
//  System.out.println("The LocalHost's HostName is: "+ip.getLocalHostName());
//

    // }

    public static String getLocalHostIP() {
        InetAddress addr = null;
        byte[] ipAddr = null;
        String hostname = "";
        String ipAddress = "";

        try {
            addr = InetAddress.getLocalHost();
            ipAddr = addr.getAddress();
            for (int i = 0; i < ipAddr.length; ++i) {
                if (i > 0) {
                    ipAddress += ".";
                }
                ipAddress += ipAddr[i] & 0xFF;
            }
        }
        catch (UnknownHostException e) {

        }
        return ipAddress;

    }
}
