ALTER TABLE appointmentrequest
ADD CONSTRAINT appointmentrequest_participationtoken UNIQUE (participationtoken);
