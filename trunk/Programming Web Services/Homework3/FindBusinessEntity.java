import org.uddi4j.client.*;
import org.uddi4j.datatype.*;
import org.uddi4j.datatype.business.*;
import org.uddi4j.datatype.tmodel.*;
import org.uddi4j.response.*;
import org.uddi4j.util.*;

import java.util.*;

/**
 * Chapter 6 - Find BusinessEntity Example
 * 
 * This example shows how to find the businessEntity
 * that represents SkatesTown.
 */
public class FindBusinessEntity {
  // Location of UDDI registry
  String inquiryURL =  "http://uddi.ibm.com/testregistry/inquiryapi";

  /**
   * Find the businessEntity and then get its details.
   */
  protected void getBusinessDetail() throws Exception {
    UDDIProxy uddiProxy = null;

    // Create UDDI proxy
    uddiProxy = new UDDIProxy();
    uddiProxy.setInquiryURL(inquiryURL);
        
    // Create identifierBag
    IdentifierBag identifierBag = new IdentifierBag();
    Vector keyedReferenceList = new Vector();
    keyedReferenceList.add(new KeyedReference("DUNS", "00-111-1111", 
      "uuid:8609C81E-EE1F-4D5A-B202-3EB13AD01823")); 
    identifierBag.setKeyedReferenceVector(keyedReferenceList);
    
    // Create categoryBag
    CategoryBag categoryBag = new CategoryBag();
    keyedReferenceList = new Vector();
    keyedReferenceList.add(new KeyedReference(
      "Sporting and Athletic Goods Manufacturing", "33992", 
      "uuid:C0B9FE13-179F-413D-8A5B-5004DB8E5BB2")); 
    keyedReferenceList.add(new KeyedReference("New York", "US-NY", 
      "uuid:4E49A8D6-D5A2-4FC2-93A0-0411D8D19E88")); 
    categoryBag.setKeyedReferenceVector(keyedReferenceList);
    
    // Find the businessEntity using just the identifierBag 
    // and categoryBag as a search criteria 
    BusinessList businessList = uddiProxy.find_business((Vector) null, 
        (DiscoveryURLs) null, 
        identifierBag, categoryBag, 
        (TModelBag) null, (FindQualifiers) null, 10);
    
    // Get businessKey for the first businessEntity found
    String businessKey = 
      ((BusinessInfo) businessList.getBusinessInfos().get(0)).getBusinessKey();
    
    // Display businessKey
    System.out.println("Key for businessEntity found: " + businessKey + ".");
    
    // Get the businessEntity details
    BusinessDetail businessDetail = uddiProxy.get_businessDetail(businessKey);
    
    // Get first business name
    Name businessName = (Name) ((BusinessEntity)
    businessDetail.getBusinessEntityVector().elementAt(0)).getNameVector().get(0);
    
    // Display business name
    System.out.println("Name of businessEntity found: " + 
      businessName.getText() + ".");
  }
  
  public static void main(String[] args) {
    try {
      FindBusinessEntity findBusinessEntity = new FindBusinessEntity();
      findBusinessEntity.getBusinessDetail();
    }
     
    catch (Exception e) {
      System.out.println("EXCEPTION: " + e.toString());
    }
    
    System.exit(0);
  }
}
