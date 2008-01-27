/**
 * CalculatorService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package localhost.axis.services.CalculatorService;

public interface CalculatorService extends java.rmi.Remote {
    public int add(int addend1, int addend2) throws java.rmi.RemoteException;
    public int division(int dividend, int divisor) throws java.rmi.RemoteException, localhost.axis.services.CalculatorService.DivideZeroException;
    public int multiplication(int multiplicand, int multiplier) throws java.rmi.RemoteException;
    public int subtraction(int minuend, int subtrahend) throws java.rmi.RemoteException;
}
