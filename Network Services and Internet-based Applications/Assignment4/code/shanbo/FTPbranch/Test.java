/**
 * User: Shanbo Li
 * Date: May 21, 2008
 * Time: 10:40:27 PM
 *
 * @author Shanbo Li
 */
public class Test {
    private static Byte aByte;

    public static void main(String[] args) {
       String a = "abc\r\ndef";
       String b = a.replaceAll("\r\n","");
       String c = a.replaceAll("\\r\\n","");
       String d = a.replaceAll("\\\r\\\n",""); 
        System.out.println(b.toString());
    }
}
