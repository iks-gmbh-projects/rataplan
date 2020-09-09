CREATE TABLE IF NOT EXISTS `rataplanuser`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `mail` varchar(100) NOT NULL UNIQUE,
  `username` varchar(50) NOT NULL UNIQUE,
  `password` varchar(60) NOT NULL,
  `firstname` varchar(50),
  `lastname` varchar(50)
);