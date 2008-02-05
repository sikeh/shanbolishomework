package com.biiblesoft.pws.hw3;

import org.apache.juddi.proxy.RegistryProxy;
import org.apache.juddi.IRegistry;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.datatype.*;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.datatype.response.*;

import java.util.Properties;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Shanbo Li
 * Date: Feb 5, 2008
 * Time: 10:52:39 AM
 *
 * @author Shanbo Li
 */
public class FindBusinessEntity {

    public static void find(String userid, String password) {
        Properties props = new Properties();
        props.setProperty(RegistryProxy.INQUIRY_ENDPOINT_PROPERTY_NAME, "http://192.168.1.100:8080/juddi/inquiry");
        props.setProperty(RegistryProxy.PUBLISH_ENDPOINT_PROPERTY_NAME, "http://192.168.1.100:8080/juddi/publish");
        IRegistry registry = new RegistryProxy(props);

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

        BusinessList businessList = null;
        try {
            businessList = registry.findBusiness((Vector) null, (DiscoveryURLs) null, identifierBag, categoryBag,
                    (TModelBag) null, (FindQualifiers) null, 10);
        } catch (RegistryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        // Get businessKey for the first businessEntity found
        BusinessInfos infos = businessList.getBusinessInfos();

        Vector businesses = infos.getBusinessInfoVector();

        if (businesses != null)
            // Display business name
            System.out.println("Name of business found:");
        {
            for (int i = 0; i < businesses.size(); i++) {
                BusinessInfo info = (BusinessInfo) businesses.elementAt(i);
                Vector outNames = info.getNameVector();
                for (int j = 0; j < outNames.size(); j++) {
                    String businessKey = info.getBusinessKey();
                    try {
                        BusinessDetail businessDetail = registry.getBusinessDetail(businessKey);
                        Vector businessEntityVector = businessDetail.getBusinessEntityVector();
                        for (int k = 0; k < businessEntityVector.size(); k++) {
                            BusinessEntity bussinessEntity = (BusinessEntity) businessEntityVector.get(k);
                            DiscoveryURLs discoveryURLs = bussinessEntity.getDiscoveryURLs();
                            Vector discoveryURLVector = discoveryURLs.getDiscoveryURLVector();
                            for (int l = 0; l < discoveryURLVector.size(); l++) {
                                DiscoveryURL discoveryURL = (DiscoveryURL) discoveryURLVector.get(l);
                                System.out.println("url -> " + discoveryURL.getValue());
                            }
                        }
                    } catch (RegistryException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    Name name = (Name) outNames.elementAt(j);
                    System.out.println(name.getValue());
                }
            }
        }
        System.out.println("\ndone.");
    }

    public static void main(String[] args) {

        find("jdoe", "jdoe");
    }

}
