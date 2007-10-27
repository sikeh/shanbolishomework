# MySQL-Front 3.2  (Build 14.8)

/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='SYSTEM' */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40103 SET SQL_NOTES='ON' */;


# Host: localhost    Database: iad
# ------------------------------------------------------
# Server version 5.0.45-community-nt

#
# Table structure for table campaigns
#

DROP TABLE IF EXISTS `campaigns`;
CREATE TABLE `campaigns` (
  `id` int(11) NOT NULL auto_increment,
  `start_date` datetime default NULL,
  `end_date` datetime default NULL,
  `client_id` int(11) default NULL,
  `director_id` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `client_id` (`client_id`),
  KEY `director_id` (`director_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

#
# Dumping data for table campaigns
#


#
# Table structure for table campaigns_staffs
#

DROP TABLE IF EXISTS `campaigns_staffs`;
CREATE TABLE `campaigns_staffs` (
  `id` int(11) NOT NULL auto_increment,
  `campaign_id` int(11) default NULL,
  `staff_id` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `campaign_id` (`campaign_id`),
  KEY `staff_id` (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Dumping data for table campaigns_staffs
#


#
# Table structure for table clients
#

DROP TABLE IF EXISTS `clients`;
CREATE TABLE `clients` (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(11) default NULL,
  `password` varchar(11) default NULL,
  `name` varchar(50) default NULL,
  `company` varchar(80) default NULL,
  `address` varchar(255) default NULL,
  `telephone` varchar(15) default NULL,
  `email` varchar(25) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Dumping data for table clients
#


#
# Table structure for table directors
#

DROP TABLE IF EXISTS `directors`;
CREATE TABLE `directors` (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(11) default NULL,
  `password` varchar(11) default NULL,
  `name` varchar(50) default NULL,
  `telephone` varchar(15) default NULL,
  `email` varchar(25) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Dumping data for table directors
#


#
# Table structure for table notes
#

DROP TABLE IF EXISTS `notes`;
CREATE TABLE `notes` (
  `id` int(11) NOT NULL auto_increment,
  `source_director_id` int(11) default NULL,
  `target_director_id` int(11) default NULL,
  `staff_id` int(11) default NULL,
  `acknowledge` tinyint(1) default '0',
  PRIMARY KEY  (`id`),
  KEY `source_director_id` (`source_director_id`),
  KEY `target_director_id` (`target_director_id`),
  KEY `staff_id` (`staff_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

#
# Dumping data for table notes
#


#
# Table structure for table staffs
#

DROP TABLE IF EXISTS `staffs`;
CREATE TABLE `staffs` (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(11) default NULL,
  `password` varchar(11) default NULL,
  `name` varchar(50) default NULL,
  `telephone` varchar(15) default NULL,
  `email` varchar(25) default NULL,
  `director_id` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `director_id` (`director_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Dumping data for table staffs
#


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
