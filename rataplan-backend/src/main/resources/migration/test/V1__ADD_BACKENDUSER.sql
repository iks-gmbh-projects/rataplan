CREATE TABLE IF NOT EXISTS backendUser (
  `id` int NOT NULL AUTO_INCREMENT,
  `authUserId` int UNIQUE,
    PRIMARY KEY (id)
);

ALTER TABLE `appointmentRequest` 
ADD COLUMN `backendUserId` int;

ALTER TABLE `appointmentRequest` 
ADD FOREIGN KEY (backendUserId) REFERENCES backendUser (id);