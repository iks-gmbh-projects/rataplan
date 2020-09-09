ALTER TABLE `appointmentMember`
ADD COLUMN `backendUserId` int;

ALTER TABLE `appointmentMember`
ADD FOREIGN KEY (backendUserId) REFERENCES backendUser (id);