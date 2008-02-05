/*
MySQL Data Transfer
Source Host: localhost
Source Database: juddi
Target Host: localhost
Target Database: juddi
Date: 2/5/2008 10:47:04
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for address
-- ----------------------------
CREATE TABLE `address` (
  `BUSINESS_KEY` varchar(41) NOT NULL,
  `CONTACT_ID` int(11) NOT NULL,
  `ADDRESS_ID` int(11) NOT NULL,
  `USE_TYPE` varchar(255) default NULL,
  `SORT_CODE` varchar(10) default NULL,
  `TMODEL_KEY` varchar(41) default NULL,
  PRIMARY KEY  (`BUSINESS_KEY`,`CONTACT_ID`,`ADDRESS_ID`),
  CONSTRAINT `address_ibfk_1` FOREIGN KEY (`BUSINESS_KEY`, `CONTACT_ID`) REFERENCES `contact` (`BUSINESS_KEY`, `CONTACT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for address_line
-- ----------------------------
CREATE TABLE `address_line` (
  `BUSINESS_KEY` varchar(41) NOT NULL,
  `CONTACT_ID` int(11) NOT NULL,
  `ADDRESS_ID` int(11) NOT NULL,
  `ADDRESS_LINE_ID` int(11) NOT NULL,
  `LINE` varchar(80) NOT NULL,
  `KEY_NAME` varchar(255) default NULL,
  `KEY_VALUE` varchar(255) default NULL,
  PRIMARY KEY  (`BUSINESS_KEY`,`CONTACT_ID`,`ADDRESS_ID`,`ADDRESS_LINE_ID`),
  CONSTRAINT `address_line_ibfk_1` FOREIGN KEY (`BUSINESS_KEY`, `CONTACT_ID`, `ADDRESS_ID`) REFERENCES `address` (`BUSINESS_KEY`, `CONTACT_ID`, `ADDRESS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for auth_token
-- ----------------------------
CREATE TABLE `auth_token` (
  `AUTH_TOKEN` varchar(51) NOT NULL,
  `PUBLISHER_ID` varchar(20) NOT NULL,
  `PUBLISHER_NAME` varchar(255) NOT NULL,
  `CREATED` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `LAST_USED` timestamp NOT NULL default '0000-00-00 00:00:00',
  `NUMBER_OF_USES` int(11) NOT NULL,
  `TOKEN_STATE` int(11) NOT NULL,
  PRIMARY KEY  (`AUTH_TOKEN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for binding_category
-- ----------------------------
CREATE TABLE `binding_category` (
  `BINDING_KEY` varchar(41) NOT NULL,
  `CATEGORY_ID` int(11) NOT NULL,
  `TMODEL_KEY_REF` varchar(41) default NULL,
  `KEY_NAME` varchar(255) default NULL,
  `KEY_VALUE` varchar(255) NOT NULL,
  PRIMARY KEY  (`BINDING_KEY`,`CATEGORY_ID`),
  CONSTRAINT `binding_category_ibfk_1` FOREIGN KEY (`BINDING_KEY`) REFERENCES `binding_template` (`BINDING_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for binding_descr
-- ----------------------------
CREATE TABLE `binding_descr` (
  `BINDING_KEY` varchar(41) NOT NULL,
  `BINDING_DESCR_ID` int(11) NOT NULL,
  `LANG_CODE` varchar(5) default NULL,
  `DESCR` varchar(255) NOT NULL,
  PRIMARY KEY  (`BINDING_KEY`,`BINDING_DESCR_ID`),
  CONSTRAINT `binding_descr_ibfk_1` FOREIGN KEY (`BINDING_KEY`) REFERENCES `binding_template` (`BINDING_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for binding_template
-- ----------------------------
CREATE TABLE `binding_template` (
  `SERVICE_KEY` varchar(41) NOT NULL,
  `BINDING_KEY` varchar(41) NOT NULL,
  `ACCESS_POINT_TYPE` varchar(20) default NULL,
  `ACCESS_POINT_URL` varchar(2000) default NULL,
  `HOSTING_REDIRECTOR` varchar(255) default NULL,
  `LAST_UPDATE` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`BINDING_KEY`),
  KEY `SERVICE_KEY` (`SERVICE_KEY`),
  CONSTRAINT `binding_template_ibfk_1` FOREIGN KEY (`SERVICE_KEY`) REFERENCES `business_service` (`SERVICE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for business_category
-- ----------------------------
CREATE TABLE `business_category` (
  `BUSINESS_KEY` varchar(41) NOT NULL,
  `CATEGORY_ID` int(11) NOT NULL,
  `TMODEL_KEY_REF` varchar(41) default NULL,
  `KEY_NAME` varchar(255) default NULL,
  `KEY_VALUE` varchar(255) NOT NULL,
  PRIMARY KEY  (`BUSINESS_KEY`,`CATEGORY_ID`),
  CONSTRAINT `business_category_ibfk_1` FOREIGN KEY (`BUSINESS_KEY`) REFERENCES `business_entity` (`BUSINESS_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for business_descr
-- ----------------------------
CREATE TABLE `business_descr` (
  `BUSINESS_KEY` varchar(41) NOT NULL,
  `BUSINESS_DESCR_ID` int(11) NOT NULL,
  `LANG_CODE` varchar(5) default NULL,
  `DESCR` varchar(255) NOT NULL,
  PRIMARY KEY  (`BUSINESS_KEY`,`BUSINESS_DESCR_ID`),
  CONSTRAINT `business_descr_ibfk_1` FOREIGN KEY (`BUSINESS_KEY`) REFERENCES `business_entity` (`BUSINESS_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for business_entity
-- ----------------------------
CREATE TABLE `business_entity` (
  `BUSINESS_KEY` varchar(41) NOT NULL,
  `AUTHORIZED_NAME` varchar(255) NOT NULL,
  `PUBLISHER_ID` varchar(20) default NULL,
  `OPERATOR` varchar(255) NOT NULL,
  `LAST_UPDATE` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`BUSINESS_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for business_identifier
-- ----------------------------
CREATE TABLE `business_identifier` (
  `BUSINESS_KEY` varchar(41) NOT NULL,
  `IDENTIFIER_ID` int(11) NOT NULL,
  `TMODEL_KEY_REF` varchar(41) default NULL,
  `KEY_NAME` varchar(255) default NULL,
  `KEY_VALUE` varchar(255) NOT NULL,
  PRIMARY KEY  (`BUSINESS_KEY`,`IDENTIFIER_ID`),
  CONSTRAINT `business_identifier_ibfk_1` FOREIGN KEY (`BUSINESS_KEY`) REFERENCES `business_entity` (`BUSINESS_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for business_name
-- ----------------------------
CREATE TABLE `business_name` (
  `BUSINESS_KEY` varchar(41) NOT NULL,
  `BUSINESS_NAME_ID` int(11) NOT NULL,
  `LANG_CODE` varchar(5) default NULL,
  `NAME` varchar(255) NOT NULL,
  PRIMARY KEY  (`BUSINESS_KEY`,`BUSINESS_NAME_ID`),
  CONSTRAINT `business_name_ibfk_1` FOREIGN KEY (`BUSINESS_KEY`) REFERENCES `business_entity` (`BUSINESS_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for business_service
-- ----------------------------
CREATE TABLE `business_service` (
  `BUSINESS_KEY` varchar(41) NOT NULL,
  `SERVICE_KEY` varchar(41) NOT NULL,
  `LAST_UPDATE` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`SERVICE_KEY`),
  KEY `BUSINESS_KEY` (`BUSINESS_KEY`),
  CONSTRAINT `business_service_ibfk_1` FOREIGN KEY (`BUSINESS_KEY`) REFERENCES `business_entity` (`BUSINESS_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for contact
-- ----------------------------
CREATE TABLE `contact` (
  `BUSINESS_KEY` varchar(41) NOT NULL,
  `CONTACT_ID` int(11) NOT NULL,
  `USE_TYPE` varchar(255) default NULL,
  `PERSON_NAME` varchar(255) NOT NULL,
  PRIMARY KEY  (`BUSINESS_KEY`,`CONTACT_ID`),
  CONSTRAINT `contact_ibfk_1` FOREIGN KEY (`BUSINESS_KEY`) REFERENCES `business_entity` (`BUSINESS_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for contact_descr
-- ----------------------------
CREATE TABLE `contact_descr` (
  `BUSINESS_KEY` varchar(41) NOT NULL,
  `CONTACT_ID` int(11) NOT NULL,
  `CONTACT_DESCR_ID` int(11) NOT NULL,
  `LANG_CODE` varchar(5) default NULL,
  `DESCR` varchar(255) NOT NULL,
  PRIMARY KEY  (`BUSINESS_KEY`,`CONTACT_ID`,`CONTACT_DESCR_ID`),
  CONSTRAINT `contact_descr_ibfk_1` FOREIGN KEY (`BUSINESS_KEY`, `CONTACT_ID`) REFERENCES `contact` (`BUSINESS_KEY`, `CONTACT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for discovery_url
-- ----------------------------
CREATE TABLE `discovery_url` (
  `BUSINESS_KEY` varchar(41) NOT NULL,
  `DISCOVERY_URL_ID` int(11) NOT NULL,
  `USE_TYPE` varchar(255) NOT NULL,
  `URL` varchar(255) NOT NULL,
  PRIMARY KEY  (`BUSINESS_KEY`,`DISCOVERY_URL_ID`),
  CONSTRAINT `discovery_url_ibfk_1` FOREIGN KEY (`BUSINESS_KEY`) REFERENCES `business_entity` (`BUSINESS_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for email
-- ----------------------------
CREATE TABLE `email` (
  `BUSINESS_KEY` varchar(41) NOT NULL,
  `CONTACT_ID` int(11) NOT NULL,
  `EMAIL_ID` int(11) NOT NULL,
  `USE_TYPE` varchar(255) default NULL,
  `EMAIL_ADDRESS` varchar(255) NOT NULL,
  PRIMARY KEY  (`BUSINESS_KEY`,`CONTACT_ID`,`EMAIL_ID`),
  CONSTRAINT `email_ibfk_1` FOREIGN KEY (`BUSINESS_KEY`, `CONTACT_ID`) REFERENCES `contact` (`BUSINESS_KEY`, `CONTACT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for instance_details_descr
-- ----------------------------
CREATE TABLE `instance_details_descr` (
  `BINDING_KEY` varchar(41) NOT NULL,
  `TMODEL_INSTANCE_INFO_ID` int(11) NOT NULL,
  `INSTANCE_DETAILS_DESCR_ID` int(11) NOT NULL,
  `LANG_CODE` varchar(5) default NULL,
  `DESCR` varchar(255) NOT NULL,
  PRIMARY KEY  (`BINDING_KEY`,`TMODEL_INSTANCE_INFO_ID`,`INSTANCE_DETAILS_DESCR_ID`),
  CONSTRAINT `instance_details_descr_ibfk_1` FOREIGN KEY (`BINDING_KEY`, `TMODEL_INSTANCE_INFO_ID`) REFERENCES `tmodel_instance_info` (`BINDING_KEY`, `TMODEL_INSTANCE_INFO_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for instance_details_doc_descr
-- ----------------------------
CREATE TABLE `instance_details_doc_descr` (
  `BINDING_KEY` varchar(41) NOT NULL,
  `TMODEL_INSTANCE_INFO_ID` int(11) NOT NULL,
  `INSTANCE_DETAILS_DOC_DESCR_ID` int(11) NOT NULL,
  `LANG_CODE` varchar(5) default NULL,
  `DESCR` varchar(255) NOT NULL,
  PRIMARY KEY  (`BINDING_KEY`,`TMODEL_INSTANCE_INFO_ID`,`INSTANCE_DETAILS_DOC_DESCR_ID`),
  CONSTRAINT `instance_details_doc_descr_ibfk_1` FOREIGN KEY (`BINDING_KEY`, `TMODEL_INSTANCE_INFO_ID`) REFERENCES `tmodel_instance_info` (`BINDING_KEY`, `TMODEL_INSTANCE_INFO_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for phone
-- ----------------------------
CREATE TABLE `phone` (
  `BUSINESS_KEY` varchar(41) NOT NULL,
  `CONTACT_ID` int(11) NOT NULL,
  `PHONE_ID` int(11) NOT NULL,
  `USE_TYPE` varchar(255) default NULL,
  `PHONE_NUMBER` varchar(50) NOT NULL,
  PRIMARY KEY  (`BUSINESS_KEY`,`CONTACT_ID`,`PHONE_ID`),
  CONSTRAINT `phone_ibfk_1` FOREIGN KEY (`BUSINESS_KEY`, `CONTACT_ID`) REFERENCES `contact` (`BUSINESS_KEY`, `CONTACT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for publisher
-- ----------------------------
CREATE TABLE `publisher` (
  `PUBLISHER_ID` varchar(20) NOT NULL,
  `PUBLISHER_NAME` varchar(255) NOT NULL,
  `EMAIL_ADDRESS` varchar(255) default NULL,
  `IS_ADMIN` varchar(5) default NULL,
  `IS_ENABLED` varchar(5) default NULL,
  `MAX_BUSINESSES` int(11) default NULL,
  `MAX_SERVICES_PER_BUSINESS` int(11) default NULL,
  `MAX_BINDINGS_PER_SERVICE` int(11) default NULL,
  `MAX_TMODELS` int(11) default NULL,
  PRIMARY KEY  (`PUBLISHER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for publisher_assertion
-- ----------------------------
CREATE TABLE `publisher_assertion` (
  `FROM_KEY` varchar(41) NOT NULL,
  `TO_KEY` varchar(41) NOT NULL,
  `TMODEL_KEY` varchar(41) NOT NULL,
  `KEY_NAME` varchar(255) NOT NULL,
  `KEY_VALUE` varchar(255) NOT NULL,
  `FROM_CHECK` varchar(5) NOT NULL,
  `TO_CHECK` varchar(5) NOT NULL,
  KEY `FROM_KEY` (`FROM_KEY`),
  KEY `TO_KEY` (`TO_KEY`),
  CONSTRAINT `publisher_assertion_ibfk_1` FOREIGN KEY (`FROM_KEY`) REFERENCES `business_entity` (`BUSINESS_KEY`),
  CONSTRAINT `publisher_assertion_ibfk_2` FOREIGN KEY (`TO_KEY`) REFERENCES `business_entity` (`BUSINESS_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for service_category
-- ----------------------------
CREATE TABLE `service_category` (
  `SERVICE_KEY` varchar(41) NOT NULL,
  `CATEGORY_ID` int(11) NOT NULL,
  `TMODEL_KEY_REF` varchar(41) default NULL,
  `KEY_NAME` varchar(255) default NULL,
  `KEY_VALUE` varchar(255) NOT NULL,
  PRIMARY KEY  (`SERVICE_KEY`,`CATEGORY_ID`),
  CONSTRAINT `service_category_ibfk_1` FOREIGN KEY (`SERVICE_KEY`) REFERENCES `business_service` (`SERVICE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for service_descr
-- ----------------------------
CREATE TABLE `service_descr` (
  `SERVICE_KEY` varchar(41) NOT NULL,
  `SERVICE_DESCR_ID` int(11) NOT NULL,
  `LANG_CODE` varchar(5) default NULL,
  `DESCR` varchar(255) NOT NULL,
  PRIMARY KEY  (`SERVICE_KEY`,`SERVICE_DESCR_ID`),
  CONSTRAINT `service_descr_ibfk_1` FOREIGN KEY (`SERVICE_KEY`) REFERENCES `business_service` (`SERVICE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for service_name
-- ----------------------------
CREATE TABLE `service_name` (
  `SERVICE_KEY` varchar(41) NOT NULL,
  `SERVICE_NAME_ID` int(11) NOT NULL,
  `LANG_CODE` varchar(5) default NULL,
  `NAME` varchar(255) NOT NULL,
  PRIMARY KEY  (`SERVICE_KEY`,`SERVICE_NAME_ID`),
  CONSTRAINT `service_name_ibfk_1` FOREIGN KEY (`SERVICE_KEY`) REFERENCES `business_service` (`SERVICE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tmodel
-- ----------------------------
CREATE TABLE `tmodel` (
  `TMODEL_KEY` varchar(41) NOT NULL,
  `AUTHORIZED_NAME` varchar(255) NOT NULL,
  `PUBLISHER_ID` varchar(20) default NULL,
  `OPERATOR` varchar(255) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `LANG_CODE` varchar(5) default NULL,
  `OVERVIEW_URL` varchar(255) default NULL,
  `DELETED` varchar(5) default NULL,
  `LAST_UPDATE` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`TMODEL_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tmodel_category
-- ----------------------------
CREATE TABLE `tmodel_category` (
  `TMODEL_KEY` varchar(41) NOT NULL,
  `CATEGORY_ID` int(11) NOT NULL,
  `TMODEL_KEY_REF` varchar(255) default NULL,
  `KEY_NAME` varchar(255) default NULL,
  `KEY_VALUE` varchar(255) NOT NULL,
  PRIMARY KEY  (`TMODEL_KEY`,`CATEGORY_ID`),
  CONSTRAINT `tmodel_category_ibfk_1` FOREIGN KEY (`TMODEL_KEY`) REFERENCES `tmodel` (`TMODEL_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tmodel_descr
-- ----------------------------
CREATE TABLE `tmodel_descr` (
  `TMODEL_KEY` varchar(41) NOT NULL,
  `TMODEL_DESCR_ID` int(11) NOT NULL,
  `LANG_CODE` varchar(5) default NULL,
  `DESCR` varchar(255) NOT NULL,
  PRIMARY KEY  (`TMODEL_KEY`,`TMODEL_DESCR_ID`),
  CONSTRAINT `tmodel_descr_ibfk_1` FOREIGN KEY (`TMODEL_KEY`) REFERENCES `tmodel` (`TMODEL_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tmodel_doc_descr
-- ----------------------------
CREATE TABLE `tmodel_doc_descr` (
  `TMODEL_KEY` varchar(41) NOT NULL,
  `TMODEL_DOC_DESCR_ID` int(11) NOT NULL,
  `LANG_CODE` varchar(5) default NULL,
  `DESCR` varchar(255) NOT NULL,
  PRIMARY KEY  (`TMODEL_KEY`,`TMODEL_DOC_DESCR_ID`),
  CONSTRAINT `tmodel_doc_descr_ibfk_1` FOREIGN KEY (`TMODEL_KEY`) REFERENCES `tmodel` (`TMODEL_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tmodel_identifier
-- ----------------------------
CREATE TABLE `tmodel_identifier` (
  `TMODEL_KEY` varchar(41) NOT NULL,
  `IDENTIFIER_ID` int(11) NOT NULL,
  `TMODEL_KEY_REF` varchar(255) default NULL,
  `KEY_NAME` varchar(255) default NULL,
  `KEY_VALUE` varchar(255) NOT NULL,
  PRIMARY KEY  (`TMODEL_KEY`,`IDENTIFIER_ID`),
  CONSTRAINT `tmodel_identifier_ibfk_1` FOREIGN KEY (`TMODEL_KEY`) REFERENCES `tmodel` (`TMODEL_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tmodel_instance_info
-- ----------------------------
CREATE TABLE `tmodel_instance_info` (
  `BINDING_KEY` varchar(41) NOT NULL,
  `TMODEL_INSTANCE_INFO_ID` int(11) NOT NULL,
  `TMODEL_KEY` varchar(41) NOT NULL,
  `OVERVIEW_URL` varchar(255) default NULL,
  `INSTANCE_PARMS` varchar(255) default NULL,
  PRIMARY KEY  (`BINDING_KEY`,`TMODEL_INSTANCE_INFO_ID`),
  CONSTRAINT `tmodel_instance_info_ibfk_1` FOREIGN KEY (`BINDING_KEY`) REFERENCES `binding_template` (`BINDING_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tmodel_instance_info_descr
-- ----------------------------
CREATE TABLE `tmodel_instance_info_descr` (
  `BINDING_KEY` varchar(41) NOT NULL,
  `TMODEL_INSTANCE_INFO_ID` int(11) NOT NULL,
  `TMODEL_INSTANCE_INFO_DESCR_ID` int(11) NOT NULL,
  `LANG_CODE` varchar(5) default NULL,
  `DESCR` varchar(255) NOT NULL,
  PRIMARY KEY  (`BINDING_KEY`,`TMODEL_INSTANCE_INFO_ID`,`TMODEL_INSTANCE_INFO_DESCR_ID`),
  CONSTRAINT `tmodel_instance_info_descr_ibfk_1` FOREIGN KEY (`BINDING_KEY`, `TMODEL_INSTANCE_INFO_ID`) REFERENCES `tmodel_instance_info` (`BINDING_KEY`, `TMODEL_INSTANCE_INFO_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `publisher` VALUES ('jdoe', 'jdoe', 'jdoe@juddi.org', 'true', 'true', '25', '20', '10', '100');
