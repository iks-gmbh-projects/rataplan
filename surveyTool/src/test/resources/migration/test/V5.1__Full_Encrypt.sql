ALTER TABLE questionGroup
    ALTER COLUMN title SET DATA TYPE bytea;

ALTER TABLE survey
    ALTER COLUMN name SET DATA TYPE bytea;
ALTER TABLE survey
    ALTER COLUMN description SET DATA TYPE bytea;