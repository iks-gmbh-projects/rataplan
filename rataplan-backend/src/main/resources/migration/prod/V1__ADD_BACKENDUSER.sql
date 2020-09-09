CREATE TABLE IF NOT EXISTS backendUser (
  id SERIAL PRIMARY KEY,
  authUserId int NOT NULL UNIQUE
);

ALTER TABLE appointmentRequest 
ADD COLUMN backendUserId int;

ALTER TABLE appointmentRequest
ADD FOREIGN KEY (backendUserId) REFERENCES backendUser (id);