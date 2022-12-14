UPDATE answer
SET text = 'RAW__##__' || text
WHERE text IS NOT NULL;

UPDATE checkbox
SET text = 'RAW__##__' || text;

UPDATE question
SET text = 'RAW__##__' || text;

UPDATE questionGroup
SET title = 'RAW__##__' || title;

UPDATE survey
SET name = 'RAW__##__' || name;

UPDATE survey
SET description = 'RAW__##__' || description
WHERE description IS NOT NULL;
