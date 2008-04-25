package assignment2.sip;

import assignment2.audio.RtpTransmit;
import assignment2.SipSpeaker;

import javax.media.MediaLocator;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

/**
 * User: Shanbo Li
 * Date: Apr 25, 2008
 * Time: 7:33:02 PM
 *
 * @author Shanbo Li
 */
public class ConnectHandler{
    private static final Logger logger = Logger.getLogger(ConnectHandler.class.getName());

    private String callId;
    private String incommingSip;
    private RtpTransmit rtpTransmit;
    private SipParser sipParser;
    private SipFactory sipFactory;
    private String remoteRTPPort;
    private int remoteSIPPort;
    private String remoteSIPIP;
    private String responseOkForInvite;
    private String bye;
    private String okForBye;
    private boolean isTalking = false;
    private MediaLocator media;
    private MediaLocator mediaClone;
    private String notFound;

    public ConnectHandler(String incommingSip) {
        this.incommingSip = incommingSip;
        this.sipFactory = new SipFactory(incommingSip, 1124);
        this.sipParser = new SipParser(incommingSip);
        this.callId = sipParser.getCallId();
        this.remoteSIPIP = sipParser.getRemoteSIPIp();
        this.remoteSIPPort = sipParser.getRemoteSIPPort();
        this.remoteRTPPort = sipParser.getRemoteRTPPort();
    }

    public void answer() {
        try {
            responseOkForInvite = sipFactory.getOkForInvite();
        } catch (ConstructSdpFailedException e) {
        } catch (ConstructSipFailedException e) {
        }
        if (responseOkForInvite != null) {
            sendSIP(responseOkForInvite, remoteSIPIP, remoteSIPPort);
            sendSIP(responseOkForInvite, remoteSIPIP, remoteSIPPort);
        }
        if (responseOkForInvite != null) {
           MediaLocator mediaLocator = new MediaLocator("file:./" + SipSpeaker.getWavToPlay());
            rtpTransmit = new RtpTransmit(media, remoteSIPIP, String.valueOf(remoteRTPPort), null);
            rtpTransmit.start();
            isTalking = true;
        }

        try {
            TimeUnit.SECONDS.sleep((long) rtpTransmit.getPlayTime());
        } catch (InterruptedException e) {
        }
        try {
            bye = sipFactory.getBye();
        } catch (ConstructSdpFailedException e) {
        } catch (ConstructSipFailedException e) {
        }

        if (bye != null) {
            sendSIP(bye, remoteSIPIP, remoteSIPPort);
            sendSIP(bye, remoteSIPIP, remoteSIPPort);

        }
        rtpTransmit.stop();
    }

    public void sendNotFound(){
        try {
            notFound = sipFactory.getNotFound();
        } catch (ConstructSipFailedException e) {
        }

        if (notFound != null){
            sendSIP(notFound,remoteSIPIP,remoteSIPPort);
        }
    }

    public void endCall() {

        okForBye = sipFactory.getOkForBye();
        if (bye != null) {
            sendSIP(bye, remoteSIPIP, remoteSIPPort);
            rtpTransmit.stop();
        }
    }

    private void sendSIP(String sip, String ip, int port) {
        try {

            byte[] message = sip.getBytes();

            InetAddress address = InetAddress.getByName(ip);

            DatagramPacket packet = new DatagramPacket(message, message.length,
                    address, port);

            DatagramSocket dsocket = new DatagramSocket();
            dsocket.send(packet);
            dsocket.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
        }

    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectHandler that = (ConnectHandler) o;

        if (callId != null ? !callId.equals(that.callId) : that.callId != null) return false;

        return true;
    }

    public int hashCode() {
        return (callId != null ? callId.hashCode() : 0);
    }

}
