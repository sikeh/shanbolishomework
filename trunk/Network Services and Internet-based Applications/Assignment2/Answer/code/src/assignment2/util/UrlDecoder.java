package assignment2.util;

/**
 * Created by IntelliJ IDEA.
 * User: Sike Huang
 * Date: 2008-4-1
 * Time: 15:38:49
 * To change this template use File | Settings | File Templates.
 */
public class UrlDecoder {

    public static String decode(String in) {
        in = in.replaceAll("\\+", " ");
        in = in.replaceAll("%21", "!");
        in = in.replaceAll("%23", "#");
        in = in.replaceAll("%24", "\\$");
        in = in.replaceAll("%25", "%");
        in = in.replaceAll("%5E", "^");
        in = in.replaceAll("%26", "&");
        in = in.replaceAll("%27", "'");
        in = in.replaceAll("%28", "(");
        in = in.replaceAll("%29", ")");
        in = in.replaceAll("%2B", "+");
        in = in.replaceAll("%3B", ";");
        in = in.replaceAll("%3A", ":");
        in = in.replaceAll("%2A", "*");
        in = in.replaceAll("%2F", "/");
        in = in.replaceAll("%3C", "<");
        in = in.replaceAll("%3D", "=");
        in = in.replaceAll("%3E", ">");
        in = in.replaceAll("%3F", "?");
        in = in.replaceAll("%5B", "[");
        in = in.replaceAll("%5C", "\\\\");      // are you kidding me?
        in = in.replaceAll("%5D", "]");
        in = in.replaceAll("%5E", "^");
        in = in.replaceAll("%5F", "_");
        in = in.replaceAll("%60", "`");
        in = in.replaceAll("%7B", "{");
        in = in.replaceAll("%7C", "|");
        in = in.replaceAll("%7D", "}");
        in = in.replaceAll("%7E", "~");
        in = in.replaceAll("%22", "\"");
        in = in.replaceAll("%2C", ",");

        return in;
    }
}
