package tslab.util;

/**
 * User: Shanbo Li
 * Date: Jun 23, 2008
 * Time: 11:17:25 PM
 *
 * @author Shanbo Li
 */
public class CRC16 {
    // Just change these two constants for any other CRC-16 polynomial.
    public static final int POLYNOMIAL = 0x1021;
    public static final short INIT = (short) 0xffff;

    private static final short[] crcTable = new short[256];

    private short crc = INIT;

    static {
        for (int dividend = 0; dividend < 256; dividend++) {
            int remainder = dividend << 8;
            for (int bit = 8; bit > 0; --bit)
                if ((remainder & 0x8000) != 0)
                    remainder = (remainder << 1) ^ POLYNOMIAL;
                else
                    remainder <<= 1;
            crcTable[dividend] = (short) remainder;
        }
    }

    public void update(byte[] buffer, int offset, int len) {
        for (int i = offset; i < len; i++) {
            int data = buffer[i] ^ (crc >>> 8);
            crc = (short) (crcTable[data & 0xff] ^ (crc << 8));
        }
    }

    public void update(int b) {
        update(new byte[]{(byte) b}, 0, 1);
    }

    public long getValue() {
        return crc;
    }

    public void reset() {
        crc = INIT;
    }

    public static void main(String[] args) {
        byte[] data = new byte[]{0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39};
        CRC16 crc16 = new CRC16();
        crc16.update(data, 0, data.length);
        System.out.println("CRC16=0x" + Long.toHexString(crc16.getValue()));
    }
}
