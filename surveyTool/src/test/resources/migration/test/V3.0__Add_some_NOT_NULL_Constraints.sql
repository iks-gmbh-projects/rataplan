DELETE FROM questionGroup
WHERE surveyId IS NULL;

ALTER TABLE questionGroup
    ALTER COLUMN surveyId SET NOT NULL;

DELETE FROM question
WHERE questionGroupId IS NULL;

ALTER TABLE question
    ALTER COLUMN questionGroupId SET NOT NULL;

DELETE FROM checkboxGroup
WHERE questionId IS NULL;

ALTER TABLE checkboxGroup
    ALTER COLUMN questionId SET NOT NULL;

DELETE FROM checkbox
WHERE checkboxGroupId IS NULL;

ALTER TABLE checkbox
    ALTER COLUMN checkboxGroupId SET NOT NULL;

DELETE FROM answer
WHERE questionId IS NULL;

ALTER TABLE answer
    ALTER COLUMN questionId SET NOT NULL;

DELETE FROM surveyResponse
WHERE surveyId IS NULL;

ALTER TABLE surveyResponse
    ALTER COLUMN surveyId SET NOT NULL;






