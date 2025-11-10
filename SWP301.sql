-- MySQL dump 10.13  Distrib 9.3.0, for macos15 (x86_64)
--
-- Host: localhost    Database: SWP301
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `booking_tables`
--

DROP TABLE IF EXISTS `booking_tables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_tables` (
                                  `booking_id` bigint NOT NULL,
                                  `table_id` bigint NOT NULL,
                                  KEY `FKclovrxgkr7s3009uqjeaf1374` (`table_id`),
                                  KEY `FK54f80t2di1cppyelssnw4mqvn` (`booking_id`),
                                  CONSTRAINT `FK54f80t2di1cppyelssnw4mqvn` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
                                  CONSTRAINT `FKclovrxgkr7s3009uqjeaf1374` FOREIGN KEY (`table_id`) REFERENCES `tables` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_tables`
--

LOCK TABLES `booking_tables` WRITE;
/*!40000 ALTER TABLE `booking_tables` DISABLE KEYS */;
INSERT INTO `booking_tables` VALUES (6,2),(5,3),(7,2),(2,5),(2,6),(4,1),(9,8),(9,9),(10,2),(10,3),(11,4),(11,5),(12,1),(13,2),(14,10);
/*!40000 ALTER TABLE `booking_tables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `created_by` bigint DEFAULT NULL,
                            `updated_by` bigint DEFAULT NULL,
                            `deleted_by` bigint DEFAULT NULL,
                            `created_at` datetime(6) DEFAULT NULL,
                            `updated_at` datetime(6) DEFAULT NULL,
                            `deleted_at` datetime(6) DEFAULT NULL,
                            `is_deleted` tinyint(1) DEFAULT '0',
                            `is_activated` tinyint(1) DEFAULT '1',
                            `customer_name` varchar(100) NOT NULL,
                            `customer_phone` varchar(15) NOT NULL,
                            `booking_time` datetime NOT NULL,
                            `num_guests` int NOT NULL,
                            `notes` text,
                            `status` varchar(20) NOT NULL,
                            `table_id` bigint DEFAULT NULL,
                            `staff_id` bigint DEFAULT NULL,
                            `customer_user_id` bigint DEFAULT NULL,
                            `staff_user_id` bigint DEFAULT NULL,
                            `customer_email` varchar(100) NOT NULL,
                            PRIMARY KEY (`id`),
                            KEY `fk_booking_table` (`table_id`),
                            KEY `fk_booking_staff` (`staff_id`),
                            KEY `FKiyid09v50r0l4ulr7xw9vn5i6` (`customer_user_id`),
                            KEY `FKd4s6p98ua0985yi2tkdj26lc5` (`staff_user_id`),
                            CONSTRAINT `fk_booking_staff` FOREIGN KEY (`staff_id`) REFERENCES `users` (`id`),
                            CONSTRAINT `fk_booking_table` FOREIGN KEY (`table_id`) REFERENCES `tables` (`id`),
                            CONSTRAINT `FKd4s6p98ua0985yi2tkdj26lc5` FOREIGN KEY (`staff_user_id`) REFERENCES `users` (`id`),
                            CONSTRAINT `FKiyid09v50r0l4ulr7xw9vn5i6` FOREIGN KEY (`customer_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (2,NULL,NULL,NULL,'2025-11-04 19:29:42.616394','2025-11-05 10:14:34.924287',NULL,0,1,'Nguyễn Văn An','0912345678','2025-11-05 19:30:00',8,'Khách muốn bàn gần cửa sổ','confirmed',NULL,NULL,NULL,NULL,'an.nguyen@example.com'),(4,NULL,NULL,NULL,'2025-11-04 23:48:35.587589','2025-11-05 10:38:47.184649',NULL,0,1,'M Cuong','0777777777','2025-11-07 16:48:00',2,'cay guong','confirmed',NULL,NULL,NULL,NULL,'mcuong@cayguong.gay'),(5,NULL,NULL,NULL,'2025-11-05 00:45:49.001939','2025-11-05 10:08:47.918717',NULL,0,1,'mgah','09123913321','2025-11-06 17:45:00',6,'none','completed',NULL,NULL,NULL,NULL,'cayguong@gmail.com'),(6,NULL,NULL,NULL,'2025-11-05 00:55:56.228335','2025-11-05 00:56:50.598277',NULL,0,1,'minh','0999999999','2025-11-05 17:55:00',4,'none','completed',NULL,NULL,NULL,NULL,'minh@gm.com'),(7,NULL,NULL,NULL,'2025-11-05 09:03:59.504281','2025-11-05 10:08:52.247243',NULL,0,1,'Sonia Alston','0111111111','2026-02-19 18:20:00',4,'Quos nesciunt porro','completed',NULL,NULL,NULL,NULL,'remowi@mailinator.com'),(9,NULL,NULL,NULL,'2025-11-06 21:48:39.925061','2025-11-06 22:15:03.215991',NULL,0,1,'Dương Hồng Minh','0773304009','2025-11-07 16:48:00',8,'gay','confirmed',NULL,NULL,NULL,NULL,'duonghongminh6bqxk@gmail.com'),(10,NULL,NULL,NULL,'2025-11-06 22:14:31.173945','2025-11-06 22:23:54.011570',NULL,0,1,'Dương Hồng Minh','0773304009','2025-11-07 13:14:00',10,'none','confirmed',NULL,NULL,NULL,NULL,'duonghongminh11a1@gmail.com'),(11,NULL,NULL,NULL,'2025-11-06 22:42:54.809392','2025-11-06 22:43:04.618377',NULL,0,1,'mcuong','0999999999','2025-11-07 15:41:00',8,'cayguong','confirmed',NULL,NULL,NULL,NULL,'tuphucnguyen20051@gmail.com'),(12,NULL,NULL,NULL,'2025-11-07 01:05:32.605325','2025-11-07 01:06:13.714829',NULL,0,1,'Minh','0773304009','2025-11-09 14:00:00',4,'','confirmed',NULL,NULL,NULL,NULL,'guest@example.com'),(13,NULL,NULL,NULL,'2025-11-07 01:19:44.143195','2025-11-07 08:09:54.583443',NULL,0,1,'Minh','0773304009','2025-11-09 14:00:00',4,'','confirmed',NULL,NULL,NULL,NULL,'guest@example.com'),(14,NULL,NULL,NULL,'2025-11-07 08:09:25.247389','2025-11-07 08:10:09.319066',NULL,0,1,'Dương Hồng Minh','0773304009','2025-11-07 13:08:00',6,'note','confirmed',NULL,NULL,NULL,NULL,'duonghongminh6bqxk@gmail.com');
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `created_by` bigint DEFAULT NULL,
                              `updated_by` bigint DEFAULT NULL,
                              `deleted_by` bigint DEFAULT NULL,
                              `created_at` datetime(6) DEFAULT NULL,
                              `updated_at` datetime(6) DEFAULT NULL,
                              `deleted_at` datetime(6) DEFAULT NULL,
                              `is_deleted` tinyint(1) DEFAULT '0',
                              `is_activated` tinyint(1) DEFAULT '1',
                              `name` varchar(100) NOT NULL,
                              `description` text,
                              `image_url` varchar(255) DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,NULL,NULL,NULL,NULL,'2025-10-01 11:02:08.790976',NULL,0,1,'Appetizer','Starter dishes to begin a meal','#4e7a1e'),(2,NULL,NULL,NULL,NULL,'2025-09-30 21:39:10.688725',NULL,0,1,'Main Course','Main dishes for a full meal','#73be26'),(3,NULL,NULL,NULL,NULL,'2025-09-30 21:39:25.037864',NULL,0,1,'Dessert','Sweet dishes to finish the meal','#550899'),(4,NULL,NULL,NULL,NULL,NULL,NULL,0,1,'Beverage','Drinks including soft drinks and juices','https://example.com/images/beverage.jpg'),(5,NULL,NULL,NULL,NULL,NULL,NULL,0,1,'Special','Special dishes of the day or chef\'s choice','https://example.com/images/special.jpg'),(6,NULL,NULL,NULL,'2025-09-30 21:14:25.119277','2025-10-01 07:31:03.415079',NULL,0,0,'Gay','Food for gay','#ee3fe4'),(8,NULL,NULL,NULL,'2025-10-01 08:47:22.944917','2025-10-01 08:47:22.944937',NULL,0,0,'An chay','day la do an chay','#77a541');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `is_activated` tinyint(1) DEFAULT '1',
                             `created_at` datetime(6) DEFAULT NULL,
                             `created_by` bigint DEFAULT NULL,
                             `is_deleted` tinyint(1) DEFAULT '0',
                             `deleted_at` datetime(6) DEFAULT NULL,
                             `deleted_by` bigint DEFAULT NULL,
                             `updated_at` datetime(6) DEFAULT NULL,
                             `updated_by` bigint DEFAULT NULL,
                             `address` varchar(255) DEFAULT NULL,
                             `date_of_birth` date DEFAULT NULL,
                             `email` varchar(255) DEFAULT NULL,
                             `full_name` varchar(100) DEFAULT NULL,
                             `otp_code` varchar(10) DEFAULT NULL,
                             `otp_expiry_time` datetime(6) DEFAULT NULL,
                             `phone_number` varchar(15) DEFAULT NULL,
                             `user_id` bigint DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `UKeuat1oase6eqv195jvb71a93s` (`user_id`),
                             UNIQUE KEY `UKrfbvkrffamfql7cjmen8v976v` (`email`),
                             UNIQUE KEY `UK6v6x92wb400iwh6unf5rwiim4` (`phone_number`),
                             CONSTRAINT `FKrh1g1a20omjmn6kurd35o3eit` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (10,1,'2025-11-10 18:49:07.317863',NULL,0,NULL,NULL,'2025-11-10 18:49:21.557650',NULL,'asda',NULL,'duonghongminh6bqxk@gmail.com','M CUong',NULL,NULL,'0123456789',5);
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `floor_element`
--

DROP TABLE IF EXISTS `floor_element`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `floor_element` (
                                 `id` varchar(255) NOT NULL,
                                 `color` varchar(255) DEFAULT NULL,
                                 `height` double NOT NULL,
                                 `label` varchar(255) DEFAULT NULL,
                                 `rotation` double NOT NULL,
                                 `type` varchar(255) NOT NULL,
                                 `width` double NOT NULL,
                                 `x` double NOT NULL,
                                 `y` double NOT NULL,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `floor_element`
--

LOCK TABLES `floor_element` WRITE;
/*!40000 ALTER TABLE `floor_element` DISABLE KEYS */;
/*!40000 ALTER TABLE `floor_element` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `floor_elements`
--

DROP TABLE IF EXISTS `floor_elements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `floor_elements` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `x` double NOT NULL,
                                  `y` double NOT NULL,
                                  `width` double NOT NULL,
                                  `height` double NOT NULL,
                                  `rotation` double DEFAULT '0',
                                  `color` varchar(20) DEFAULT NULL,
                                  `type` varchar(50) NOT NULL,
                                  `label` varchar(100) DEFAULT NULL,
                                  `created_by` varchar(255) DEFAULT NULL,
                                  `updated_by` varchar(255) DEFAULT NULL,
                                  `deleted_by` varchar(255) DEFAULT NULL,
                                  `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
                                  `updated_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                  `deleted_at` datetime(6) DEFAULT NULL,
                                  `is_deleted` tinyint(1) DEFAULT '0',
                                  `is_activated` tinyint(1) DEFAULT '1',
                                  `location_id` bigint NOT NULL,
                                  `table_id` bigint DEFAULT NULL,
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `UKlrmfvx7o09hxxhsbceubbjxf` (`table_id`),
                                  KEY `FKtafk2hgu6kmiujp7o9omt2j8i` (`location_id`),
                                  CONSTRAINT `FKmcp0gkiqethv84ftufmamwjus` FOREIGN KEY (`table_id`) REFERENCES `tables` (`id`),
                                  CONSTRAINT `FKtafk2hgu6kmiujp7o9omt2j8i` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `floor_elements`
--

LOCK TABLES `floor_elements` WRITE;
/*!40000 ALTER TABLE `floor_elements` DISABLE KEYS */;
INSERT INTO `floor_elements` VALUES (46,258,157,100,100,0,'#3B82F6','window','window 1',NULL,NULL,NULL,'2025-11-03 21:10:11.811564','2025-11-03 21:10:11.811584',NULL,0,1,2,NULL),(47,258,157,100,100,0,'#3B82F6','window','window 1',NULL,NULL,NULL,'2025-11-03 21:11:24.008314','2025-11-03 21:11:24.008332',NULL,0,1,2,NULL),(53,1129,349,260,450,0,'#a66e6e','balcony','Balcony',NULL,NULL,NULL,'2025-11-03 22:15:03.147422','2025-11-04 08:00:59.685283',NULL,0,1,1,NULL),(54,118,586,145,80,0,'#b99d5f','door','Door',NULL,NULL,NULL,'2025-11-03 22:15:06.234800','2025-11-04 07:58:06.596557',NULL,0,1,1,NULL),(65,994,214,65,175,0,'#bdcce5','window','window 1',NULL,NULL,NULL,'2025-11-03 22:59:32.674233','2025-11-04 07:56:37.944253',NULL,0,1,1,NULL),(66,148,199,80,80,0,'#8B5CF6','table','S1',NULL,NULL,NULL,'2025-11-03 23:20:40.175754','2025-11-05 10:33:40.259192',NULL,0,1,1,1),(67,505,340,1000,495,0,'#e9dddd','other','Ground',NULL,NULL,NULL,'2025-11-04 07:46:02.807353','2025-11-05 10:31:38.728029',NULL,0,1,1,NULL),(70,994,426,65,175,0,'#c1cde1','window','window 2',NULL,NULL,NULL,'2025-11-04 07:56:46.274580','2025-11-04 07:57:10.718354',NULL,0,1,1,NULL),(74,366,200,80,80,0,'#8B5CF6','table','S2',NULL,NULL,NULL,'2025-11-05 10:31:41.178069','2025-11-05 10:33:48.409474',NULL,0,1,1,2),(75,598,195,80,80,0,'#8B5CF6','table','S3',NULL,NULL,NULL,'2025-11-05 10:32:00.850139','2025-11-05 10:33:53.321693',NULL,0,1,1,3),(76,841,196,80,80,0,'#8B5CF6','table','S4',NULL,NULL,NULL,'2025-11-05 10:32:08.711901','2025-11-05 10:33:58.331861',NULL,0,1,1,4),(77,456,514,140,80,0,'#8B5CF6','table','S6',NULL,NULL,NULL,'2025-11-05 10:32:20.537062','2025-11-05 10:34:45.322373',NULL,0,1,1,10),(78,762,516,140,80,0,'#8B5CF6','table','S5',NULL,NULL,NULL,'2025-11-05 10:32:21.614444','2025-11-05 10:34:05.088981',NULL,0,1,1,5),(80,258,80,140,60,0,'#c5d4ec','window','window 3',NULL,NULL,NULL,'2025-11-05 10:35:22.329069','2025-11-05 10:36:37.827040',NULL,0,1,1,NULL),(81,693,76,140,60,0,'#bccce6','window','window 4',NULL,NULL,NULL,'2025-11-05 10:35:47.797444','2025-11-05 10:36:29.716275',NULL,0,1,1,NULL),(84,258,157,100,100,0,'#3B82F6','window','window 1',NULL,NULL,NULL,'2025-11-10 18:58:24.241959','2025-11-10 18:58:24.241973',NULL,0,1,2,NULL),(85,258,157,100,100,0,'#3B82F6','window','window 1',NULL,NULL,NULL,'2025-11-10 18:58:24.241972','2025-11-10 18:58:24.241982',NULL,0,1,2,NULL);
/*!40000 ALTER TABLE `floor_elements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `location` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `is_activated` tinyint(1) DEFAULT '1',
                            `created_at` datetime(6) DEFAULT NULL,
                            `created_by` bigint DEFAULT NULL,
                            `is_deleted` tinyint(1) DEFAULT '0',
                            `deleted_at` datetime(6) DEFAULT NULL,
                            `deleted_by` bigint DEFAULT NULL,
                            `updated_at` datetime(6) DEFAULT NULL,
                            `updated_by` bigint DEFAULT NULL,
                            `description` varchar(255) DEFAULT NULL,
                            `name` varchar(100) NOT NULL,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `UKsahixf1v7f7xns19cbg12d946` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location`
--

LOCK TABLES `location` WRITE;
/*!40000 ALTER TABLE `location` DISABLE KEYS */;
INSERT INTO `location` VALUES (1,1,'2025-11-02 11:15:20.116745',NULL,0,NULL,NULL,'2025-11-02 11:15:37.207241',NULL,'Level 2','Sorrento'),(2,1,'2025-11-02 11:15:25.831724',NULL,0,NULL,NULL,'2025-11-02 11:15:25.831744',NULL,'Level 1','Verona'),(3,1,'2025-11-02 11:15:46.148177',NULL,0,NULL,NULL,'2025-11-02 11:15:46.148190',NULL,'Level 3','Roma'),(4,1,'2025-11-02 11:15:55.377716',NULL,0,NULL,NULL,'2025-11-02 11:15:55.377734',NULL,'Level 4','Terrace');
/*!40000 ALTER TABLE `location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu_items`
--

DROP TABLE IF EXISTS `menu_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu_items` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `created_by` bigint DEFAULT NULL,
                              `updated_by` bigint DEFAULT NULL,
                              `deleted_by` bigint DEFAULT NULL,
                              `created_at` datetime(6) DEFAULT NULL,
                              `updated_at` datetime(6) DEFAULT NULL,
                              `deleted_at` datetime(6) DEFAULT NULL,
                              `is_deleted` tinyint(1) DEFAULT '0',
                              `is_activated` tinyint(1) DEFAULT '1',
                              `name` varchar(100) NOT NULL,
                              `description` text,
                              `image_url` varchar(255) DEFAULT NULL,
                              `price` decimal(10,2) NOT NULL,
                              `status` varchar(20) NOT NULL,
                              `category_id` bigint NOT NULL,
                              `availability` enum('AVAILABLE','UNAVAILABLE') DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              KEY `fk_menu_category` (`category_id`),
                              CONSTRAINT `fk_menu_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu_items`
--

LOCK TABLES `menu_items` WRITE;
/*!40000 ALTER TABLE `menu_items` DISABLE KEYS */;
INSERT INTO `menu_items` VALUES (10,NULL,NULL,NULL,NULL,'2025-11-06 22:44:39.152226',NULL,0,0,'aaaaa','aaaaasadsadasdasd','/storage/1759246110325-nuochuaminhbel.jpeg',123.00,'available',1,NULL),(14,NULL,NULL,NULL,'2025-10-01 07:30:26.747547','2025-10-01 07:30:26.747559',NULL,0,0,'Gay Pork','This food were created by Tuan and Nguyen , the two gays ','/storage/1759278624818-nuochuaminhbel.jpeg',0.00,'available',6,NULL),(15,NULL,NULL,NULL,'2025-11-06 22:48:35.335591','2025-11-06 22:48:35.335604',NULL,0,1,'ca ran','ca ran','/storage/1762444113737-Biểu đồ không có tiêu đề.drawio (4).png',1.00,'unavailable',4,NULL);
/*!40000 ALTER TABLE `menu_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_details`
--

DROP TABLE IF EXISTS `order_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_details` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `created_by` bigint DEFAULT NULL,
                                 `updated_by` bigint DEFAULT NULL,
                                 `deleted_by` bigint DEFAULT NULL,
                                 `created_at` datetime(6) DEFAULT NULL,
                                 `updated_at` datetime(6) DEFAULT NULL,
                                 `deleted_at` datetime(6) DEFAULT NULL,
                                 `is_deleted` tinyint(1) DEFAULT '0',
                                 `is_activated` tinyint(1) DEFAULT '1',
                                 `order_id` bigint NOT NULL,
                                 `menu_item_id` bigint NOT NULL,
                                 `quantity` int NOT NULL DEFAULT '1',
                                 `price_at_order` decimal(10,2) NOT NULL,
                                 `status` varchar(255) NOT NULL,
                                 `notes` text,
                                 PRIMARY KEY (`id`),
                                 KEY `fk_orderdetail_order` (`order_id`),
                                 KEY `fk_orderdetail_menu` (`menu_item_id`),
                                 CONSTRAINT `fk_orderdetail_menu` FOREIGN KEY (`menu_item_id`) REFERENCES `menu_items` (`id`),
                                 CONSTRAINT `fk_orderdetail_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_details`
--

LOCK TABLES `order_details` WRITE;
/*!40000 ALTER TABLE `order_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `created_by` bigint DEFAULT NULL,
                          `updated_by` bigint DEFAULT NULL,
                          `deleted_by` bigint DEFAULT NULL,
                          `created_at` datetime(6) DEFAULT NULL,
                          `updated_at` datetime(6) DEFAULT NULL,
                          `deleted_at` datetime(6) DEFAULT NULL,
                          `is_deleted` tinyint(1) DEFAULT '0',
                          `is_activated` tinyint(1) DEFAULT '1',
                          `table_id` bigint NOT NULL,
                          `staff_id` bigint DEFAULT NULL,
                          `total_amount` decimal(12,2) NOT NULL,
                          `status` varchar(20) NOT NULL,
                          `notes` text,
                          `completed_at` datetime(6) DEFAULT NULL,
                          `promotion_id` bigint DEFAULT NULL,
                          `customer_user_id` bigint DEFAULT NULL,
                          `staff_user_id` bigint DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          KEY `fk_order_table` (`table_id`),
                          KEY `fk_order_staff` (`staff_id`),
                          KEY `fk_order_promotion` (`promotion_id`),
                          KEY `FKnr2jtai5a4jbute3j4rh49ggi` (`customer_user_id`),
                          KEY `FKmk1gxtko6ls5pvl53e7a2m82i` (`staff_user_id`),
                          CONSTRAINT `fk_order_promotion` FOREIGN KEY (`promotion_id`) REFERENCES `promotions` (`id`),
                          CONSTRAINT `fk_order_staff` FOREIGN KEY (`staff_id`) REFERENCES `users` (`id`),
                          CONSTRAINT `fk_order_table` FOREIGN KEY (`table_id`) REFERENCES `tables` (`id`),
                          CONSTRAINT `FKmk1gxtko6ls5pvl53e7a2m82i` FOREIGN KEY (`staff_user_id`) REFERENCES `users` (`id`),
                          CONSTRAINT `FKnr2jtai5a4jbute3j4rh49ggi` FOREIGN KEY (`customer_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `created_by` bigint DEFAULT NULL,
                               `updated_by` bigint DEFAULT NULL,
                               `deleted_by` bigint DEFAULT NULL,
                               `created_at` datetime(6) DEFAULT NULL,
                               `updated_at` datetime(6) DEFAULT NULL,
                               `deleted_at` datetime(6) DEFAULT NULL,
                               `is_deleted` tinyint(1) DEFAULT '0',
                               `is_activated` tinyint(1) DEFAULT '1',
                               `name` varchar(50) DEFAULT NULL,
                               `description` varchar(255) DEFAULT NULL,
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promotions`
--

DROP TABLE IF EXISTS `promotions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promotions` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `created_by` bigint DEFAULT NULL,
                              `updated_by` bigint DEFAULT NULL,
                              `deleted_by` bigint DEFAULT NULL,
                              `created_at` datetime(6) DEFAULT NULL,
                              `updated_at` datetime(6) DEFAULT NULL,
                              `deleted_at` datetime(6) DEFAULT NULL,
                              `is_deleted` tinyint(1) DEFAULT '0',
                              `is_activated` tinyint(1) DEFAULT '1',
                              `name` varchar(100) NOT NULL,
                              `code` varchar(50) DEFAULT NULL,
                              `description` text,
                              `promotion_type` varchar(25) NOT NULL,
                              `value` decimal(10,2) NOT NULL,
                              `min_spend` decimal(12,2) DEFAULT NULL,
                              `start_date` datetime NOT NULL,
                              `end_date` datetime NOT NULL,
                              `usage_limit` int DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promotions`
--

LOCK TABLES `promotions` WRITE;
/*!40000 ALTER TABLE `promotions` DISABLE KEYS */;
/*!40000 ALTER TABLE `promotions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `created_by` bigint DEFAULT NULL,
                           `updated_by` bigint DEFAULT NULL,
                           `deleted_by` bigint DEFAULT NULL,
                           `created_at` datetime(6) DEFAULT NULL,
                           `updated_at` datetime(6) DEFAULT NULL,
                           `deleted_at` datetime(6) DEFAULT NULL,
                           `is_deleted` tinyint(1) DEFAULT '0',
                           `is_activated` tinyint(1) DEFAULT '1',
                           `order_id` bigint NOT NULL,
                           `rating_score` int NOT NULL,
                           `comment` text,
                           `customer_user_id` bigint NOT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `order_id` (`order_id`),
                           KEY `FKecvyhigqvrf2vaag56ug4hfoo` (`customer_user_id`),
                           CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
                           CONSTRAINT `FKecvyhigqvrf2vaag56ug4hfoo` FOREIGN KEY (`customer_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permissions`
--

DROP TABLE IF EXISTS `role_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permissions` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `created_by` bigint DEFAULT NULL,
                                    `updated_by` bigint DEFAULT NULL,
                                    `deleted_by` bigint DEFAULT NULL,
                                    `created_at` datetime(6) DEFAULT NULL,
                                    `updated_at` datetime(6) DEFAULT NULL,
                                    `deleted_at` datetime(6) DEFAULT NULL,
                                    `is_deleted` tinyint(1) DEFAULT '0',
                                    `is_activated` tinyint(1) DEFAULT '1',
                                    `role_id` bigint NOT NULL,
                                    `permission_id` bigint NOT NULL,
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `role_permissions_unique` (`role_id`,`permission_id`),
                                    UNIQUE KEY `role_permissions_role_id_permission_id_unique` (`role_id`,`permission_id`),
                                    KEY `fk_rolepermission_permission` (`permission_id`),
                                    CONSTRAINT `fk_rolepermission_permission` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE,
                                    CONSTRAINT `fk_rolepermission_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permissions`
--

LOCK TABLES `role_permissions` WRITE;
/*!40000 ALTER TABLE `role_permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `created_by` bigint DEFAULT NULL,
                         `updated_by` bigint DEFAULT NULL,
                         `deleted_by` bigint DEFAULT NULL,
                         `created_at` datetime(6) DEFAULT NULL,
                         `updated_at` datetime(6) DEFAULT NULL,
                         `deleted_at` datetime(6) DEFAULT NULL,
                         `is_deleted` tinyint(1) DEFAULT '0',
                         `is_activated` tinyint(1) DEFAULT '1',
                         `name` varchar(255) DEFAULT NULL,
                         `description` varchar(255) DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,NULL,NULL,NULL,NULL,NULL,NULL,0,1,'ADMIN','Quản trị hệ thống, có toàn quyền'),(2,NULL,NULL,NULL,NULL,NULL,NULL,0,1,'WAITSTAFF','Nhân viên phục vụ bàn, nhận order từ khách'),(3,NULL,NULL,NULL,NULL,NULL,NULL,0,1,'KITCHEN_STAFF','Nhân viên bếp, chế biến món ăn'),(4,NULL,NULL,NULL,NULL,NULL,NULL,0,1,'CASHIER','Thu ngân, xử lý thanh toán'),(5,NULL,NULL,NULL,NULL,NULL,NULL,0,1,'CUSTOMER','Khách hàng');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `staff`
--

DROP TABLE IF EXISTS `staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `is_activated` tinyint(1) DEFAULT '1',
                         `created_at` datetime(6) DEFAULT NULL,
                         `created_by` bigint DEFAULT NULL,
                         `is_deleted` tinyint(1) DEFAULT '0',
                         `deleted_at` datetime(6) DEFAULT NULL,
                         `deleted_by` bigint DEFAULT NULL,
                         `updated_at` datetime(6) DEFAULT NULL,
                         `updated_by` bigint DEFAULT NULL,
                         `email` varchar(255) DEFAULT NULL,
                         `full_name` varchar(100) NOT NULL,
                         `phone_number` varchar(15) DEFAULT NULL,
                         `store_id` bigint NOT NULL,
                         `user_id` bigint DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `UKpvctx4dbua9qh4p4s3gm3scrh` (`email`),
                         UNIQUE KEY `UKrpkd84jnduk7tp07j3tlpne15` (`phone_number`),
                         UNIQUE KEY `UK7qatq4kob2sr6rlp44khhj53g` (`user_id`),
                         KEY `FK68xevww9py6d2ieym64tiyehm` (`store_id`),
                         CONSTRAINT `FK68xevww9py6d2ieym64tiyehm` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`),
                         CONSTRAINT `FKdlvw23ak3u9v9bomm8g12rtc0` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staff`
--

LOCK TABLES `staff` WRITE;
/*!40000 ALTER TABLE `staff` DISABLE KEYS */;
/*!40000 ALTER TABLE `staff` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stores`
--

DROP TABLE IF EXISTS `stores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stores` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `store_name` varchar(100) NOT NULL,
                          `is_activated` tinyint(1) DEFAULT '1',
                          `created_at` datetime(6) DEFAULT NULL,
                          `created_by` bigint DEFAULT NULL,
                          `is_deleted` tinyint(1) DEFAULT '0',
                          `deleted_at` datetime(6) DEFAULT NULL,
                          `deleted_by` bigint DEFAULT NULL,
                          `updated_at` datetime(6) DEFAULT NULL,
                          `updated_by` bigint DEFAULT NULL,
                          `address` varchar(255) DEFAULT NULL,
                          `name` varchar(100) NOT NULL,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `store_name` (`store_name`),
                          UNIQUE KEY `UKki78gykrclic213ssuw4s7xq9` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stores`
--

LOCK TABLES `stores` WRITE;
/*!40000 ALTER TABLE `stores` DISABLE KEYS */;
/*!40000 ALTER TABLE `stores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `support_requests`
--

DROP TABLE IF EXISTS `support_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `support_requests` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `created_by` bigint DEFAULT NULL,
                                    `updated_by` bigint DEFAULT NULL,
                                    `deleted_by` bigint DEFAULT NULL,
                                    `created_at` datetime(6) DEFAULT NULL,
                                    `updated_at` datetime(6) DEFAULT NULL,
                                    `deleted_at` datetime(6) DEFAULT NULL,
                                    `is_deleted` tinyint(1) DEFAULT '0',
                                    `is_activated` tinyint(1) DEFAULT '1',
                                    `table_id` bigint NOT NULL,
                                    `request_type` varchar(20) NOT NULL,
                                    `status` varchar(20) NOT NULL,
                                    `details` text,
                                    `staff_id` bigint DEFAULT NULL,
                                    `resolved_at` datetime(6) DEFAULT NULL,
                                    `customer_user_id` bigint NOT NULL,
                                    `staff_user_id` bigint DEFAULT NULL,
                                    PRIMARY KEY (`id`),
                                    KEY `fk_support_table` (`table_id`),
                                    KEY `fk_support_staff` (`staff_id`),
                                    KEY `FKakbbsj0bxebjwfwmr949ckwnj` (`customer_user_id`),
                                    KEY `FKhm6gr8n3j8m73ab22f5qbu220` (`staff_user_id`),
                                    CONSTRAINT `fk_support_staff` FOREIGN KEY (`staff_id`) REFERENCES `users` (`id`),
                                    CONSTRAINT `fk_support_table` FOREIGN KEY (`table_id`) REFERENCES `tables` (`id`),
                                    CONSTRAINT `FKakbbsj0bxebjwfwmr949ckwnj` FOREIGN KEY (`customer_user_id`) REFERENCES `users` (`id`),
                                    CONSTRAINT `FKhm6gr8n3j8m73ab22f5qbu220` FOREIGN KEY (`staff_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `support_requests`
--

LOCK TABLES `support_requests` WRITE;
/*!40000 ALTER TABLE `support_requests` DISABLE KEYS */;
/*!40000 ALTER TABLE `support_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tables`
--

DROP TABLE IF EXISTS `tables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tables` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `created_by` bigint DEFAULT NULL,
                          `updated_by` bigint DEFAULT NULL,
                          `deleted_by` bigint DEFAULT NULL,
                          `created_at` datetime(6) DEFAULT NULL,
                          `updated_at` datetime(6) DEFAULT NULL,
                          `deleted_at` datetime(6) DEFAULT NULL,
                          `is_deleted` tinyint(1) DEFAULT '0',
                          `is_activated` tinyint(1) DEFAULT '1',
                          `table_number` varchar(255) NOT NULL,
                          `capacity` int NOT NULL,
                          `location` varchar(255) DEFAULT NULL,
                          `status` varchar(255) NOT NULL,
                          `location_id` bigint NOT NULL,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `table_number` (`table_number`),
                          KEY `FKp39k3h17sn06bymyn1q7q5rdd` (`location_id`),
                          CONSTRAINT `FKp39k3h17sn06bymyn1q7q5rdd` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tables`
--

LOCK TABLES `tables` WRITE;
/*!40000 ALTER TABLE `tables` DISABLE KEYS */;
INSERT INTO `tables` VALUES (1,NULL,NULL,NULL,'2025-11-02 11:16:30.279100','2025-11-10 16:18:12.912456',NULL,0,1,'S1',2,NULL,'Available',1),(2,NULL,NULL,NULL,'2025-11-02 11:16:39.564998','2025-11-10 15:51:12.596141',NULL,0,1,'S2',4,NULL,'Available',1),(3,NULL,NULL,NULL,'2025-11-02 11:16:51.600849','2025-11-10 15:51:12.607687',NULL,0,1,'S3',6,NULL,'Available',1),(4,NULL,NULL,NULL,'2025-11-02 11:16:59.600256','2025-11-10 15:51:12.620400',NULL,0,1,'S4',4,NULL,'Available',1),(5,NULL,NULL,NULL,'2025-11-02 11:17:03.195069','2025-11-10 15:51:12.632449',NULL,0,1,'S5',4,NULL,'Available',1),(6,NULL,NULL,NULL,'2025-11-02 11:17:11.352047','2025-11-07 08:22:25.963139',NULL,0,1,'T1',4,NULL,'Available',4),(7,NULL,NULL,NULL,'2025-11-02 11:17:20.312812','2025-11-02 11:17:20.312826',NULL,0,1,'T2',4,NULL,'Available',4),(8,NULL,NULL,NULL,'2025-11-02 11:17:32.784847','2025-11-10 15:51:12.667065',NULL,0,1,'T3',4,NULL,'Available',1),(9,NULL,NULL,NULL,'2025-11-02 11:17:37.347354','2025-11-10 15:51:12.678057',NULL,0,1,'T4',4,NULL,'Available',1),(10,NULL,NULL,NULL,'2025-11-05 10:34:18.948022','2025-11-10 15:51:12.689426',NULL,0,1,'S6',6,NULL,'Available',1);
/*!40000 ALTER TABLE `tables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `created_by` bigint DEFAULT NULL,
                                `updated_by` bigint DEFAULT NULL,
                                `deleted_by` bigint DEFAULT NULL,
                                `created_at` datetime(6) DEFAULT NULL,
                                `updated_at` datetime(6) DEFAULT NULL,
                                `deleted_at` datetime(6) DEFAULT NULL,
                                `is_deleted` tinyint(1) DEFAULT '0',
                                `is_activated` tinyint(1) DEFAULT '1',
                                `order_id` bigint NOT NULL,
                                `amount_paid` decimal(12,2) NOT NULL,
                                `payment_method` varchar(20) NOT NULL,
                                `transaction_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                `transaction_code` varchar(255) DEFAULT NULL,
                                `cashier_id` bigint DEFAULT NULL,
                                `cashier_user_id` bigint DEFAULT NULL,
                                `checkout_url` varchar(500) DEFAULT NULL,
                                `notes` text,
                                `payment_code` bigint DEFAULT NULL,
                                `payment_link_id` varchar(255) DEFAULT NULL,
                                `payment_status` varchar(20) DEFAULT NULL,
                                `payos_reference` varchar(255) DEFAULT NULL,
                                `qr_code` text,
                                PRIMARY KEY (`id`),
                                KEY `fk_transaction_order` (`order_id`),
                                KEY `fk_transaction_cashier` (`cashier_id`),
                                KEY `FKgytjab9adhft5sqwwhl6my90j` (`cashier_user_id`),
                                CONSTRAINT `fk_transaction_cashier` FOREIGN KEY (`cashier_id`) REFERENCES `users` (`id`),
                                CONSTRAINT `fk_transaction_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
                                CONSTRAINT `FKgytjab9adhft5sqwwhl6my90j` FOREIGN KEY (`cashier_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `created_by` bigint DEFAULT NULL,
                              `updated_by` bigint DEFAULT NULL,
                              `deleted_by` bigint DEFAULT NULL,
                              `created_at` datetime(6) DEFAULT NULL,
                              `updated_at` datetime(6) DEFAULT NULL,
                              `deleted_at` datetime(6) DEFAULT NULL,
                              `is_deleted` tinyint(1) DEFAULT '0',
                              `is_activated` tinyint(1) DEFAULT '1',
                              `user_id` bigint NOT NULL,
                              `role_id` bigint NOT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `user_roles_unique` (`user_id`,`role_id`),
                              UNIQUE KEY `user_roles_user_id_role_id_unique` (`user_id`,`role_id`),
                              KEY `fk_userrole_role` (`role_id`),
                              CONSTRAINT `fk_userrole_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
                              CONSTRAINT `fk_userrole_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (1,NULL,NULL,NULL,'2025-11-02 11:10:36.368270','2025-11-02 11:10:36.368285',NULL,0,1,3,1),(3,NULL,NULL,NULL,'2025-11-10 18:49:21.537393','2025-11-10 18:49:21.537405',NULL,0,1,5,5);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `created_by` bigint DEFAULT NULL,
                         `updated_by` bigint DEFAULT NULL,
                         `deleted_by` bigint DEFAULT NULL,
                         `created_at` datetime(6) DEFAULT NULL,
                         `updated_at` datetime(6) DEFAULT NULL,
                         `deleted_at` datetime(6) DEFAULT NULL,
                         `is_deleted` tinyint(1) DEFAULT '0',
                         `is_activated` tinyint(1) DEFAULT '1',
                         `full_name` varchar(100) DEFAULT NULL,
                         `username` varchar(50) NOT NULL,
                         `hashed_password` varchar(255) DEFAULT NULL COMMENT 'Chỉ cho staff',
                         `phone_number` varchar(15) DEFAULT NULL,
                         `email` varchar(255) DEFAULT NULL,
                         `store_id` bigint DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `username` (`username`),
                         UNIQUE KEY `phone_number` (`phone_number`),
                         UNIQUE KEY `UKowbd7kqb60wnmhjyyjnku6tt0` (`username`,`store_id`),
                         KEY `fk_store` (`store_id`),
                         CONSTRAINT `fk_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,NULL,NULL,NULL,'2025-09-27 15:00:43.087068','2025-09-27 15:00:43.087095',NULL,0,0,'John Doe','john_doe','$2a$10$hc643DgWGdfl7PCrmtAby.uMp7BSaYAziBSXYm9/FGTcnSo7oydTC','0987654321','john.doe@example.com',NULL),(3,NULL,NULL,NULL,'2025-11-02 11:10:36.359014','2025-11-02 11:10:36.359046',NULL,0,1,NULL,'demousee','$2a$10$vdQrd13yK3hROvSCziC5kuw0xnexGEAv9ELw14QOf1AKy21il7qtC',NULL,NULL,NULL),(5,NULL,NULL,NULL,'2025-11-10 18:49:21.528181','2025-11-10 18:49:21.528194',NULL,0,1,NULL,'0123456789',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-10 19:03:50
