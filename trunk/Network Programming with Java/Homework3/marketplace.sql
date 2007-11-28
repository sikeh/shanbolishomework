/*
MySQL Data Transfer
Source Host: localhost
Source Database: marketplace
Target Host: localhost
Target Database: marketplace
Date: 2007/11/28 16:58:51
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for account
-- ----------------------------
CREATE TABLE `account` (
  `id` int(11) NOT NULL auto_increment,
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
  `bought_time` datetime default NULL,
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
  `placed_time` datetime default NULL,
  PRIMARY KEY  (`id`),
  KEY `fk_item_seller_id` (`seller_id`),
  CONSTRAINT `fk_item_seller_id` FOREIGN KEY (`seller_id`) REFERENCES `market_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for market_user
-- ----------------------------
CREATE TABLE `market_user` (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(255) default NULL,
  `password` varchar(40) default NULL,
  `account_id` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `fk_market_user_account_id` (`account_id`),
  CONSTRAINT `fk_market_user_account_id` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sold_history
-- ----------------------------
CREATE TABLE `sold_history` (
  `id` int(11) NOT NULL auto_increment,
  `buyer_name` int(11) default NULL,
  `item_name` int(11) default NULL,
  `amount` int(11) default NULL,
  `price` int(11) default NULL,
  `sold_time` datetime default NULL,
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
  PRIMARY KEY  (`id`),
  KEY `fk_wish_user_id` (`user_id`),
  CONSTRAINT `fk_wish_user_id` FOREIGN KEY (`user_id`) REFERENCES `market_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
