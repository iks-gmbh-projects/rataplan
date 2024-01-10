CREATE TABLE IF NOT EXISTS appointmentRequestConfig (
  id SERIAL PRIMARY KEY,
  decisionType int NOT NULL,
  isStartDate boolean,
  isEndDate boolean,
  isStartTime boolean,
  isEndTime boolean,
  isUrl boolean,
  isDescription boolean
);
CREATE TABLE IF NOT EXISTS appointmentRequest (
  id SERIAL PRIMARY KEY,
  title varchar (100) NOT NULL,
  description varchar(500),
  organizerMail varchar(100),
  deadline date NOT NULL,
  appointmentRequestConfigId int REFERENCES appointmentRequestConfig(id)
);
CREATE TABLE IF NOT EXISTS appointmentmember (
  id SERIAL PRIMARY KEY,
  appointmentRequestId int REFERENCES appointmentRequest(id),
  name varchar(100)
);
CREATE TABLE IF NOT EXISTS appointment (
  id SERIAL PRIMARY KEY,
  appointmentRequestId int REFERENCES appointmentRequest(id),
  startDate timestamptz,
  endDate timestamptz,
  url varchar(30),
  description varchar(200)
);
CREATE TABLE IF NOT EXISTS appointmentdecision (
  id SERIAL PRIMARY KEY,
  appointmentId int REFERENCES appointment(id),
  appointmentMemberId int REFERENCES appointmentMember(id),
  decision int,
  participants int,
  UNIQUE (appointmentId, appointmentMemberId)
);