UPDATE appointmentRequest
SET title = 'RAW__##__' || title
WHERE title IS NOT NULL;

UPDATE appointmentRequest
SET description = 'RAW__##__' || description
WHERE description IS NOT NULL;

UPDATE appointmentRequest
SET organizermail = 'RAW__##__' || organizermail
WHERE organizermail IS NOT NULL;

UPDATE appointmentRequest
SET organizername = 'RAW__##__' || organizername
WHERE organizername IS NOT NULL;

UPDATE appointment
SET description = 'RAW__##__' || description
WHERE description IS NOT NULL;

UPDATE appointmentMember
SET name = 'RAW__##__' || name
WHERE name IS NOT NULL;
