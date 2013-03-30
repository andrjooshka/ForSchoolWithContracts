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
-- Table structure for table `teacher_addition`
--

DROP TABLE IF EXISTS `teacher_addition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `teacher_addition` (
  `addition_id` int(11) NOT NULL AUTO_INCREMENT,
  `field_1` varchar(255) DEFAULT NULL,
  `field_2` varchar(255) DEFAULT NULL,
  `field_3` varchar(255) DEFAULT NULL,
  `field_4` varchar(255) DEFAULT NULL,
  `teacher_id` int(11) NOT NULL,
  `field_5` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`addition_id`),
  UNIQUE KEY `teacher_id` (`teacher_id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_addition`
--

LOCK TABLES `teacher_addition` WRITE;
/*!40000 ALTER TABLE `teacher_addition` DISABLE KEYS */;
INSERT INTO `teacher_addition` VALUES (1,'1','1','1','1',17,'1'),(2,'2','2','2','2',18,'2'),(3,'3','3','3','3',14,'3'),(4,'4','4','4','4',22,'4'),(5,'5','5','5','5',6,'5'),(6,'6','6','6','6',8,'6'),(7,'7','7','7','7',23,'7'),(8,'8','8','8','8',7,'8'),(9,'9','9','9','9',20,'9'),(10,'10','10','10','10',9,'10'),(11,'11','11','11','11',19,'11'),(12,'12','12','12','12',16,'12'),(13,'13','13','13','13',26,'13'),(14,'14','14','14','14',13,'14'),(15,'15','15','15','15',12,'15'),(16,'16','16','16','16',24,'16'),(17,'17','17','17','17',27,'17'),(18,'18','18','18','18',28,'18'),(19,'19','19','19','19',30,'19'),(20,'20','20','20','20',29,'20'),(21,'21','21','21','21',36,'21'),(22,'22','22','22','22',31,'22'),(23,'23','23','23','23',32,'23'),(24,'24','24','24','24',33,'24'),(25,'25','25','25','25',39,'25'),(26,'26','26','26','26',38,'26'),(27,'27','27','27','27',43,'27'),(28,'28','28','28','28',42,'28'),(29,'29','29','29','29',44,'29'),(30,'30','30','30','30',45,'30'),(31,'31','31','31','31',46,'31'),(32,'32','32','32','32',47,'32'),(33,'33','33','33','33',49,'33'),(34,'34','34','34','34',51,'34'),(35,'35','35','35','35',52,'35'),(36,'36','36','36','36',48,'36'),(37,'37','37','37','37',50,'37'),(38,'38','38','38','38',53,'38'),(39,'39','39','39','39',54,'39'),(40,'40','40','40','40',55,'40'),(41,'41','41','41','41',56,'41');
/*!40000 ALTER TABLE `teacher_addition` ENABLE KEYS */;
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
