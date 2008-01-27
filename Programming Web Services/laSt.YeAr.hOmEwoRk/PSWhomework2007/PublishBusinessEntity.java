import org.uddi4j.client.*;
import org.uddi4j.datatype.*;
import org.uddi4j.datatype.business.*;
import org.uddi4j.datatype.tmodel.*;
import org.uddi4j.response.*;
import org.uddi4j.util.*;

import java.util.*;

/**
 * Chapter 6 - Publish BusinessEntity Example
 * 
 * This example shows how to publish the businessEntity
 * that represents SkatesTown.
 */
public class PublishBusinessEntity {
  // Location of UDDI registry
  String publishURL = "https://uddi.ibm.com/testregistry/publishapi";

  /**
   * Create the businessEntity and then process the save_business operation.
   */
  protected void saveBusiness(String userid, String password) throws Exception {
    UDDIProxy uddiProxy = null;

    // Add SSL support (this is IBM's SSL support but it can be replaced 
    // with other implementations)
    System.setProperty("java.protocol.handler.pkgs", 
      "com.ibm.net.ssl.internal.www.protocol");
    java.security.Security.addProvider(new com.ibm.jsse.JSSEProvider());
    
    // Create UDDI proxy
    uddiProxy = new UDDIProxy();
    uddiProxy.setPublishURL(publishURL);
    
    // Create businessEntity
    BusinessEntity businessEntity = new BusinessEntity();
    businessEntity.setBusinessKey("");
    
    // Set name and description
    businessEntity.setDefaultNameString("SkatesTown", "en");
    Vector description = new Vector();
    description.add(new Description("UDDI businessEntity for SkatesTown.", "en"));
    businessEntity.setDescriptionVector(description);
    
    // Create first contact
    Contact ctoContact = new Contact("Dean Carroll");
    ctoContact.setUseType("Technical Information");
    ctoContact.setDefaultDescriptionString("CTO for technical information");
    Vector phoneList = new Vector();
    Phone mainPhone = new Phone("1.212.555.0001");
    mainPhone.setUseType("Main Office");
    phoneList.add(mainPhone);
    ctoContact.setPhoneVector(phoneList);
    Vector emailList = new Vector();
    Email email = new Email("dean.carroll@SkatesTown.com");
    email.setUseType("CTO");
    emailList.add(email);
    email = new Email("info@SkatesTown.com");
    email.setUseType("General Information");
    emailList.add(email);
    ctoContact.setEmailVector(emailList);
    Vector skatesTownAddress = new Vector();
    Address address = new Address();
    address.setSortCode("10001");
    address.setUseType("Main Office");
    Vector addressLineList = new Vector();
    addressLineList.add("2001 Skate Services Lane");
    addressLineList.add("New York, NY 10001");
    addressLineList.add("USA");
    address.setAddressLineStrings(addressLineList);
    skatesTownAddress.add(address);
    ctoContact.setAddressVector(skatesTownAddress);
    
    // Create second contact
    Contact salesContact = new Contact("Sandy Smith");
    salesContact.setUseType("Sales Information");
    salesContact.setDefaultDescriptionString("VP Sales");
    phoneList = new Vector();
    phoneList.add(mainPhone);
    Phone mobilePhone = new Phone("1.212.555.8888");
    mobilePhone.setUseType("Mobile");
    phoneList.add(mobilePhone);
    salesContact.setPhoneVector(phoneList);
    emailList = new Vector();
    email = new Email("sandy.smith@SkatesTown.com");
    email.setUseType("VP Sales");
    emailList.add(email);
    email = new Email("sales@SkatesTown.com");
    email.setUseType("Sales Information");
    emailList.add(email);
    salesContact.setEmailVector(emailList);
    salesContact.setAddressVector(skatesTownAddress);
    
    // Set contacts
    Contacts contacts = new Contacts();
    contacts.add(ctoContact);
    contacts.add(salesContact);
    businessEntity.setContacts(contacts);
    
    // Set identifierBag
    IdentifierBag identifierBag = new IdentifierBag();
    Vector keyedReferenceList = new Vector();
    keyedReferenceList.add(new KeyedReference("DUNS", "00-111-1111", 
      "uuid:8609C81E-EE1F-4D5A-B202-3EB13AD01823")); 
    identifierBag.setKeyedReferenceVector(keyedReferenceList);
    businessEntity.setIdentifierBag(identifierBag);
    
    // Set categoryBag
    CategoryBag categoryBag = new CategoryBag();
    keyedReferenceList = new Vector();
    keyedReferenceList.add(new KeyedReference(
      "Sporting and Athletic Goods Manufacturing", "33992", 
      "uuid:C0B9FE13-179F-413D-8A5B-5004DB8E5BB2")); 
    keyedReferenceList.add(new KeyedReference("New York", "US-NY", 
      "uuid:4E49A8D6-D5A2-4FC2-93A0-0411D8D19E88")); 
    categoryBag.setKeyedReferenceVector(keyedReferenceList);
    businessEntity.setCategoryBag(categoryBag);
    
    // Obtain authToken using get_authToken UDDI API
    AuthToken authToken = uddiProxy.get_authToken(userid, password);

    // Save businessEntity
    Vector businessEntityList = new Vector();
    businessEntityList.add(businessEntity);
    BusinessDetail businessDetail = 
      uddiProxy.save_business(authToken.getAuthInfoString(), businessEntityList);
    
    // Get businessKey for published businessEntity
    String businessKey = ((BusinessEntity)
      businessDetail.getBusinessEntityVector().elementAt(0)).getBusinessKey();
    
    // Display businessKey
    System.out.println("Published businessEntity key: " + businessKey + ".");
  }
  
  public static void main(String[] args) {
    try {
      PublishBusinessEntity publishBusinessEntity = new PublishBusinessEntity();
      publishBusinessEntity.saveBusiness(args[0], args[1]);
    }
     
    catch (Exception e) {
      System.out.println("EXCEPTION: " + e.toString());
    }
    
    System.exit(0);
  }
}
