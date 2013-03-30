CREATE DATABASE  IF NOT EXISTS `music` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `music`;
-- MySQL dump 10.13  Distrib 5.5.29, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: music
-- ------------------------------------------------------
-- Server version	5.5.29-0ubuntu0.12.10.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `event_type_additions`
--

DROP TABLE IF EXISTS `event_type_additions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_type_additions` (
  `addition_id` int(11) NOT NULL AUTO_INCREMENT,
  `additionClause` int(11) NOT NULL,
  `additionCode` int(11) NOT NULL,
  `additionValue` int(11) NOT NULL,
  `event_type_id` int(11) NOT NULL,
  PRIMARY KEY (`addition_id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_type_additions`
--

LOCK TABLES `event_type_additions` WRITE;
/*!40000 ALTER TABLE `event_type_additions` DISABLE KEYS */;
INSERT INTO `event_type_additions` VALUES (1,0,0,100,111),(2,0,0,100,65),(3,0,0,100,122),(4,0,0,100,45),(5,0,0,100,112),(6,0,0,0,136),(7,0,0,0,137),(8,0,0,50,126),(9,0,0,50,10),(10,0,0,60,19),(11,0,0,50,125),(12,0,0,50,56),(13,0,0,50,52),(14,0,0,60,124),(15,0,0,100,41),(16,0,0,100,91),(17,0,0,0,23),(18,0,0,100,46),(19,0,0,100,92),(20,0,0,100,60),(21,0,0,100,77),(22,0,0,100,98),(23,0,0,350,148),(24,0,0,75,140),(25,0,0,75,54),(26,0,0,75,143),(27,0,0,100,142),(28,0,0,100,174),(29,0,0,50,179),(30,0,0,50,11),(31,0,0,50,53),(32,0,0,100,172),(33,0,0,100,202),(34,0,0,100,114),(35,0,0,50,220),(36,0,0,150,219),(37,0,0,100,42),(38,0,0,100,79),(39,0,0,150,161),(40,0,0,300,242),(41,0,0,300,247);
/*!40000 ALTER TABLE `event_type_additions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-03-30 21:37:00
