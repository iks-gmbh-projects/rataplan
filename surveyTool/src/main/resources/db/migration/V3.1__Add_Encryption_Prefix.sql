ALTER TABLE answer
ALTER COLUMN text SET DATA TYPE VARCHAR(5000);

UPDATE answer
SET text = 'RAW__##__' || text
WHERE text IS NOT NULL;

ALTER TABLE checkbox
ALTER COLUMN text SET DATA TYPE VARCHAR(400);

UPDATE checkbox
SET text = 'RAW__##__' || text;

ALTER TABLE question
ALTER COLUMN text SET DATA TYPE VARCHAR(400);

UPDATE question
SET text = 'RAW__##__' || text;

ALTER TABLE questionGroup
ALTER COLUMN title SET DATA TYPE VARCHAR(400);

UPDATE questionGroup
SET title = 'RAW__##__' || title;

ALTER TABLE survey
ALTER COLUMN name SET DATA TYPE VARCHAR(400);

UPDATE survey
SET name = 'RAW__##__' || name;

ALTER TABLE survey
ALTER COLUMN description SET DATA TYPE VARCHAR(4000);

UPDATE survey
SET description = 'RAW__##__' || description
WHERE description IS NOT NULL;
