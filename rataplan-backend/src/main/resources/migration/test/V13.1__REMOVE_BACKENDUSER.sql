ALTER TABLE appointmentmember
DROP CONSTRAINT appointmentmember_backenduserid_fkey;

ALTER TABLE appointmentmember
RENAME COLUMN backenduserid TO userid;

ALTER TABLE appointmentrequest
DROP CONSTRAINT appointmentrequest_backenduserid_fkey;

ALTER TABLE appointmentrequest
RENAME COLUMN backenduserid TO userid;

ALTER TABLE backenduseraccess
DROP CONSTRAINT backenduseraccess_backenduserid_fkey;

ALTER TABLE backenduseraccess
RENAME COLUMN backenduserid TO userid;

DROP TABLE backenduser;
