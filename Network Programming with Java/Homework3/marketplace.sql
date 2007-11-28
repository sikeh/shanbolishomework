/*
MySQL Data Transfer
Source Host: localhost
Source Database: marketplace
Target Host: localhost
Target Database: marketplace
Date: 11/28/2007 15:02:57
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for account
-- ----------------------------
CREATE TABLE `account` (
  `id` int(11) NOT NULL auto_increment,
  `user_id` int(11) default NULL,
  `balance` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for bought_history
-- ----------------------------
CREATE TABLE `bought_history` (
  `id` int(11) NOT NULL auto_increment,
  `seller_name` int(11) default NULL,
  `item_name` int(11) default NULL,
  `amount` int(11) default NULL,
  `price` int(11) default NULL,
  `time` datetime default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for item
-- ----------------------------
CREATE TABLE `item` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `price` int(11) default NULL,
  `amount` int(11) default NULL,
  `seller_id` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sold_history
-- ----------------------------
CREATE TABLE `sold_history` (
  `id` int(11) NOT NULL auto_increment,
  `buyer_name` int(11) default NULL,
  `item_name` int(11) default NULL,
  `amount` int(11) default NULL,
  `price` int(11) default NULL,
  `time` datetime default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
CREATE TABLE `user` (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(255) default NULL,
  `password` varchar(40) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for wish
-- ----------------------------
CREATE TABLE `wish` (
  `id` int(11) NOT NULL auto_increment,
  `user_id` int(11) default NULL,
  `item_name` varchar(255) default NULL,
  `price` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `item` VALUES ('1', '12', '123', '0', '123');
INSERT INTO `item` VALUES ('2', '123', '123', '2', '4');

-- ----------------------------
-- Trigger structure for T
-- ----------------------------
DELIMITER ;;
CREATE TRIGGER `T` AFTER UPDATE ON `item` FOR EACH ROW BEGIN
IF NEW.amount = 0 THEN
DELETE FROM item where NEW.amount = 0;
END IF;
END;;
DELIMITER ;
