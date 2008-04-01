import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Sike Huang
 * Date: 2008-3-29
 * Time: 21:38:38
 * To change this template use File | Settings | File Templates.
 */
public class MxLookup {
    public static void main( String args[] ) {

        try {
            doLookup("kth.se");
        } catch (NamingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if( args.length == 0 ) {
          System.err.println( "Usage: MXLookup host [...]" );
          System.exit( 99 );
        }
        for( int i = 0; i < args.length; i++ ) {
          try {
            System.out.println( args[i] + " has " +
              doLookup( args[i] ) + " mail servers" );
          }
          catch( Exception e ) {
            System.out.println(args[i] + " : " + e.getMessage());
          }
        }
      }

      static int doLookup( String hostName ) throws NamingException {
        Hashtable env = new Hashtable();
        env.put("java.naming.factory.initial",
                "com.sun.jndi.dns.DnsContextFactory");
        DirContext ictx = new InitialDirContext( env );
        Attributes attrs =
           ictx.getAttributes( hostName, new String[] { "MX" });
        Attribute attr = attrs.get( "MX" );
        if( attr == null ) return( 0 );
        return( attr.size() );
      }
    
}
