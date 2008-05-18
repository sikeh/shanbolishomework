package tslab;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;

import java.util.Scanner;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import tslab.util.Tools;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * Date: May 17, 2008
 * Time: 12:32:25 PM
 *
 * @author Sike Huang and Shanbo Li
 */
public class Bouncer {
    private static JpcapCaptor captor;
    // used by factory
    private static InetAddress bouncerAddress;
    private static byte[] bouncerMac;
    private static InetAddress serverAddress;
    private static byte[] serverMac;

    private static String listenIp;
    private static String listenPort;
    private static String serverIp;
    private static String serverPort;

    public Bouncer() {
        //
    }

    private NetworkInterface selectDevice() {
        System.out.println("List of interfaces");
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        for (int i = 0; i < devices.length; i++) {
            System.out.print(i + ": " + devices[i].name);
            System.out.print(" [ip=" + getIpv4(devices[i]) + "] ");
            System.out.println("");
        }
        System.out.print("Select one: ");
        Scanner scanner = new Scanner(System.in);
        int index = scanner.nextInt();
        if (index < 0 || index > devices.length - 1) {
            return null;
        }
        return devices[index];
    }

    private static InetAddress getIpv4(NetworkInterface networkInterface) {
        for (NetworkInterfaceAddress a : networkInterface.addresses) {
            if (a.address.toString().split(":").length >= 4) {
                continue;
            }
            return a.address;
        }
        return null;
    }

    private NetworkInterface selectDevice(String nameOfDevice) {
        for (NetworkInterface device : JpcapCaptor.getDeviceList()) {
            if (device.name.equalsIgnoreCase(nameOfDevice)) {
                return device;
            }
        }
        return null;
    }


    public static void main(String[] args) {
        Bouncer bouncer = new Bouncer();
        if (args.length < 2 || args.length > 3) {
            bouncer.printUsage();
            System.exit(1);
        }
        NetworkInterface device = null;
        String[] listen = null;
        String[] server = null;
        if (args.length == 2) {
            listen = args[0].split(":");
            server = args[1].split(":");
            validateParameters(listen, server);
            device = bouncer.selectDevice();
        }
        if (args.length == 3) {
            listen = args[1].split(":");
            server = args[2].split(":");
            validateParameters(listen, server);
            device = bouncer.selectDevice(args[0]);
        }

        if (device == null) {
            System.out.println("Invalid interface!");
            System.exit(1);
        }
        try {
            captor = JpcapCaptor.openDevice(device, 65535, false, 20);
            captor.setFilter("ip", true);
            captor.setFilter("dst host " + listenIp, true);
        } catch (IOException e) {
            System.out.println("Can't open device: " + e.toString());
            System.exit(1);
        }

        System.out.println("Resolving bouncer IP and MAC...");
        try {
            bouncerAddress = getIpv4(device);
            bouncerMac = Tools.arp(bouncerAddress);
        } catch (UnknownHostException e) {
            System.out.println("Failed in resolving IP!");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Failed in resolving MAC!");
            System.exit(1);
        }

        System.out.println("Resolving server IP and MAC...");
        try {
            serverAddress = InetAddress.getByName(serverIp);
            serverMac = Tools.arp(serverIp);
        } catch (UnknownHostException e) {
            System.out.println("Failed in resolving IP!");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Failed in resolving MAC!");
            System.exit(1);
        }

        System.out.println("Start listenning...");
        PacketListener listner = new PacketListener(captor, bouncerAddress, bouncerMac, serverAddress, serverMac);
        listner.receive();
    }

    private static void validateParameters(String[] listen, String[] server) {
        try {
            listenIp = listen[0];
            if (listen.length > 1) {
                listenPort = listen[1];
            }
            serverIp = server[0];
            if (server.length > 1) {
                serverPort = server[1];
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = "Invalid Parameters";
            exit(message);
        }
    }

    private static void exit(String message) {
        System.out.println("");
        System.out.println(message);
        printUsage();
        System.exit(1);
    }

    private static void printUsage() {
        System.out.println("usage:");
        System.out.println("java tslab.Bouncer [interface] listen_ip:listen_port server_ip:server_port");
    }
}
