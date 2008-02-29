/*
MySQL Data Transfer
Source Host: localhost
Source Database: flight
Target Host: localhost
Target Database: flight
Date: 2/29/2008 1:12:25
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for book_order
-- ----------------------------
CREATE TABLE `book_order` (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(255) default NULL,
  `date` date default NULL,
  `back_date` date default NULL,
  `flight1_code` varchar(255) default NULL,
  `flight2_code` varchar(255) default '-1',
  `back_flight1_code` varchar(255) default '-1',
  `back_flight2_code` varchar(255) default '-1',
  PRIMARY KEY  (`id`),
  KEY `username` (`username`),
  KEY `fk_flight1` (`flight1_code`),
  KEY `fk_flight2` (`flight2_code`),
  KEY `fk_back_flight1` (`back_flight1_code`),
  KEY `fk_back_flight2` (`back_flight2_code`),
  CONSTRAINT `fk_flight1` FOREIGN KEY (`flight1_code`) REFERENCES `flight_info` (`flight_code`),
  CONSTRAINT `fk_flight2` FOREIGN KEY (`flight2_code`) REFERENCES `flight_info` (`flight_code`),
  CONSTRAINT `fk_back_flight1` FOREIGN KEY (`back_flight1_code`) REFERENCES `flight_info` (`flight_code`),
  CONSTRAINT `fk_back_flight2` FOREIGN KEY (`back_flight2_code`) REFERENCES `flight_info` (`flight_code`),
  CONSTRAINT `book_order_ibfk_1` FOREIGN KEY (`username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for flight_info
-- ----------------------------
CREATE TABLE `flight_info` (
  `flight_code` varchar(255) NOT NULL default '0',
  `departure_city` varchar(255) default NULL,
  `departure_time` time default NULL,
  `arrival_city` varchar(255) default NULL,
  `arrival_time` time default NULL,
  `price` int(11) default NULL,
  PRIMARY KEY  (`flight_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for timetable
-- ----------------------------
CREATE TABLE `timetable` (
  `id` int(11) NOT NULL default '0',
  `date` date default NULL,
  `flight_code` varchar(255) default NULL,
  `available` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `fk_flight_code` (`flight_code`),
  CONSTRAINT `fk_flight_code` FOREIGN KEY (`flight_code`) REFERENCES `flight_info` (`flight_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
CREATE TABLE `user` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) default NULL,
  PRIMARY KEY  (`username`),
  KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `flight_info` VALUES ('CA912', 'Stockholm', '05:50:00', 'Beijing', '14:50:00', '1124');
