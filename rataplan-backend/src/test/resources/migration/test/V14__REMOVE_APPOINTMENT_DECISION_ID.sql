ALTER TABLE appointmentdecision
    DROP COLUMN id;
ALTER TABLE appointmentdecision
    ALTER COLUMN appointmentid SET NOT NULL;
ALTER TABLE appointmentdecision
    ALTER COLUMN appointmentmemberid SET NOT NULL;
ALTER TABLE appointmentdecision
    ADD PRIMARY KEY (appointmentid, appointmentmemberid);