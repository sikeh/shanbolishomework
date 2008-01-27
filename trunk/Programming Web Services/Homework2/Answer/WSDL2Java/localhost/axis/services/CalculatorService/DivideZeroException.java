/**
 * DivideZeroException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package localhost.axis.services.CalculatorService;

public class DivideZeroException extends org.apache.axis.AxisFault {
    public java.lang.String divideZeroError;
    public java.lang.String getDivideZeroError() {
        return this.divideZeroError;
    }

    public DivideZeroException() {
    }

    public DivideZeroException(java.lang.Exception target) {
        super(target);
    }

    public DivideZeroException(java.lang.String message, java.lang.Throwable t) {
        super(message, t);
    }

      public DivideZeroException(java.lang.String divideZeroError) {
        this.divideZeroError = divideZeroError;
    }

    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {
        context.serialize(qname, null, divideZeroError);
    }
}
