# MySQL-Front 3.2  (Build 14.8)

/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='SYSTEM' */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40103 SET SQL_NOTES='ON' */;


# Host: localhost    Database: iad_production
# ------------------------------------------------------
# Server version 5.0.45-community-nt

DROP DATABASE IF EXISTS `iad_production`;
CREATE DATABASE `iad_production` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `iad_production`;

#
# Table structure for table campaigns
#

CREATE TABLE `campaigns` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `budget` varchar(255) default NULL,
  `description` text,
  `start_date` datetime default NULL,
  `end_date` datetime default NULL,
  `client_id` int(11) default NULL,
  `director_id` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `client_id` (`client_id`),
  KEY `director_id` (`director_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

#
# Dumping data for table campaigns
#

INSERT INTO `campaigns` VALUES (1,'Overstep Uppsala universitet','1000000 kr','Done\r\nObviously, KTH is the BEST!','2007-09-21 22:56:00','2007-09-26 22:56:00',8,7);
INSERT INTO `campaigns` VALUES (2,'Overstep Harvard','$1000000000000','Tough, \r\nmoney is not enough','2002-01-22 10:00:00','2012-01-01 00:00:00',8,7);
INSERT INTO `campaigns` VALUES (3,'Acquire Facebook','821442 US dolar','Facebook is popular','2007-10-29 12:48:00','2007-11-01 15:56:00',8,6);
INSERT INTO `campaigns` VALUES (4,'Sponsor Beijing Olympic','4123413848 RMB','Olympic in China 2008','2008-08-08 20:08:00','2008-12-27 17:46:00',9,6);

#
# Table structure for table campaigns_staffs
#

CREATE TABLE `campaigns_staffs` (
  `campaign_id` int(11) default NULL,
  `staff_id` int(11) default NULL,
  `active` tinyint(1) default NULL,
  KEY `campaign_id` (`campaign_id`),
  KEY `staff_id` (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Dumping data for table campaigns_staffs
#

INSERT INTO `campaigns_staffs` VALUES (2,10,NULL);
INSERT INTO `campaigns_staffs` VALUES (2,18,NULL);

#
# Table structure for table clients
#

CREATE TABLE `clients` (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(11) default NULL,
  `hashed_password` varchar(40) default NULL,
  `salt` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  `company` varchar(255) default NULL,
  `address` varchar(255) default NULL,
  `telephone` varchar(255) default NULL,
  `email` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

#
# Dumping data for table clients
#

INSERT INTO `clients` VALUES (8,'kth','202aba917521f4acbfabb277c20661d83cd36129','380682700.686896416699669','Anders Eriksson','Kungliga Tekniska hÃ¶gskolan ','ValhallavÃ¤gen 79','08-790 60 00','kth@kth.se');
INSERT INTO `clients` VALUES (9,'ericsson','91a63423054bf6cdd964c592d0fc4d36a688dd36','386007600.885585340865525','Carl-Henric Svanberg','Ericsson AB','TORSHAMNSGATAN 23, KISTA, Sweden','+46 8 719 00 00','ericsson@ericsson.com');
INSERT INTO `clients` VALUES (10,'google','deed2976772d657f1370f8446d2dd5a0e9d00dd9','375817000.465016871785344','Eric E. Schmidt,','Google','Googleplex USA','001-800-900913','google@google.com');

#
# Table structure for table directors
#

CREATE TABLE `directors` (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(11) default NULL,
  `hashed_password` varchar(40) default NULL,
  `salt` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  `telephone` varchar(255) default NULL,
  `email` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

#
# Dumping data for table directors
#

INSERT INTO `directors` VALUES (6,'sikeh','e2b4746e9489a05545d5880f463d4b4698a84a35','376296700.76632917605896','Sike Huang','0762320173','sikeh@kth.se');
INSERT INTO `directors` VALUES (7,'shanbo','1eb7a64fa543d7346c9e906df2428a12a73e769b','380063100.997198819693607','Shanbo Li','0704646157','shanboli@Gmail.com');
INSERT INTO `directors` VALUES (8,'leifl','6e2c9ae7d91fee4071ce9753a60d6b4f5545de61','384421400.299875301811143','Leif LindbÃ¤ck','************','leifl@kth.se');

#
# Table structure for table notes
#

CREATE TABLE `notes` (
  `id` int(11) NOT NULL auto_increment,
  `campaign_id` int(11) default NULL,
  `source_director_id` int(11) default NULL,
  `target_director_id` int(11) default NULL,
  `staff_id` int(11) default NULL,
  `reply` tinyint(1) default '0' COMMENT '0 = appending   1= action',
  `result` tinyint(1) default '0' COMMENT '0=Refuse                            1=OK',
  `send_time` datetime default NULL,
  `reply_time` datetime default NULL,
  PRIMARY KEY  (`id`),
  KEY `source_director_id` (`source_director_id`),
  KEY `target_director_id` (`target_director_id`),
  KEY `staff_id` (`staff_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

#
# Dumping data for table notes
#


#
# Table structure for table roots
#

CREATE TABLE `roots` (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(11) default NULL,
  `hashed_password` char(40) default NULL,
  `salt` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

#
# Dumping data for table roots
#

INSERT INTO `roots` VALUES (5,'root','73011fe234e51a5110dcedb95446d8e22aa2a2bf','380618600.226471218868444','Root');

#
# Table structure for table staffs
#

CREATE TABLE `staffs` (
  `id` int(11) NOT NULL auto_increment,
  `director_id` int(11) default NULL,
  `username` varchar(11) default NULL,
  `hashed_password` char(40) default NULL,
  `salt` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  `telephone` varchar(255) default NULL,
  `email` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `director_id` (`director_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;

#
# Dumping data for table staffs
#

INSERT INTO `staffs` VALUES (9,7,'peter','4b07eb9c672c1ab8082d611ca050fcc3cbef62fc','384416700.468548699244589','Peter Petrelli','999999999','peter@heroes.com');
INSERT INTO `staffs` VALUES (10,7,'hiro','599ec6d18f82ce2735c849c65c287bfc3ca53891','380932800.0346248941179561','Hiro Nakamura (ä¸­æ‘åºƒ Nakamura Hiro) ','8888888888888','hiro@heroes.com');
INSERT INTO `staffs` VALUES (11,7,'claire','a2a0dd7870a7194de6506eb5ee33d9dc7c6516c3','379871900.0436011476358285','Claire Bennet','77777777777','claire@heroes.com');
INSERT INTO `staffs` VALUES (12,6,'rachel','349d98928e1928f5695d779eda9777476d27b351','389304300.262943473289612','Rachel Green','111111','rachel@friends.us');
INSERT INTO `staffs` VALUES (13,6,'ross','07d070fa8faba2fdf0c470c4a2c5a0360701ee45','387942300.988273659684027','Ross Geller','222222','ross@friends.us');
INSERT INTO `staffs` VALUES (14,6,'joey','f9d0439e2fc6d20454922581f9dc59e3fce83a4a','386463000.107304206061347','Joey Tribbiani','333333','joey@friends.us');
INSERT INTO `staffs` VALUES (15,6,'phoebe','9dffa363a5b78cbe2dac30c26a2d7f56173c6867','389284700.286933551974702','Phoebe Buffay','444444','phoebe@friends.us');
INSERT INTO `staffs` VALUES (16,6,'chandler','bd64dc4c047aef9b4b1bdbdc097e39d3421a61a4','376384100.661844523486932','Chandler Bing','555555','chandler@friends.us');
INSERT INTO `staffs` VALUES (17,6,'monica','04123d7b7137711aeb1e9f4d4d39d2416c89f36d','385339800.288652251396553','Monica Geller','666666','monica@friends.us');
INSERT INTO `staffs` VALUES (18,7,'ando','da8f3c49bec3ba55b83733cfee2df2b5f4ec4fff','388383400.502366129299713','Ando Masahashi','00000000','ando@heroes.com');
INSERT INTO `staffs` VALUES (19,7,'mohinder','7b70dd7a8fde9d804e143e26b5ed59666c96c3f2','388666300.661065781247637','Mohinder Suresh','9876543','mohinder@heroes.com');
INSERT INTO `staffs` VALUES (20,7,'molly','883c163b5f13f47d6f82de23ea07e461d0384622','378257500.258988984020387','Molly Walker','32452345','molly@heroes.com');
INSERT INTO `staffs` VALUES (21,8,'bill','e09388cbf802d5d85d66783127d8df53aa32e8c3','387346200.121095350894112','Bill Gates','32.95.98.2000','bill@microsoft.com');
INSERT INTO `staffs` VALUES (22,8,'james','ec552dcd04bca33d9a61b858baae66e24e55bc41','379562600.293184772441441','James Gosling','1.1 1.2 1.3 1.4 5.0 6.0','james@sun.com');
INSERT INTO `staffs` VALUES (23,8,'linus','36e60bb04495ac92ed606e89612b5e9cb1713e35','382220400.151780122215697','Linus Torvalds','2.6.12','linus@helsinki.fi');

#
#  Foreign keys for table campaigns
#

ALTER TABLE `campaigns`
  ADD FOREIGN KEY (`director_id`) REFERENCES `directors` (`id`),
  ADD FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`);

#
#  Foreign keys for table campaigns_staffs
#

ALTER TABLE `campaigns_staffs`
  ADD FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`id`),
  ADD FOREIGN KEY (`campaign_id`) REFERENCES `campaigns` (`id`);

#
#  Foreign keys for table notes
#

ALTER TABLE `notes`
  ADD FOREIGN KEY (`source_director_id`) REFERENCES `directors` (`id`),
  ADD FOREIGN KEY (`target_director_id`) REFERENCES `directors` (`id`),
  ADD FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`id`);

#
#  Foreign keys for table staffs
#

ALTER TABLE `staffs`
  ADD FOREIGN KEY (`director_id`) REFERENCES `directors` (`id`);


/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
