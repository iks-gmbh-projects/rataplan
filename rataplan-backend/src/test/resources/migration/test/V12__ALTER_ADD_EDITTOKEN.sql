ALTER TABLE appointmentRequest
ADD COLUMN editToken varchar(10);

ALTER TABLE appointmentRequest
ADD CONSTRAINT appointmentRequest_editToken UNIQUE (editToken);
