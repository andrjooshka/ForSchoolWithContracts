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
-- Table structure for table `event_types`
--

DROP TABLE IF EXISTS `event_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `price` int(11) NOT NULL,
  `title` varchar(255) CHARACTER SET cp1251 NOT NULL,
  `schoolMoney` int(11) NOT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=296 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_types`
--

LOCK TABLES `event_types` WRITE;
/*!40000 ALTER TABLE `event_types` DISABLE KEYS */;
INSERT INTO `event_types` VALUES (1,500,'Вокал инд: 1300',600,'\0'),(2,500,'Гитара инд: 1100',400,'\0'),(3,500,'Фортепиано инд: 1200',500,''),(4,500,'Сольфеджио инд: 1000',400,''),(5,500,'Барабаны инд: 1300',600,''),(6,500,'Электро  инд: 1100',500,''),(7,500,'Бас инд: 1100',400,''),(8,500,'Саксофон инд: 1300',600,''),(9,500,'Флейта инд: 1350',750,'\0'),(10,500,'Вокал пара: 900',500,''),(11,500,'Гитара пара: 800',400,''),(12,500,'Сольфеджио пара: 850',450,'\0'),(13,500,'Барабаны пара: 850',450,''),(14,500,'Электро  пара: 850',450,''),(15,500,'Бас пара: 800',400,''),(18,500,'Вокал груп: 750',375,''),(19,500,'Гитара груп: 700',350,''),(20,500,'Сольфеджио груп: 600',300,''),(21,500,'21',670,'\0'),(22,500,'22',400,''),(23,500,'23',500,''),(24,500,'24',400,''),(25,500,'25',400,''),(26,500,'26',250,''),(27,500,'27',300,'\0'),(28,500,'28',225,''),(29,500,'29',200,''),(30,500,'30',470,''),(31,500,'31',390,''),(32,500,'32',397,''),(33,500,'33',200,'\0'),(34,500,'34',500,'\0'),(35,500,'35',240,''),(36,500,'36',470,''),(37,500,'37',300,''),(38,500,'38',400,''),(39,500,'39',605,'\0'),(40,500,'40',700,''),(41,500,'41',470,'\0'),(42,500,'42',500,'\0'),(43,500,'43',470,''),(44,500,'44',370,''),(45,500,'45',570,''),(46,500,'46',670,'\0'),(47,500,'47',700,''),(48,500,'48',570,''),(49,500,'49',600,''),(50,500,'50',500,''),(51,500,'51',200,'\0'),(52,500,'52',550,''),(53,500,'53',450,''),(54,500,'54',425,'\0'),(55,500,'55',390,''),(56,500,'56',400,'\0'),(57,500,'57',365,''),(58,500,'58',350,''),(59,500,'59',315,''),(60,500,'60',470,''),(61,500,'61',370,''),(62,500,'62',470,''),(63,500,'63',470,''),(64,500,'64',500,''),(65,500,'65',470,''),(66,500,'66',290,''),(67,500,'67',570,''),(68,500,'68',300,''),(69,500,'69',405,''),(70,500,'70',0,'\0'),(71,500,'71',380,''),(72,500,'72',300,''),(73,500,'73',350,''),(74,500,'74',605,''),(75,500,'75',405,''),(76,500,'76',400,''),(77,500,'77',500,''),(78,500,'78',400,''),(79,500,'79',500,''),(80,500,'80',250,'\0'),(81,500,'81',0,''),(82,500,'82',396,''),(83,500,'83',282,''),(84,500,'84',0,'\0'),(86,500,'86',570,''),(87,500,'87',600,''),(89,500,'89',700,'\0'),(90,500,'90',0,''),(91,500,'91',580,''),(92,500,'92',580,''),(93,500,'93',0,''),(95,500,'95',325,''),(96,500,'96',200,''),(97,500,'97',605,''),(98,500,'98',380,''),(99,500,'99',405,'\0'),(101,500,'101',480,''),(102,500,'102',580,''),(103,500,'103',500,''),(104,500,'104',0,'\0'),(105,500,'105',800,''),(106,500,'106',770,''),(107,500,'107',600,''),(109,500,'109',0,''),(110,500,'110',450,'\0'),(111,500,'111',550,'\0'),(112,500,'112',750,'\0'),(113,500,'113',550,'\0'),(114,500,'114',750,'\0'),(115,500,'115',950,'\0'),(116,500,'116',485,''),(117,500,'117',1180,'\0'),(118,500,'118',950,'\0'),(119,500,'119',650,'\0'),(120,500,'120',700,'\0'),(121,500,'121',850,'\0'),(122,500,'122',550,''),(123,500,'123',685,''),(124,500,'124',600,'\0'),(125,500,'125',500,'\0'),(126,500,'126',650,'\0'),(127,500,'127',234,''),(128,500,'128',650,'\0'),(129,500,'129',1,''),(134,500,'134',625,''),(135,500,'135',400,''),(136,500,'136',570,''),(137,500,'137',650,''),(139,500,'139',1,''),(140,500,'140',675,'\0'),(141,500,'141',1450,'\0'),(142,500,'142',550,'\0'),(143,500,'143',625,'\0'),(144,500,'144',1005,''),(145,500,'145',325,''),(146,500,'146',0,'\0'),(147,500,'147',950,'\0'),(148,500,'148',700,''),(149,500,'149',0,''),(150,500,'150',1170,''),(151,500,'151',3510,''),(152,500,'152',1200,''),(153,500,'153',1370,''),(154,500,'154',4680,''),(155,500,'155',950,''),(156,500,'156',460,'\0'),(157,500,'157',2600,''),(158,500,'158',2420,''),(159,500,'159',3010,''),(160,500,'160',800,''),(161,500,'161',1125,''),(162,500,'162',375,'\0'),(163,500,'163',2276,'\0'),(164,500,'164',1150,'\0'),(165,500,'165',0,'\0'),(166,500,'166',225,'\0'),(167,500,'167',0,'\0'),(168,500,'168',0,''),(169,500,'169',0,''),(170,500,'170',0,'\0'),(171,500,'171',0,''),(172,500,'172',0,'\0'),(173,500,'173',0,''),(174,500,'174',500,''),(175,500,'175',440,'\0'),(176,500,'176',800,''),(177,500,'177',850,'\0'),(178,500,'178',700,'\0'),(179,500,'179',450,''),(180,500,'180',376,''),(181,500,'181',580,'\0'),(182,500,'182',370,''),(183,500,'183',650,'\0'),(184,500,'184',1000,'\0'),(185,500,'185',800,'\0'),(186,500,'186',1250,''),(187,500,'187',550,'\0'),(188,500,'188',0,'\0'),(189,500,'189',1776,'\0'),(190,500,'190',2000,''),(191,500,'191',1700,''),(192,500,'192',0,''),(193,500,'193',1100,''),(194,500,'194',660,''),(195,500,'195',650,''),(196,500,'196',4500,''),(197,500,'197',1350,''),(198,500,'198',410,''),(199,500,'199',4000,''),(200,500,'200',1150,''),(201,500,'201',1425,'\0'),(202,500,'202',750,''),(203,500,'203',2370,''),(204,500,'204',2740,''),(205,500,'205',750,''),(206,500,'206',150,''),(207,500,'207',-1630,''),(208,500,'208',1600,''),(209,500,'209',580,''),(210,500,'210',450,''),(211,500,'211',900,''),(212,500,'212',750,'\0'),(213,500,'213',1270,''),(214,500,'214',600,''),(215,500,'215',0,'\0'),(216,500,'216',2300,''),(217,500,'217',2540,''),(218,500,'218',325,''),(219,500,'219',1125,'\0'),(220,500,'220',750,'\0'),(221,500,'221',1000,''),(222,500,'222',5500,''),(223,500,'223',850,''),(224,500,'224',60,''),(225,500,'225',2250,''),(226,500,'226',460,''),(227,500,'227',840,''),(228,500,'228',1250,''),(229,500,'229',2140,''),(230,500,'230',2106,''),(231,500,'231',2106,''),(232,500,'232',7100,''),(233,500,'233',1900,''),(234,500,'234',440,'\0'),(235,500,'235',70,''),(236,500,'236',750,'\0'),(237,500,'237',575,'\0'),(238,500,'238',1870,''),(239,500,'239',300,''),(240,500,'240',1230,'\0'),(241,500,'241',2250,''),(242,500,'242',850,'\0'),(243,500,'243',1100,'\0'),(244,500,'244',850,'\0'),(245,500,'245',800,'\0'),(246,500,'246',800,'\0'),(247,500,'247',650,'\0'),(248,500,'248',1300,'\0'),(249,500,'249',1550,'\0'),(250,500,'250',1500,'\0'),(251,500,'251',1250,'\0'),(252,500,'252',1200,'\0'),(253,500,'253',1450,'\0'),(254,500,'254',1400,'\0'),(255,500,'255',775,'\0'),(256,500,'256',875,'\0'),(257,500,'257',1330,'\0'),(258,500,'258',1300,'\0'),(259,500,'259',1300,'\0'),(260,500,'260',700,'\0'),(261,500,'261',900,'\0'),(262,500,'262',1000,'\0'),(263,500,'263',740,'\0'),(264,500,'264',840,'\0'),(265,500,'265',1980,''),(266,500,'266',250,''),(267,500,'267',1155,''),(268,500,'268',7178,''),(269,500,'269',2106,''),(270,500,'270',4380,''),(271,500,'271',4640,''),(272,500,'272',7000,''),(273,500,'273',5640,''),(274,500,'274',6000,''),(275,500,'275',3000,''),(276,500,'276',9600,''),(277,500,'277',2466,''),(278,500,'278',2800,''),(279,500,'279',2106,''),(280,500,'280',3860,''),(281,500,'281',200,''),(282,500,'282',2980,''),(283,500,'283',5850,''),(284,500,'284',2400,''),(285,500,'285',15000,''),(286,500,'286',6250,''),(287,500,'287',2466,''),(288,500,'288',2500,''),(289,500,'289',9750,''),(290,500,'290',1450,''),(291,500,'291',2250,''),(292,500,'292',3510,''),(293,500,'293',6600,''),(294,500,'294',-1050,''),(295,500,'295',4200,'');
/*!40000 ALTER TABLE `event_types` ENABLE KEYS */;
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
