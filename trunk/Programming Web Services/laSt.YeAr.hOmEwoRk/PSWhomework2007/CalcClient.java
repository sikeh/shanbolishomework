import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.utils.Options;

import javax.xml.rpc.ParameterMode;

public class CalcClient
{
   public static void main(String [] args) throws Exception {
       Options options = new Options(args);
       
       String endpoint = "http://localhost:8080/axis/services/CalculatorService";
       
       Service  service = new Service();
       Call     call    = (Call) service.createCall();
       call.setTargetEndpointAddress( new java.net.URL(endpoint) );

	   //read operation
	   System.out.println("0. add");
       System.out.println("1. subtraction");
       System.out.println("2. multiplication");
       System.out.println("3. division");
       System.out.print("please select an operation: ");
	   byte[] b = new byte[256];
	   int n = System.in.read(b);
	   int x = Integer.parseInt(new String(b, 0, 1));	   

	   String operName = new String();
	   String para1Name = new String();
	   String para2Name = new String();
	   switch(x){
	   case 0: operName = "add"; para1Name = "addend1"; para2Name = "addend2";
		   break;
	   case 1: operName = "subtraction"; para1Name = "minuend"; para2Name = "subtrahend";
		   break;
	   case 2: operName = "multiplication"; para1Name = "multiplicand"; para2Name = "multiplier";
		   break;
	   case 3: operName = "division"; para1Name = "dividend"; para2Name = "divisor";
		   break;
	   default: System.out.println(" error"); System.exit(-1); 
	   }

       System.out.print("Input parameter 1: ");
	   n = System.in.read(b);
	   int p1 = Integer.parseInt(new String(b, 0, n-2));	   
       System.out.print("Input parameter 2: ");
	   n = System.in.read(b);
	   int p2 = Integer.parseInt(new String(b, 0, n-2));	   

       call.setOperationName(operName);
       call.addParameter( para1Name, XMLType.XSD_INT, ParameterMode.IN );
       call.addParameter( para2Name, XMLType.XSD_INT, ParameterMode.IN );
       call.setReturnType( XMLType.XSD_INT );

       Integer ret = (Integer) call.invoke( new Object [] {new Integer(p1), new Integer(p2)});
       
       System.out.println("Got result : " + ret);
   }
}
