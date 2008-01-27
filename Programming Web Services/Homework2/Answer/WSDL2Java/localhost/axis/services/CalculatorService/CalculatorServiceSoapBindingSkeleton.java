/**
 * CalculatorServiceSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package localhost.axis.services.CalculatorService;

public class CalculatorServiceSoapBindingSkeleton implements localhost.axis.services.CalculatorService.CalculatorService, org.apache.axis.wsdl.Skeleton {
    private localhost.axis.services.CalculatorService.CalculatorService impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "addend1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "addend2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("add", _params, new javax.xml.namespace.QName("", "addReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://DefaultNamespace", "add"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("add") == null) {
            _myOperations.put("add", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("add")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "dividend"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "divisor"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("division", _params, new javax.xml.namespace.QName("", "divisionReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://DefaultNamespace", "division"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("division") == null) {
            _myOperations.put("division", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("division")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("divideZeroException");
        _fault.setQName(new javax.xml.namespace.QName("http://localhost:8080/axis/CalculatorService.jws", "divideZeroError"));
        _fault.setClassName("localhost.axis.services.CalculatorService.DivideZeroException");
        _fault.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "multiplicand"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "multiplier"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("multiplication", _params, new javax.xml.namespace.QName("", "multiplicationReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://DefaultNamespace", "multiplication"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("multiplication") == null) {
            _myOperations.put("multiplication", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("multiplication")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "minuend"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "subtrahend"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("subtraction", _params, new javax.xml.namespace.QName("", "subtractionReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://DefaultNamespace", "subtraction"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("subtraction") == null) {
            _myOperations.put("subtraction", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("subtraction")).add(_oper);
    }

    public CalculatorServiceSoapBindingSkeleton() {
        this.impl = new localhost.axis.services.CalculatorService.CalculatorServiceSoapBindingImpl();
    }

    public CalculatorServiceSoapBindingSkeleton(localhost.axis.services.CalculatorService.CalculatorService impl) {
        this.impl = impl;
    }
    public int add(int addend1, int addend2) throws java.rmi.RemoteException
    {
        int ret = impl.add(addend1, addend2);
        return ret;
    }

    public int division(int dividend, int divisor) throws java.rmi.RemoteException, localhost.axis.services.CalculatorService.DivideZeroException
    {
        int ret = impl.division(dividend, divisor);
        return ret;
    }

    public int multiplication(int multiplicand, int multiplier) throws java.rmi.RemoteException
    {
        int ret = impl.multiplication(multiplicand, multiplier);
        return ret;
    }

    public int subtraction(int minuend, int subtrahend) throws java.rmi.RemoteException
    {
        int ret = impl.subtraction(minuend, subtrahend);
        return ret;
    }

}
