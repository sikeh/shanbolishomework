/*
MySQL Data Transfer
Source Host: localhost
Source Database: iad_development
Target Host: localhost
Target Database: iad_development
Date: 2007-10-28 20:18:53
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `campaigns` VALUES ('1', 'KTH coder', '10000', 'it works', '2007-10-27 15:25:00', '2007-10-30 19:25:00', '1', '1');
INSERT INTO `campaigns` VALUES ('2', 'beat Google', '1', 'mission impossible', '2012-10-27 15:52:00', '2012-12-27 15:52:00', '2', '3');
INSERT INTO `campaigns` VALUES ('3', 'beat UU', '1000000000', 'done', '2003-10-27 15:53:00', '2005-10-27 15:53:00', '6', '2');
INSERT INTO `campaigns` VALUES ('4', 'asdfasdf', '0', 'asdf', '2007-10-28 19:09:00', '2007-10-31 23:09:00', '1', '1');
INSERT INTO `campaigns` VALUES ('5', '234324', '234234', '234234', '2007-08-28 19:10:00', '2007-12-28 19:10:00', '1', '2');
INSERT INTO `campaigns` VALUES ('6', 'ttt', '0', 'asdf', '2007-10-28 19:12:00', '2007-12-28 19:12:00', '3', '1');
INSERT INTO `campaigns` VALUES ('9', 'test1', '3', '2', '2007-10-28 19:13:00', '2007-12-28 19:13:00', '1', '2');
INSERT INTO `campaigns` VALUES ('10', 'test2', '0', 'asdf', '2007-01-28 19:14:00', '2007-09-28 19:14:00', '4', '2');
INSERT INTO `campaigns_staffs` VALUES ('2', '2', null);
INSERT INTO `campaigns_staffs` VALUES ('1', '1', null);
INSERT INTO `campaigns_staffs` VALUES ('1', '2', null);
INSERT INTO `campaigns_staffs` VALUES ('3', '4', null);
INSERT INTO `campaigns_staffs` VALUES ('9', '4', null);
INSERT INTO `clients` VALUES ('1', 'sikeh', 'sikeh', null, 'Sike Huang', 'KTH', 'Sweden', '123456', 'sikeh@kth.se');
INSERT INTO `clients` VALUES ('2', 'shanbo', 'shanbo', null, 'Shanbo Li', 'KTH', 'USA', '999999', 'shanbo@kth.se');
INSERT INTO `clients` VALUES ('3', 'jay', 'jay', null, 'Jay Chow', 'jc', 'asdf', '131231', 'asfa@asdfasdf.com');
INSERT INTO `clients` VALUES ('4', 'ljr', 'ljr', null, 'Liang Jing Ru', 'Sony', 'JP', '99998888', 'un known');
INSERT INTO `clients` VALUES ('5', 'c', '1d1b2989b2c5eae403635d8a2f65a1b09ef01979', '383358700.287945222266877', 'c', 'c', 'c', 'c', 'c');
INSERT INTO `clients` VALUES ('6', 'works', 'ce69df3d7b95e3e18c7b3e1663d6ad9f8a3e190c', '381317500.162985962300821', 'works', 'works', 'works', 'works', 'works');
INSERT INTO `directors` VALUES ('1', 'loux', 'loux', null, 'Lou Xiao', '123', 'loux@kth.se');
INSERT INTO `directors` VALUES ('2', 'leif', 'fa2e463ba7131f3c9d9daa2f907c66ed7d188aa3', '385667400.0149739770500781', 'Leif', '0001112', 'leif@kth.se');
INSERT INTO `directors` VALUES ('3', 'e', '57a90dd70af54e840afc56319d7eee6d0d89d2d1', '384764200.115376250026253', 'e', 'e', 'e');
INSERT INTO `notes` VALUES ('9', '1', '1', '2', '2', '1', '1', '2007-10-28 13:57:38', '2007-10-28 13:57:49');
INSERT INTO `notes` VALUES ('10', '1', '1', '2', '4', '1', '1', '2007-10-28 14:19:15', '2007-10-28 14:37:10');
INSERT INTO `notes` VALUES ('12', '1', '1', '2', '1', '1', '1', '2007-10-28 15:19:44', '2007-10-28 15:19:59');
INSERT INTO `notes` VALUES ('13', '1', '1', '2', '2', '1', '1', '2007-10-28 15:19:44', '2007-10-28 15:20:00');
INSERT INTO `notes` VALUES ('14', '1', '1', '2', '3', '1', '0', '2007-10-28 15:19:44', '2007-10-28 15:20:01');
INSERT INTO `notes` VALUES ('15', '9', '2', '1', '1', '0', '0', '2007-10-28 19:15:00', null);
INSERT INTO `roots` VALUES ('1', 'meidan', '361f45d21e6fc935161d5a25b18e42011305120a', '384129600.876833633813021', 'Meidan Li');
INSERT INTO `roots` VALUES ('2', 'a', 'fe8dc372f128861092585f0b0fda4325c72de46b', '390100100.335556405669301', 'a');
INSERT INTO `roots` VALUES ('3', 'b', 'fdcc41bda1410bf19a5f1f16beb1be34987b00e6', '379724800.309281568724261', '123');
INSERT INTO `staffs` VALUES ('1', '1', 'wzf', '63ebe6dac8801333cf88f85e232f8cb401acbe81', '385420600.419888338128913', 'wei zhen fang', '000', 'no');
INSERT INTO `staffs` VALUES ('2', '2', 'yy', 'yy', null, 'yan yi', '234', '4324');
INSERT INTO `staffs` VALUES ('3', '2', 'mg', 'mg', null, 'Ghdoat', '12345', 'mg@puv.fi');
INSERT INTO `staffs` VALUES ('4', '2', 'gc', 'gc', null, 'Gao Chao', '123456', 'gc@puv.fi');
INSERT INTO `staffs` VALUES ('5', '3', 'q', '4206fdf384fdc15ffea10606d7f101195525aee4', '383569300.187695193706421', 'q', 'q', 'q');
INSERT INTO `staffs` VALUES ('6', '2', 'cool', '694f01681a2f2bb343c3d3b8832c9821bc4f07af', '389939900.490841581176099', 'cool', 'cool', 'cool');
INSERT INTO `staffs` VALUES ('7', '1', 'staff3', '8a5b8ce67b827fdf59cb4509a7d424f71ed90ab4', '388996000.421578730169285', 'staff3', '12313', '12312');
