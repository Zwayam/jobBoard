CREATE DATABASE careersite;

CREATE TABLE `jobs` (
  `referencenumber` varchar(255) NOT NULL,
  `job_title` varchar(255) NOT NULL,
  `created_date` datetime NOT NULL,
  `job_url` text NOT NULL,
  `country` varchar(255) DEFAULT NULL,
  `description` mediumtext CHARACTER SET utf8 NOT NULL,
  `company` varchar(255) DEFAULT NULL,
  `jdskills_known` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


ALTER TABLE `jobs`
  ADD UNIQUE KEY `referencenumber` (`referencenumber`);
