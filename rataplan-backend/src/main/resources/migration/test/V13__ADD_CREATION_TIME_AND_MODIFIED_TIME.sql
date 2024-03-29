ALTER TABLE appointment
    ADD COLUMN creationTime TIMESTAMP WITH TIMEZONE;
ALTER TABLE appointment
    ADD COLUMN lastUpdated TIMESTAMP WITH TIMEZONE;
ALTER TABLE appointment
    ADD COLUMN version INTEGER NOT NULL;

ALTER TABLE appointmentdecision
    ADD COLUMN creationTime TIMESTAMP WITH TIMEZONE;
ALTER TABLE appointmentdecision
    ADD COLUMN lastUpdated TIMESTAMP WITH TIMEZONE;
ALTER TABLE appointmentdecision
    ADD COLUMN version INTEGER NOT NULL;

ALTER TABLE appointmentmember
    ADD COLUMN creationTime TIMESTAMP WITH TIMEZONE;
ALTER TABLE appointmentmember
    ADD COLUMN lastUpdated TIMESTAMP WITH TIMEZONE;
ALTER TABLE appointmentmember
    ADD COLUMN version INTEGER NOT NULL;

ALTER TABLE appointmentrequest
    ADD COLUMN creationTime TIMESTAMP WITH TIMEZONE;
ALTER TABLE appointmentrequest
    ADD COLUMN lastUpdated TIMESTAMP WITH TIMEZONE;
ALTER TABLE appointmentrequest
    ADD COLUMN version INTEGER NOT NULL;

ALTER TABLE appointmentrequestconfig
    ADD COLUMN creationTime TIMESTAMP WITH TIMEZONE;
ALTER TABLE appointmentrequestconfig
    ADD COLUMN lastUpdated TIMESTAMP WITH TIMEZONE;
ALTER TABLE appointmentrequestconfig
    ADD COLUMN version INTEGER NOT NULL;

ALTER TABLE backenduser
    ADD COLUMN creationTime TIMESTAMP WITH TIMEZONE;
ALTER TABLE backenduser
    ADD COLUMN lastUpdated TIMESTAMP WITH TIMEZONE;
ALTER TABLE backenduser
    ADD COLUMN version INTEGER NOT NULL;

ALTER TABLE backenduseraccess
    ADD COLUMN creationTime TIMESTAMP WITH TIMEZONE;
ALTER TABLE backenduseraccess
    ADD COLUMN lastUpdated TIMESTAMP WITH TIMEZONE;
ALTER TABLE backenduseraccess
    ADD COLUMN version INTEGER NOT NULL;