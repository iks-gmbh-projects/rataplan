CREATE TABLE IF NOT EXISTS backendUserAccess (
	id SERIAL PRIMARY KEY,
	isEdit boolean,
	isInvited boolean,
	appointmentRequestId int REFERENCES appointmentRequest(id),
	backendUserId int REFERENCES backendUser(id)
);