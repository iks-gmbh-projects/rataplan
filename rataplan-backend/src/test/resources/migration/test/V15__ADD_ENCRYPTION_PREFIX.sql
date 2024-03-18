ALTER TABLE appointmentRequest
    ALTER COLUMN title SET DATA TYPE VARCHAR(200);
ALTER TABLE appointmentRequest
    ALTER COLUMN description SET DATA TYPE VARCHAR(1500);
ALTER TABLE appointmentRequest
    ALTER COLUMN organizerMail SET DATA TYPE VARCHAR(200);
ALTER TABLE appointmentRequest
    ALTER COLUMN organizerName SET DATA TYPE VARCHAR(100);

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

ALTER TABLE appointment
    ALTER COLUMN description SET DATA TYPE VARCHAR(300);
ALTER TABLE appointment
    ALTER COLUMN url SET DATA TYPE VARCHAR(300);

UPDATE appointment
SET description = 'RAW__##__' || description
WHERE description IS NOT NULL;

UPDATE appointment
SET url = 'RAW__##__' || description
WHERE url IS NOT NULL;

ALTER TABLE appointmentMember
    ALTER COLUMN name SET DATA TYPE VARCHAR(200);

UPDATE appointmentMember
SET name = 'RAW__##__' || name
WHERE name IS NOT NULL;
