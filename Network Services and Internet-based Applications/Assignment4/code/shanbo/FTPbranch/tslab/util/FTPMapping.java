package tslab.util;

/**
 * Created by IntelliJ IDEA.
 * Develop with pleasure.
 * User: Shanbo Li
 * Date: May 17, 2008
 * Time: 11:03:36 PM
 */
public class FTPMapping {
    private long wrongAck;
    private long correctAck;

    public FTPMapping(long wrongAck, long correctAck) {
        this.wrongAck = wrongAck;
        this.correctAck = correctAck;
    }

    public FTPMapping(long wrongAck) {
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
        if (!(o instanceof FTPMapping)) return false;

        FTPMapping that = (FTPMapping) o;

        if (wrongAck != that.wrongAck) return false;

        return true;
    }

    public int hashCode() {
        return (int) (wrongAck ^ (wrongAck >>> 32));
    }
}
