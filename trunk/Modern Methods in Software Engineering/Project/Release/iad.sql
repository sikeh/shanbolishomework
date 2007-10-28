/*
MySQL Data Transfer
Source Host: localhost
Source Database: iad_development
Target Host: localhost
Target Database: iad_development
Date: 2007-10-28 21:27:11
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

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
INSERT INTO `campaigns_staffs` VALUES ('3', '4', null);
INSERT INTO `campaigns_staffs` VALUES ('9', '4', null);
INSERT INTO `campaigns_staffs` VALUES ('1', '1', null);
INSERT INTO `campaigns_staffs` VALUES ('1', '2', null);
INSERT INTO `clients` VALUES ('1', 'sikeh', 'sikeh', null, 'Sike Huang', 'KTH', 'Sweden', '123456', 'sikeh@kth.se');
INSERT INTO `clients` VALUES ('2', 'shanbo', 'shanbo', null, 'Shanbo Li', 'KTH', 'USA', '999999', 'shanbo@kth.se');
INSERT INTO `clients` VALUES ('3', 'jay', 'jay', null, 'Jay Chow', 'jc', 'asdf', '131231', 'asfa@asdfasdf.com');
INSERT INTO `clients` VALUES ('4', 'ljr', 'ljr', null, 'Liang Jing Ru', 'Sony', 'JP', '99998888', 'un known');
INSERT INTO `clients` VALUES ('5', 'c', '1d1b2989b2c5eae403635d8a2f65a1b09ef01979', '383358700.287945222266877', 'c', 'c', 'c', 'c', 'c');
INSERT INTO `clients` VALUES ('6', 'works', 'ce69df3d7b95e3e18c7b3e1663d6ad9f8a3e190c', '381317500.162985962300821', 'works', 'works', 'works', 'works', 'works');
INSERT INTO `clients` VALUES ('7', 'a', '258c6011d42b81e6590718cd603f8a34e3c1f622', '389344300.334815128584871', 'a', 'a', 'a', 'a', 'a');
INSERT INTO `directors` VALUES ('1', 'loux', '657c085d800e41584f1da6a547452b00f97c9bb3', '376545600.0197922125819672', 'Lou Xiao', '123', 'loux@kth.se');
INSERT INTO `directors` VALUES ('2', 'leif', 'f3c77c95d5412908f2fff65dc10c6d82c583de46', '384655000.150723629533759', 'Leif', '0001112', 'leif@kth.se');
INSERT INTO `directors` VALUES ('3', 'e', '49a9ba1b8d7d6301c435cabcf47f6fa180c52f8c', '377839900.169480658513017', 'e', 'e', 'e');
INSERT INTO `directors` VALUES ('4', 'director', '9e639dec21827513bd91b2bbe20d25b40b575098', '379246700.744367318117973', 'director', 'director', 'director');
INSERT INTO `notes` VALUES ('9', '1', '1', '2', '2', '1', '1', '2007-10-28 13:57:38', '2007-10-28 13:57:49');
INSERT INTO `notes` VALUES ('10', '1', '1', '2', '4', '1', '1', '2007-10-28 14:19:15', '2007-10-28 14:37:10');
INSERT INTO `notes` VALUES ('12', '1', '1', '2', '1', '1', '1', '2007-10-28 15:19:44', '2007-10-28 15:19:59');
INSERT INTO `notes` VALUES ('13', '1', '1', '2', '2', '1', '1', '2007-10-28 15:19:44', '2007-10-28 15:20:00');
INSERT INTO `notes` VALUES ('14', '1', '1', '2', '3', '1', '0', '2007-10-28 15:19:44', '2007-10-28 15:20:01');
INSERT INTO `notes` VALUES ('15', '9', '2', '1', '1', '0', '0', '2007-10-28 19:15:00', null);
INSERT INTO `notes` VALUES ('16', '1', '1', '2', '7', '0', '0', '2007-10-28 21:24:36', null);
INSERT INTO `roots` VALUES ('5', 'root', '73011fe234e51a5110dcedb95446d8e22aa2a2bf', '380618600.226471218868444', 'Root');
INSERT INTO `roots` VALUES ('7', '1', '8b0b228ac47de1ec9fd8c724147b4c9f41780980', '388537800.756923345025414', '1');
INSERT INTO `roots` VALUES ('8', 'meidan', '95c690f34a5a58d671df2a7db3ea09cee171c777', '389961100.266758964541309', 'Meidan Li');
INSERT INTO `roots` VALUES ('9', 'sike', '2e60c201ef79fcfd1fb72e6c54f900871f43c9af', '388591500.797993380592681', 'sike');
INSERT INTO `roots` VALUES ('10', '111', '4323b5549549832bc97d1d24ee1693a62608a921', '379364500.688092060388774', '333');
INSERT INTO `staffs` VALUES ('1', '1', 'wzf', 'b6c93ca2eaebc484e685814aa11c714653b8e0ac', '375509500.548487871628172', 'wei zhen fang', '000', 'no');
INSERT INTO `staffs` VALUES ('2', '1', 'yy', '4c3b636786299d1a2c772ba14bbf83d6f7d5985b', '382841000.675124474758862', 'yan yi', '234', '4324');
INSERT INTO `staffs` VALUES ('3', '1', 'mg', 'd2da14c29321df2a124b29b0185be113dedf66e9', '376453500.513010730595775', 'Ghdoat', '12345', 'mg@puv.fi');
INSERT INTO `staffs` VALUES ('4', '1', 'gc', 'bcdef994a1d9b7e3a9ff0a58e8368b2dd07ac3f2', '384690400.886570998690431', 'Gao Chao', '123456', 'gc@puv.fi');
INSERT INTO `staffs` VALUES ('5', '1', 'q', 'e765250fe92b686f4cebd9e865e7a6cc3275859b', '377410600.538406642906117', 'q', 'q', 'q');
INSERT INTO `staffs` VALUES ('6', '1', 'cool', '3c14928f40bb619d25f8a9c7357a2725f61a3caa', '386448900.0431392616860046', 'cool', 'cool', 'cool');
INSERT INTO `staffs` VALUES ('7', '2', 'staff3', 'e545f20742a8c5ab3badc90e48d404569110e452', '378632100.289744058562519', 'staff3', '12313', '12312');
INSERT INTO `staffs` VALUES ('8', '1', 'staff', '938d7bf97f2a7cf2125a5f0de68db28949785885', '386655800.627628093054484', 'staff', '1234', '124');
