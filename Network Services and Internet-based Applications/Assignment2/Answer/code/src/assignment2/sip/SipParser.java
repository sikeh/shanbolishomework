package assignment2.sip;

/**
 * User: Shanbo Li
 * Date: Apr 25, 2008
 * Time: 4:26:33 PM
 *
 * @author Shanbo Li
 */
public class SipParser {
    private String ip;
    private String port = "5060";
    private String inComingSip;
    private String callId;
    private String to;

    /**
     * @param inComingSip
     */
    public SipParser(String inComingSip) {
        this.inComingSip = inComingSip;
        doParse(inComingSip);
    }

    public String getCallId() {
        return callId;
    }

    public String getTo() {
        return to;
    }

    public MessageType getMessageType() {
        return getMessageType(inComingSip);
    }

    public static MessageType getMessageType(String line0) {
        if (line0.startsWith("INVITE")) {
            return MessageType.INVITE;
        }

        if (line0.startsWith("BYE")) {
            return MessageType.BYE;
        }

        if (line0.startsWith("CANCLE")) {
            return MessageType.CANCLE;
        }

        if (line0.startsWith("CANCLE")) {
            return MessageType.CANCLE;
        }

        if (line0.startsWith("SIP")) {
            return MessageType.RESPONSE;
        }

        if (line0.startsWith("ACK")) {
            return MessageType.ACK;
        }

        if (line0.startsWith("REGISTER")) {
            return MessageType.REGISTER;
        }

        if (line0.startsWith("OPTIONS")) {
            return MessageType.OPTIONS;
        }

        return MessageType.UNKNOW;
    }

    public String getRemoteSIPIp() {
        return ip;
    }

    public int getRemoteSIPPort() {
        try {
            return Integer.valueOf(port);
        } catch (NumberFormatException e) {
            return 5060;
        }

    }

    public String getRemoteRTPPort() {
        String[] lines = inComingSip.split("\r\n");
        String[] mField = null;

        for (String line : lines) {
            if (line.startsWith("m=")) {
                mField = line.split(" ");
                return mField[1];

            }
        }
        return null;
    }

    /**
     * get Contact ip from a sip
     *
     * @param sip
     * @return ip or null
     */
    private void doParse(String sip) {
        String[] lines = sip.split("\r\n");
        String[] keys = new String[24];
        for (String line : lines) {
            //get Contact Ip And Port
            if (line.startsWith("Contact:")) {
                keys = line.split(":");
                if (keys.length == 4) {
                    port = keys[3].split(">")[0];
                }
                // if has a "@"
                if (keys[2].indexOf("@") != -1) {
                    String s = keys[2].split("@")[1];
                    ip = s.split(">")[0].split(":")[0];
                }
                // No "@"
                else {
                    String s = keys[2].split("@")[0];
                    ip = s.split(">")[0].split(":")[0];

                }
            }

            if (line.startsWith("Call-ID:")){
                this.callId = line.split(" ")[1];
            }

            if (line.startsWith("To:")){
                this.to = line.split(":")[2];
            }
        }
    }
}
