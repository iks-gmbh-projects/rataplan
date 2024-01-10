ALTER TABLE `appointmentMember`
ADD COLUMN `backendUserId` int;

ALTER TABLE `appointmentMember`
ADD CONSTRAINT appointmentmember_backenduserid_fkey FOREIGN KEY (backendUserId) REFERENCES backendUser (id);