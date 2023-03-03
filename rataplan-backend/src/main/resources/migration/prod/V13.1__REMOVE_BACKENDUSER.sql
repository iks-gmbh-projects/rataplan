ALTER TABLE appointmentmember
DROP CONSTRAINT appointmentmember_backenduserid_fkey;

ALTER TABLE appointmentmember
RENAME COLUMN backenduserid TO userid;

UPDATE appointmentmember SET userid=backenduser.authuserid
FROM backenduser
WHERE appointmentmember.userid=backenduser.id;

ALTER TABLE appointmentrequest
DROP CONSTRAINT appointmentrequest_backenduserid_fkey;

ALTER TABLE appointmentrequest
RENAME COLUMN backenduserid TO userid;

UPDATE appointmentrequest
SET userid=backenduser.authuserid
FROM backenduser
WHERE appointmentrequest.userid=backenduser.id;

ALTER TABLE backenduseraccess
DROP CONSTRAINT backenduseraccess_backenduserid_fkey;

ALTER TABLE backenduseraccess
RENAME COLUMN backenduserid TO userid;

UPDATE backenduseraccess
SET userid=backenduser.authuserid
FROM backenduser
WHERE backenduseraccess.userid=backenduser.id;

DROP TABLE backenduser;
