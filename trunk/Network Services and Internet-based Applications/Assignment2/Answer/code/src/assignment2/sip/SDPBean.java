package assignment2.sip;

import assignment2.ui.IP;

/**
 * User: Shanbo Li
 * Date: Apr 13, 2008
 * Time: 10:44:28 PM
 *
 * @author Shanbo Li
 */
public class SDPBean {
    private String v;
    private String o;
    private String s;
    private String c;
    private String t;
    private String m;
    private String a;

    public SDPBean() {
        v = "v=0\r\n";
        o = "o=BiiblesoftSipSpeaker " + System.currentTimeMillis() + " " + System.currentTimeMillis() + " IN IP4 " + IP.getLocalHostIP() + "\r\n";
        c = "c=IN IP4 " + IP.getLocalHostIP() + "\r\n";
        t = "t=0 0\r\n";
        a = "a=rtpmap:0 PCMU/8000\r\n" +
                "a=sendrecv\r\n" +
                "a=direction:active\r\n";
    }

    public String getV() {
        return v;
    }

    public String getO() {
        return o;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getC() {
        return c;
    }

    public String getT() {
        return t;
    }

    public String getM() {
        return m;
    }

    public void setMWithPort(String port) {
        this.m = "m=audio " + port + " RTP/AVP 0\r\n";
    }

    public String getSdp() throws ConstructSdpFailedException {
        if (m == null) {
            throw new ConstructSdpFailedException("Please initial \"m\" field before getSDP");
        }

        if (s == null) {
            throw new ConstructSdpFailedException("Please initial \"s\" field before getSDP");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(v);
        sb.append(o);
        sb.append(s);
        sb.append(c);
        sb.append(t);
        sb.append(m);
        sb.append(a);

        return sb.toString();
    }
}
