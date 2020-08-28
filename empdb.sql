/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 10.4.6-MariaDB : Database - empdb
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`empdb` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `empdb`;

/*Table structure for table `compliance` */

DROP TABLE IF EXISTS `compliance`;

CREATE TABLE `compliance` (
  `complianceid` int(10) NOT NULL AUTO_INCREMENT,
  `rlType` varchar(15) DEFAULT NULL,
  `details` varchar(250) DEFAULT NULL,
  `createDate` date DEFAULT NULL,
  `department_id` int(10) DEFAULT NULL,
  PRIMARY KEY (`complianceid`),
  KEY `FK_DEPART_ID` (`department_id`),
  CONSTRAINT `FK_DEPART_ID` FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5000 DEFAULT CHARSET=utf8;

/*Data for the table `compliance` */

LOCK TABLES `compliance` WRITE;

UNLOCK TABLES;

/*Table structure for table `department` */

DROP TABLE IF EXISTS `department`;

CREATE TABLE `department` (
  `department_id` int(10) NOT NULL AUTO_INCREMENT,
  `department_nm` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`department_id`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8;

/*Data for the table `department` */

LOCK TABLES `department` WRITE;

insert  into `department`(`department_id`,`department_nm`) values 
(1,'Management'),
(2,'Sales and Marketing'),
(3,'Software Development'),
(4,'Infrastructure'),
(100,'Logistics');

UNLOCK TABLES;

/*Table structure for table `employees` */

DROP TABLE IF EXISTS `employees`;

CREATE TABLE `employees` (
  `empid` int(10) NOT NULL AUTO_INCREMENT,
  `firstname` varchar(45) DEFAULT NULL,
  `lastname` varchar(45) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `department_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`empid`),
  KEY `FK_DEPARTMENT_ID` (`department_id`),
  CONSTRAINT `FK_DEPARTMENT_ID` FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10003 DEFAULT CHARSET=utf8;

/*Data for the table `employees` */

LOCK TABLES `employees` WRITE;

insert  into `employees`(`empid`,`firstname`,`lastname`,`dob`,`email`,`department_id`) values 
(10000,'Poojan','Sharma','1987-09-26','poojan.d.sharma@gmail.com',1),
(10001,'Januka','Dulal','1986-11-08','januka.d.sharma@gmail.com',1);

UNLOCK TABLES;

/*Table structure for table `login_master` */

DROP TABLE IF EXISTS `login_master`;

CREATE TABLE `login_master` (
  `userid` int(10) NOT NULL AUTO_INCREMENT,
  `password` varchar(30) DEFAULT NULL,
  `role` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`userid`),
  CONSTRAINT `FK_EMPID` FOREIGN KEY (`userid`) REFERENCES `employees` (`empid`)
) ENGINE=InnoDB AUTO_INCREMENT=10003 DEFAULT CHARSET=utf8;

/*Data for the table `login_master` */

LOCK TABLES `login_master` WRITE;

insert  into `login_master`(`userid`,`password`,`role`) values 
(10000,'test','ADMIN'),
(10001,'password','EMPLOYEE');

UNLOCK TABLES;

/*Table structure for table `statusreport` */

DROP TABLE IF EXISTS `statusreport`;

CREATE TABLE `statusreport` (
  `complianceid` int(11) NOT NULL AUTO_INCREMENT,
  `statusrptid` int(11) DEFAULT NULL,
  `empid` int(11) DEFAULT NULL,
  `comments` varchar(15) DEFAULT NULL,
  `createDate` date DEFAULT NULL,
  `department_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`complianceid`),
  KEY `statusreport_ibfk_1` (`department_id`),
  KEY `statusreport_ibfk_2` (`empid`),
  CONSTRAINT `statusreport_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`),
  CONSTRAINT `statusreport_ibfk_2` FOREIGN KEY (`empid`) REFERENCES `employees` (`empid`),
  CONSTRAINT `statusreport_ibfk_3` FOREIGN KEY (`complianceid`) REFERENCES `compliance` (`complianceid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `statusreport` */

LOCK TABLES `statusreport` WRITE;

UNLOCK TABLES;

/* Trigger structure for table `employees` */

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `employees_after_insert` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `employees_after_insert` AFTER INSERT ON `employees` FOR EACH ROW BEGIN
    
    INSERT INTO login_master (userid, PASSWORD, ROLE) VALUES (new.empid, 'password', 'EMPLOYEE');

    END */$$


DELIMITER ;

/* Procedure structure for procedure `addEmp_sp` */

/*!50003 DROP PROCEDURE IF EXISTS  `addEmp_sp` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `addEmp_sp`(
	IN p_first_name VARCHAR(45),
	IN p_last_name VARCHAR(45),
	IN p_dob DATE,
	IN p_email_id VARCHAR(100),
	IN p_dept_id INT(11)
    )
BEGIN
	
		INSERT INTO `employees` (
		  `firstname`,
		  `lastname`,
		  `dob`,
		  `email`,
		  `department_id`
		)
		VALUES
		  (
		    p_first_name,
		    p_last_name,
		    p_dob,
		    p_email_id,
		    p_dept_id
		  );

	END */$$
DELIMITER ;

/* Procedure structure for procedure `getUserDetails` */

/*!50003 DROP PROCEDURE IF EXISTS  `getUserDetails` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserDetails`(
	inout p_user_id int(11),
	out p_password varchar(30),
	out p_role varchar(20)
    )
BEGIN
	
		SELECT userid,password,role 
		 INTO p_user_id, p_password, p_role 
		 FROM login_master where userid =p_user_id;

	END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
