/*
MySQL Data Transfer
Source Host: localhost
Source Database: iad_development
Target Host: localhost
Target Database: iad_development
Date: 2007-10-28 8:33:03
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for campaigns
-- ----------------------------
CREATE TABLE `campaigns` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `budget` int(11) DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

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
  `password` varchar(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `company` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for directors
-- ----------------------------
CREATE TABLE `directors` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(11) DEFAULT NULL,
  `password` varchar(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for notes
-- ----------------------------
CREATE TABLE `notes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `source_director_id` int(11) DEFAULT NULL,
  `target_director_id` int(11) DEFAULT NULL,
  `staff_id` int(11) DEFAULT NULL,
  `acknowledge` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `source_director_id` (`source_director_id`),
  KEY `target_director_id` (`target_director_id`),
  KEY `staff_id` (`staff_id`),
  CONSTRAINT `notes_ibfk_1` FOREIGN KEY (`source_director_id`) REFERENCES `directors` (`id`),
  CONSTRAINT `notes_ibfk_2` FOREIGN KEY (`target_director_id`) REFERENCES `directors` (`id`),
  CONSTRAINT `notes_ibfk_3` FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for staffs
-- ----------------------------
CREATE TABLE `staffs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `director_id` int(11) DEFAULT NULL,
  `username` varchar(11) DEFAULT NULL,
  `password` varchar(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `director_id` (`director_id`),
  CONSTRAINT `staffs_ibfk_1` FOREIGN KEY (`director_id`) REFERENCES `directors` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `campaigns` VALUES ('1', 'KTH coder', '10000', 'it works', '2007-10-27 15:25:00', '2007-10-30 19:25:00', '1', '1');
INSERT INTO `campaigns` VALUES ('2', 'beat Google', '1', 'mission impossible', '2012-10-27 15:52:00', '2012-12-27 15:52:00', '2', '2');
INSERT INTO `campaigns` VALUES ('3', 'beat UU', '1000000000', 'done', '2003-10-27 15:53:00', '2005-10-27 15:53:00', '1', '2');
INSERT INTO `campaigns_staffs` VALUES ('2', '2', null);
INSERT INTO `campaigns_staffs` VALUES ('1', '1', null);
INSERT INTO `clients` VALUES ('1', 'sikeh', 'sikeh', 'Sike Huang', 'KTH', 'Sweden', '123456', 'sikeh@kth.se');
INSERT INTO `clients` VALUES ('2', 'shanbo', 'shanbo', 'Shanbo Li', 'KTH', 'USA', '999999', 'shanbo@kth.se');
INSERT INTO `clients` VALUES ('3', 'jay', 'jay', 'Jay Chow', 'jc', 'asdf', '131231', 'asfa@asdfasdf.com');
INSERT INTO `clients` VALUES ('4', 'ljr', 'ljr', 'Liang Jing Ru', 'Sony', 'JP', '99998888', 'un known');
INSERT INTO `directors` VALUES ('1', 'loux', 'loux', 'Lou Xiao', '123', 'loux@kth.se');
INSERT INTO `directors` VALUES ('2', 'leif', 'leif', 'Leif', '0001112', 'leif@kth.se');
INSERT INTO `notes` VALUES ('7', '1', '2', '2', '0');
INSERT INTO `staffs` VALUES ('1', '1', 'wzf', 'wzf', 'wei zhen fang', '000', 'no');
INSERT INTO `staffs` VALUES ('2', '2', 'yy', 'yy', 'yan yi', '234', '4324');
INSERT INTO `staffs` VALUES ('3', '1', 'mg', 'mg', 'Ghdoat', '12345', 'mg@puv.fi');
INSERT INTO `staffs` VALUES ('4', '2', 'gc', 'gc', 'Gao Chao', '123456', 'gc@puv.fi');
