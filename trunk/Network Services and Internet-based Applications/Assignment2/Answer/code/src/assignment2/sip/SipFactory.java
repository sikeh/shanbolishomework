package assignment2.sip;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * User: Shanbo Li
 * Date: Apr 13, 2008
 * Time: 3:24:09 PM
 *
 * @author Shanbo Li
 */
public class SipFactory {
    public static final Logger logger = Logger.getLogger(SipFactory.class.getName());

    private String[] lines;
    private int rtpPort;
    private int fromTag = -1;
    private String toTag = null;
    private String me;
    private int initialCSeq;
    private SipParser sipParser;
    private int remoteSipPort;
    private String remoteSipIp;


    /**
     * @param line
     * @param rtpPort
     */
    public SipFactory(String line, int rtpPort) {
        lines = line.split("\r\n");
        this.rtpPort = rtpPort;
        sipParser = new SipParser(line);
        remoteSipIp = sipParser.getRemoteSIPIp();
        remoteSipPort = sipParser.getRemoteSIPPort();
    }

    public String getOkForBye() {
        String sip;
        SIPBean sipBean = new SIPBean();

        for (String line : lines) {
            if (line.startsWith("Via:")) {
                sipBean.setVia(line + "\r\n");
            }

            if (line.startsWith("Record-Route:")) {
                sipBean.setRoute(line + "\r\n");
            }

            if (line.startsWith("To:")) {
                if (toTag != null) {
                    sipBean.setTo(line + ";tag=" + toTag + "\r\n");
                } else {
                    sipBean.setTo(line + "\r\n");
                }

                me = line.split("<")[1].split(">")[0];

                sipBean.setContact("Contact: " + line.substring(4) + "\r\n");
            }

            if (line.startsWith("From:")) {
                sipBean.setFrom(line + "\r\n");
                try {
                    fromTag = Integer.parseInt(line.substring(line.length() - 4));
                } catch (NumberFormatException e) {
                }
                if (fromTag != -1 && toTag != null) {
                    toTag = GenerateTag.getTag(fromTag);
                }
            }

            if (line.startsWith("Call-ID:")) {
                sipBean.setCallId(line + "\r\n");
            }

            if (line.startsWith("CSeq:")) {
                sipBean.setCseq(line + "\r\n");
            }
        }

        sipBean.setType("SIP/2.0 200 OK\r\n");
        sip = sipBean.getOkForByeBean();
        return sip;
    }

    public String getNotFound() throws ConstructSipFailedException {
        String sip;
        SIPBean sipBean = new SIPBean();
        SDPBean sdpBean = new SDPBean();

        for (String line : lines) {
            if (line.startsWith("Via:")) {
                sipBean.setVia(line + "\r\n");
            }

            if (line.startsWith("Record-Route:")) {
                sipBean.setRoute(line + "\r\n");
            }

            if (line.startsWith("To:")) {
                if (toTag != null) {
                    sipBean.setTo(line + ";tag=" + toTag + "\r\n");
                } else {
                    sipBean.setTo(line + "\r\n");
                }

                me = line.split("<")[1].split(">")[0];

                sipBean.setContact("Contact: " + line.substring(4) + "\r\n");
            }

            if (line.startsWith("From:")) {
                sipBean.setFrom(line + "\r\n");
                try {
                    fromTag = Integer.parseInt(line.substring(line.length() - 4));
                } catch (NumberFormatException e) {
                }
                if (fromTag != -1 && toTag != null) {
                    toTag = GenerateTag.getTag(fromTag);
                }
            }

            if (line.startsWith("Call-ID:")) {
                sipBean.setCallId(line + "\r\n");
            }

            if (line.startsWith("CSeq:")) {
                sipBean.setCseq(line + "\r\n");

            }
        }

        sipBean.setType("SIP/2.0 404 Not Found\r\n");


        sip = sipBean.getNotFoundBeann();


        return sip;
    }

    public String getOkForInvite() throws ConstructSdpFailedException, ConstructSipFailedException {
        String sip;
        SIPBean sipBean = new SIPBean();
        SDPBean sdpBean = new SDPBean();

        for (String line : lines) {
            if (line.startsWith("Via:")) {
                sipBean.setVia(line + "\r\n");
            }

            if (line.startsWith("Record-Route:")) {
                sipBean.setRoute(line + "\r\n");
            }

            if (line.startsWith("To:")) {
                if (toTag != null) {
                    sipBean.setTo(line + ";tag=" + toTag + "\r\n");
                } else {
                    sipBean.setTo(line + "\r\n");
                }

                me = line.split("<")[1].split(">")[0];

                sipBean.setContact("Contact: " + line.substring(4) + "\r\n");
            }

            if (line.startsWith("From:")) {
                sipBean.setFrom(line + "\r\n");
                try {
                    fromTag = Integer.parseInt(line.substring(line.length() - 4));
                } catch (NumberFormatException e) {
                }
                if (fromTag != -1 && toTag != null) {
                    toTag = GenerateTag.getTag(fromTag);
                }
            }

            if (line.startsWith("Call-ID:")) {
                sipBean.setCallId(line + "\r\n");
            }

            if (line.startsWith("CSeq:")) {
                sipBean.setCseq(line + "\r\n");

                initialCSeq = Integer.valueOf(line.split(" ")[1]);
            }

            if (line.startsWith("s=")) {
                sdpBean.setS(line + "\r\n");
            }


        }

        sdpBean.setMWithPort(String.valueOf(rtpPort));

        try {
            sipBean.setSdp(sdpBean.getSdp());
        } catch (ConstructSdpFailedException e) {
            logger.log(Level.SEVERE, null, e);
            throw e;
        }

        try {
            sip = sipBean.getOkForInventBean();
        } catch (ConstructSipFailedException e) {
            logger.log(Level.SEVERE, null, e);
            throw e;
        }

        return sip;
    }

    public String getBye() throws ConstructSdpFailedException, ConstructSipFailedException {
        SIPBean sipBean = new SIPBean();
        for (String line : lines) {
            if (line.startsWith("Via:")) {
                sipBean.setVia(line + "\r\n");
            }

            if (line.startsWith("Record-Route:")) {
                sipBean.setRoute(line + "\r\n");
            }

            if (line.startsWith("To:")) {
                if (toTag != null) {
                    sipBean.setTo(line + ";tag=" + toTag + "\r\n");
                } else {
                    sipBean.setTo(line + "\r\n");
                }

                me = line.split("<")[1].split(">")[0];
                sipBean.setType("BYE " + me + " SIP/2.0\r\n");
                sipBean.setContact("Contact: " + line.substring(4) + "\r\n");
            }

            if (line.startsWith("From:")) {
                sipBean.setFrom(line + "\r\n");
                try {
                    fromTag = Integer.parseInt(line.substring(line.length() - 4));
                } catch (NumberFormatException e) {
                }
                if (fromTag != -1 && toTag != null) {
                    toTag = GenerateTag.getTag(fromTag);
                }
            }

            if (line.startsWith("Call-ID:")) {
                sipBean.setCallId(line + "\r\n");
            }

            if (line.startsWith("CSeq:")) {
                sipBean.setCseq("CSeq: " + (initialCSeq + 24) + " BYE\r\n");
            }

        }

        sipBean.setType("BYE sip:" + remoteSipIp + ":" + remoteSipPort + " SIP/2.0\r\n");

        String sip;
        try {
            sip = sipBean.getByeBean();
        } catch (ConstructSipFailedException e) {
            logger.log(Level.SEVERE, null, e);
            throw e;
        }

        return sip;
    }
}
