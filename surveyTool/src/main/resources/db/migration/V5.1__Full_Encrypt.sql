ALTER TABLE questionGroup
    ALTER COLUMN title SET DATA TYPE bytea USING DECODE(SUBSTR(title, 10), 'base64');

ALTER TABLE survey
    ALTER COLUMN name SET DATA TYPE bytea USING DECODE(SUBSTR(name, 10), 'base64'),
    ALTER COLUMN description SET DATA TYPE bytea USING DECODE(SUBSTR(description, 10), 'base64');