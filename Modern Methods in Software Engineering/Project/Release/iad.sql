/*
MySQL Data Transfer
Source Host: localhost
Source Database: iad_development
Target Host: localhost
Target Database: iad_development
Date: 2007-10-29 23:57:18
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for campaigns
-- ----------------------------
CREATE TABLE `campaigns` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `budget` varchar(255) DEFAULT NULL,
  `description` text,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `client_id` int(11) DEFAULT NULL,
  `director_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `client_id` (`client_id`),
  KEY `director_id` (`director_id`),
  CONSTRAINT `campaigns_ibfk_1` FOREIGN KEY (`director_id`) REFERENCES `directors` (`id`),
  CONSTRAINT `campaigns_ibfk_2` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for campaigns_staffs
-- ----------------------------
CREATE TABLE `campaigns_staffs` (
  `campaign_id` int(11) DEFAULT NULL,
  `staff_id` int(11) DEFAULT NULL,
  `active` tinyint(1) DEFAULT NULL,
  KEY `campaign_id` (`campaign_id`),
  KEY `staff_id` (`staff_id`),
  CONSTRAINT `campaigns_staffs_ibfk_1` FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`id`),
  CONSTRAINT `campaigns_staffs_ibfk_2` FOREIGN KEY (`campaign_id`) REFERENCES `campaigns` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for clients
-- ----------------------------
CREATE TABLE `clients` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(11) DEFAULT NULL,
  `hashed_password` varchar(40) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `company` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for directors
-- ----------------------------
CREATE TABLE `directors` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(11) DEFAULT NULL,
  `hashed_password` varchar(40) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for notes
-- ----------------------------
CREATE TABLE `notes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `campaign_id` int(11) DEFAULT NULL,
  `source_director_id` int(11) DEFAULT NULL,
  `target_director_id` int(11) DEFAULT NULL,
  `staff_id` int(11) DEFAULT NULL,
  `reply` tinyint(1) DEFAULT '0' COMMENT '0 = appending   1= action',
  `result` tinyint(1) DEFAULT '0' COMMENT '0=Refuse                            1=OK',
  `send_time` datetime DEFAULT NULL,
  `reply_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `source_director_id` (`source_director_id`),
  KEY `target_director_id` (`target_director_id`),
  KEY `staff_id` (`staff_id`),
  CONSTRAINT `notes_ibfk_1` FOREIGN KEY (`source_director_id`) REFERENCES `directors` (`id`),
  CONSTRAINT `notes_ibfk_2` FOREIGN KEY (`target_director_id`) REFERENCES `directors` (`id`),
  CONSTRAINT `notes_ibfk_3` FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for roots
-- ----------------------------
CREATE TABLE `roots` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(11) DEFAULT NULL,
  `hashed_password` char(40) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for staffs
-- ----------------------------
CREATE TABLE `staffs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `director_id` int(11) DEFAULT NULL,
  `username` varchar(11) DEFAULT NULL,
  `hashed_password` char(40) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `director_id` (`director_id`),
  CONSTRAINT `staffs_ibfk_1` FOREIGN KEY (`director_id`) REFERENCES `directors` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `campaigns` VALUES ('1', 'Overstep Uppsala universitet', '1000000 kr', 'Done\r\nObviously, KTH is the BEST!', '2007-09-21 22:56:00', '2007-09-26 22:56:00', '8', '7');
INSERT INTO `campaigns` VALUES ('2', 'Overstep Harvard', '$1000000000000', 'Tough, \r\nmoney is not enough', '2002-01-22 10:00:00', '2012-01-01 00:00:00', '8', '7');
INSERT INTO `campaigns` VALUES ('3', 'Acquire Facebook', '821442 US dolar', 'Facebook is popular', '2007-10-29 12:48:00', '2007-11-01 15:56:00', '8', '6');
INSERT INTO `campaigns` VALUES ('4', 'Sponsor Beijing Olympic', '4123413848 RMB', 'Olympic in China 2008', '2008-08-08 20:08:00', '2008-12-27 17:46:00', '9', '6');
INSERT INTO `campaigns_staffs` VALUES ('2', '10', null);
INSERT INTO `campaigns_staffs` VALUES ('2', '18', null);
INSERT INTO `clients` VALUES ('8', 'kth', '202aba917521f4acbfabb277c20661d83cd36129', '380682700.686896416699669', 'Anders Eriksson', 'Kungliga Tekniska hÃ¶gskolan ', 'ValhallavÃ¤gen 79', '08-790 60 00', 'kth@kth.se');
INSERT INTO `clients` VALUES ('9', 'ericsson', '91a63423054bf6cdd964c592d0fc4d36a688dd36', '386007600.885585340865525', 'Carl-Henric Svanberg', 'Ericsson AB', 'TORSHAMNSGATAN 23, KISTA, Sweden', '+46 8 719 00 00', 'ericsson@ericsson.com');
INSERT INTO `clients` VALUES ('10', 'google', 'deed2976772d657f1370f8446d2dd5a0e9d00dd9', '375817000.465016871785344', 'Eric E. Schmidt,', 'Google', 'Googleplex USA', '001-800-900913', 'google@google.com');
INSERT INTO `directors` VALUES ('6', 'sikeh', 'e2b4746e9489a05545d5880f463d4b4698a84a35', '376296700.76632917605896', 'Sike Huang', '0762320173', 'sikeh@kth.se');
INSERT INTO `directors` VALUES ('7', 'shanbo', '1eb7a64fa543d7346c9e906df2428a12a73e769b', '380063100.997198819693607', 'Shanbo Li', '0704646157', 'shanboli@Gmail.com');
INSERT INTO `directors` VALUES ('8', 'leifl', '6e2c9ae7d91fee4071ce9753a60d6b4f5545de61', '384421400.299875301811143', 'Leif LindbÃ¤ck', '************', 'leifl@kth.se');
INSERT INTO `roots` VALUES ('5', 'root', '73011fe234e51a5110dcedb95446d8e22aa2a2bf', '380618600.226471218868444', 'Root');
INSERT INTO `staffs` VALUES ('9', '7', 'peter', '4b07eb9c672c1ab8082d611ca050fcc3cbef62fc', '384416700.468548699244589', 'Peter Petrelli', '999999999', 'peter@heroes.com');
INSERT INTO `staffs` VALUES ('10', '7', 'hiro', '599ec6d18f82ce2735c849c65c287bfc3ca53891', '380932800.0346248941179561', 'Hiro Nakamura (ä¸­æ‘åºƒ Nakamura Hiro) ', '8888888888888', 'hiro@heroes.com');
INSERT INTO `staffs` VALUES ('11', '7', 'claire', 'a2a0dd7870a7194de6506eb5ee33d9dc7c6516c3', '379871900.0436011476358285', 'Claire Bennet', '77777777777', 'claire@heroes.com');
INSERT INTO `staffs` VALUES ('12', '6', 'rachel', '349d98928e1928f5695d779eda9777476d27b351', '389304300.262943473289612', 'Rachel Green', '111111', 'rachel@friends.us');
INSERT INTO `staffs` VALUES ('13', '6', 'ross', '07d070fa8faba2fdf0c470c4a2c5a0360701ee45', '387942300.988273659684027', 'Ross Geller', '222222', 'ross@friends.us');
INSERT INTO `staffs` VALUES ('14', '6', 'joey', 'f9d0439e2fc6d20454922581f9dc59e3fce83a4a', '386463000.107304206061347', 'Joey Tribbiani', '333333', 'joey@friends.us');
INSERT INTO `staffs` VALUES ('15', '6', 'phoebe', '9dffa363a5b78cbe2dac30c26a2d7f56173c6867', '389284700.286933551974702', 'Phoebe Buffay', '444444', 'phoebe@friends.us');
INSERT INTO `staffs` VALUES ('16', '6', 'chandler', 'bd64dc4c047aef9b4b1bdbdc097e39d3421a61a4', '376384100.661844523486932', 'Chandler Bing', '555555', 'chandler@friends.us');
INSERT INTO `staffs` VALUES ('17', '6', 'monica', '04123d7b7137711aeb1e9f4d4d39d2416c89f36d', '385339800.288652251396553', 'Monica Geller', '666666', 'monica@friends.us');
INSERT INTO `staffs` VALUES ('18', '7', 'ando', 'da8f3c49bec3ba55b83733cfee2df2b5f4ec4fff', '388383400.502366129299713', 'Ando Masahashi', '00000000', 'ando@heroes.com');
INSERT INTO `staffs` VALUES ('19', '7', 'mohinder', '7b70dd7a8fde9d804e143e26b5ed59666c96c3f2', '388666300.661065781247637', 'Mohinder Suresh', '9876543', 'mohinder@heroes.com');
INSERT INTO `staffs` VALUES ('20', '7', 'molly', '883c163b5f13f47d6f82de23ea07e461d0384622', '378257500.258988984020387', 'Molly Walker', '32452345', 'molly@heroes.com');
INSERT INTO `staffs` VALUES ('21', '8', 'bill', 'e09388cbf802d5d85d66783127d8df53aa32e8c3', '387346200.121095350894112', 'Bill Gates', '32.95.98.2000', 'bill@microsoft.com');
INSERT INTO `staffs` VALUES ('22', '8', 'james', 'ec552dcd04bca33d9a61b858baae66e24e55bc41', '379562600.293184772441441', 'James Gosling', '1.1 1.2 1.3 1.4 5.0 6.0', 'james@sun.com');
INSERT INTO `staffs` VALUES ('23', '8', 'linus', '36e60bb04495ac92ed606e89612b5e9cb1713e35', '382220400.151780122215697', 'Linus Torvalds', '2.6.12', 'linus@helsinki.fi');
