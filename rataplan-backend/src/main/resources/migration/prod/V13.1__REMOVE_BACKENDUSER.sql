ALTER TABLE appointmentmember
DROP CONSTRAINT appointmentmember_backenduserid_fkey;

ALTER TABLE appointmentmember
RENAME COLUMN backenduserid TO userid;

UPDATE appointmentmember SET appointmentmember.userid=backenduser.authuserid
FROM appointmentmember
WHERE appointmentmember.backenduserid=backenduser.id
JOIN backenduser on (appointmentmember.userid=backenduser.authuserid);

ALTER TABLE appointmentrequest
DROP CONSTRAINT appointmentrequest_backenduserid_fkey;

ALTER TABLE appointmentrequest
RENAME COLUMN backenduserid TO userid;

UPDATE appointmentrequest
SET appointmentrequest.userid=backenduser.authuserid
FROM appointmentrequest
JOIN backenduser on (appointmentrequest.userid=backenduser.authuserid);

ALTER TABLE backenduseraccess
DROP CONSTRAINT backenduseraccess_backenduserid_fkey;

ALTER TABLE backenduseraccess
RENAME COLUMN backenduserid TO userid;

UPDATE backenduseraccess
SET backenduseraccess.userid=backenduser.authuserid
FROM backenduseraccess
JOIN backenduser on (appointmentmember.userid=backenduser.authuserid);

DROP TABLE backenduser;
