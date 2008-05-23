package tslab.util;

/**
 * Use for command mapping (port = 21 at Ftp Server)
 *
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li and Sike Huang
 * Date: May 17, 2008
 * Time: 11:03:36 PM
 */
public class FTPCommandMapping {
    private long wrongAck;
    private long correctAck;

    public FTPCommandMapping(long wrongAck, long correctAck) {
        this.wrongAck = wrongAck;
        this.correctAck = correctAck;
    }

    public FTPCommandMapping(long wrongAck) {
        this.wrongAck = wrongAck;
    }

    public long getWrongAck() {
        return wrongAck;
    }

    public long getCorrectAck() {
        return correctAck;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FTPCommandMapping)) return false;

        FTPCommandMapping that = (FTPCommandMapping) o;

        if (wrongAck != that.wrongAck) return false;

        return true;
    }

    public int hashCode() {
        return (int) (wrongAck ^ (wrongAck >>> 32));
    }
}
