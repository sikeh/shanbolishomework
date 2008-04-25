package assignment2.sip;

/**
 * User: Shanbo Li
 * Date: Apr 13, 2008
 * Time: 4:41:32 PM
 *
 * @author Shanbo Li
 */
public class SIPBean {
    String type;
    String via;
    String route;
    String to;
    String from;
    String callId;
    String cseq;
    String userAgent;
    String contact;
    String allow;
    String accept;
    String supported;
    String contentType;
    String maxForwards = "Max-Forwards: 24\r\n";
    String contentLength = "Content-Length: 0\r\n";

    String sdp;

    public SIPBean() {

        userAgent = "User-Agent: BiibleSoft SIP Speaker by Shanbo Li and Sike Huang\r\n";
        allow = "Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY\r\n";
        accept = "Accept: application/sdp\r\n";
        supported = "Supported: replaces\r\n";
        contentType = "Content-Type: application/sdp\r\n";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getCseq() {
        return cseq;
    }

    public void setCseq(String cseq) {
        this.cseq = cseq;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAllow() {
        return allow;
    }

    public String getAccept() {
        return accept;
    }

    public String getSupported() {
        return supported;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentLength() {
        return contentLength;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
        this.contentLength = "Content-Length: " + sdp.length() + "\r\n";
    }

    public String getNotFoundBeann() {

        StringBuilder sb = new StringBuilder();
        sb.append(type);
        sb.append(via);
        sb.append(to);
        sb.append(from);
        sb.append(callId);
        sb.append(cseq);
        sb.append(userAgent);
        sb.append(contentLength);
        sb.append("\r\n");
        return sb.toString();
    }

    public String getOkForInventBean() throws ConstructSipFailedException {
        type = "SIP/2.0 200 OK\r\n";

        validateSIPContent();
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        if (via != null) sb.append(via);
        if (route != null) sb.append(route);
        sb.append(to);
        sb.append(from);
        sb.append(callId);
        sb.append(cseq);
        sb.append(userAgent);
        sb.append(contact);
        sb.append(allow);
        sb.append(accept);
        sb.append(supported);
        sb.append(contentType);
        sb.append(contentLength);
        sb.append("\r\n");
        sb.append(sdp);

        return sb.toString();
    }

    public String getByeBean() throws ConstructSipFailedException {
        validateSIPContent();
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        sb.append(cseq);
        sb.append(via);
        sb.append(contentLength);
        sb.append(maxForwards);
        sb.append(callId);
        sb.append(userAgent);
        sb.append(from);
        sb.append(to);
        sb.append("\r\n");

        return sb.toString();
    }


    private void validateSIPContent() throws ConstructSipFailedException {
        if (via == null) {
            throw new ConstructSipFailedException("Please initial \"via\" field before getSIP");
        }

        if (to == null) {
            throw new ConstructSipFailedException("Please initial \"to\" field before getSIP");
        }

        if (from == null) {
            throw new ConstructSipFailedException("Please initial \"from\" field before getSIP");
        }

        if (callId == null) {
            throw new ConstructSipFailedException("Please initial \"callId\" field before getSIP");
        }

        if (cseq == null) {
            throw new ConstructSipFailedException("Please initial \"cseq\" field before getSIP");
        }

        if (contact == null) {
            throw new ConstructSipFailedException("Please initial \"contact\" field before getSIP");
        }
    }

    public String getOkForByeBean() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        if (via != null) sb.append(via);
        if (route != null) sb.append(route);
        sb.append(to);
        sb.append(from);
        sb.append(callId);
        sb.append(cseq);
        sb.append(userAgent);
        sb.append(contentLength);
        sb.append("\r\n");
        return sb.toString();
    }
}
