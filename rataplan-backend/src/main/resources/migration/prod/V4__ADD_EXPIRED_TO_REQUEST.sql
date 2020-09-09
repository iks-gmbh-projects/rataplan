ALTER TABLE appointmentRequest 
ADD COLUMN isExpired boolean;

UPDATE appointmentRequest
set isExpired = false
WHERE deadline > now();

UPDATE appointmentRequest
set isExpired = true
WHERE deadline <= now();