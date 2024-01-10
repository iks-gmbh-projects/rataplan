ALTER TABLE checkbox
	DROP CONSTRAINT checkbox_checkboxGroupId_fkey,
	ADD FOREIGN KEY (checkboxGroupId) REFERENCES checkboxGroup(id) ON DELETE CASCADE;

ALTER TABLE checkboxGroup
	DROP CONSTRAINT checkboxGroup_questionId_fkey,
	ADD FOREIGN KEY (questionId) REFERENCES question(id) ON DELETE CASCADE;

ALTER TABLE question
	DROP CONSTRAINT question_questionGroupId_fkey,
	ADD FOREIGN KEY (questionGroupId) REFERENCES questionGroup(id) ON DELETE CASCADE;

ALTER TABLE questionGroup
	DROP CONSTRAINT questionGroup_surveyID_fkey,
	ADD FOREIGN KEY (surveyId) REFERENCES survey(id) ON DELETE CASCADE;

ALTER TABLE surveyResponse
	DROP CONSTRAINT surveyResponse_surveyId_fkey,
	ADD FOREIGN KEY (surveyId) REFERENCES survey(id) ON DELETE CASCADE;

ALTER TABLE answer
	DROP CONSTRAINT answer_questionId_fkey,
	DROP CONSTRAINT answer_responseId_fkey,
	ADD FOREIGN KEY (questionId) REFERENCES question(id) ON DELETE CASCADE,
	ADD FOREIGN KEY (responseId) REFERENCES surveyResponse(id) ON DELETE CASCADE;

ALTER TABLE checkboxSelections
	DROP CONSTRAINT checkboxSelections_answerId_fkey,
	DROP CONSTRAINT checkboxSelections_checkboxId_fkey,
	ADD FOREIGN KEY (answerId) REFERENCES answer(id) ON DELETE CASCADE,
	ADD FOREIGN KEY (checkboxId) REFERENCES checkbox(id) ON DELETE CASCADE;