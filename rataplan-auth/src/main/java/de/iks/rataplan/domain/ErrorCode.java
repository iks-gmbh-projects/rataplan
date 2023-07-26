package de.iks.rataplan.domain;

// Errorcodes müssen auch für diesen service aufrufende services definiert sein (Backend)
public enum ErrorCode {
	INVALID_TOKEN,
	UNEXPECTED_ERROR,
	MAIL_IN_USE,
	USERNAME_IN_USE,
	WRONG_CREDENTIALS,
	FORBIDDEN,
	TIMEOUT
}
