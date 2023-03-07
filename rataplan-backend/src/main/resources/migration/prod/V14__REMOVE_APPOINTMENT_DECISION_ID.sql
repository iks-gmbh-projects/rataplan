ALTER TABLE appointmentdecision
    DROP COLUMN id,
    ADD PRIMARY KEY (appointmentid, appointmentmemberid);