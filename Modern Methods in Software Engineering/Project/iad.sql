/*
MySQL Data Transfer
Source Host: localhost
Source Database: iad
Target Host: localhost
Target Database: iad
Date: 2007-10-26 23:59:23
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for campaigns
-- ----------------------------
CREATE TABLE `campaigns` (
  `id` int(11) NOT NULL auto_increment,
  `start_date` datetime default NULL,
  `end_date` datetime default NULL,
  `client_id` int(11) default NULL,
  `director_id` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `f_client_id` (`client_id`),
  KEY `f_director_id` (`director_id`),
  CONSTRAINT `campaigns_ibfk_1` FOREIGN KEY (`director_id`) REFERENCES `directors` (`id`),
  CONSTRAINT `campaigns_ibfk_2` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for campaigns_staffs
-- ----------------------------
CREATE TABLE `campaigns_staffs` (
  `id` int(11) NOT NULL auto_increment,
  `campaign_id` int(11) default NULL,
  `staff_id` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `f_campaign_id` (`campaign_id`),
  KEY `staff_id` (`staff_id`),
  CONSTRAINT `campaigns_staffs_ibfk_2` FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`id`),
  CONSTRAINT `campaigns_staffs_ibfk_1` FOREIGN KEY (`campaign_id`) REFERENCES `campaigns` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for clients
-- ----------------------------
CREATE TABLE `clients` (
  `id` int(11) NOT NULL auto_increment,
  `client_number` varchar(255) default NULL,
  `password` varchar(255) default NULL,
  `company` varchar(255) default NULL,
  `email` varchar(255) default NULL,
  `telephone` varchar(255) default NULL,
  `address` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for directors
-- ----------------------------
CREATE TABLE `directors` (
  `id` int(11) NOT NULL auto_increment,
  `director_number` varchar(255) default NULL,
  `password` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  `telephone` varchar(255) default NULL,
  `e-mail` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for notes
-- ----------------------------
CREATE TABLE `notes` (
  `id` int(11) NOT NULL auto_increment,
  `target_director_id` int(11) default NULL,
  `source_director_id` int(11) default NULL,
  `staff_id` int(11) default NULL,
  `acknowledgement` int(11) default '0' COMMENT '0=no ack, 1=ack',
  PRIMARY KEY  (`id`),
  CONSTRAINT `notes_ibfk_1` FOREIGN KEY (`id`) REFERENCES `staffs` (`id`),
  CONSTRAINT `notes_ibfk_2` FOREIGN KEY (`id`) REFERENCES `directors` (`id`),
  CONSTRAINT `notes_ibfk_3` FOREIGN KEY (`id`) REFERENCES `directors` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for staffs
-- ----------------------------
CREATE TABLE `staffs` (
  `id` int(11) NOT NULL auto_increment,
  `stuff_number` varchar(255) default NULL,
  `password` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  `telephone` varchar(255) default NULL,
  `e-mail` varchar(255) default NULL,
  `director_id` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `f_s_d_id` (`director_id`),
  CONSTRAINT `f_s_d_id` FOREIGN KEY (`director_id`) REFERENCES `directors` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
