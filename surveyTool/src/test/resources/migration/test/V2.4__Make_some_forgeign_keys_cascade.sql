ALTER TABLE checkbox
	DROP CONSTRAINT checkbox_checkboxGroupId_fkey;
ALTER TABLE checkbox
	ADD CONSTRAINT checkbox_checkboxGroupId_fkey FOREIGN KEY (checkboxGroupId) REFERENCES checkboxGroup(id) ON DELETE CASCADE;

ALTER TABLE checkboxGroup
	DROP CONSTRAINT checkboxGroup_questionId_fkey;
ALTER TABLE checkboxGroup
	ADD CONSTRAINT checkboxGroup_questionId_fkey FOREIGN KEY (questionId) REFERENCES question(id) ON DELETE CASCADE;

ALTER TABLE question
	DROP CONSTRAINT question_questionGroupId_fkey;
ALTER TABLE question
	ADD CONSTRAINT question_questionGroupId_fkey FOREIGN KEY (questionGroupId) REFERENCES questionGroup(id) ON DELETE CASCADE;

ALTER TABLE questionGroup
	DROP CONSTRAINT questionGroup_surveyID_fkey;
ALTER TABLE questionGroup
	ADD CONSTRAINT questionGroup_surveyID_fkey FOREIGN KEY (surveyId) REFERENCES survey(id) ON DELETE CASCADE;

ALTER TABLE surveyResponse
	DROP CONSTRAINT surveyResponse_surveyId_fkey;
ALTER TABLE surveyResponse
	ADD CONSTRAINT surveyResponse_surveyId_fkey FOREIGN KEY (surveyId) REFERENCES survey(id) ON DELETE CASCADE;

ALTER TABLE answer
	DROP CONSTRAINT answer_questionId_fkey;
ALTER TABLE answer
	DROP CONSTRAINT answer_responseId_fkey;
ALTER TABLE answer
	ADD CONSTRAINT answer_questionId_fkey FOREIGN KEY (questionId) REFERENCES question(id) ON DELETE CASCADE;
ALTER TABLE answer
	ADD CONSTRAINT answer_responseId_fkey FOREIGN KEY (responseId) REFERENCES surveyResponse(id) ON DELETE CASCADE;