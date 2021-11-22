RENAME TABLE User TO userTempName,
    Survey TO surveyTempName,
    Question_Group TO questionGroup,
    Question TO questionTempName,
    Checkbox_Group TO checkboxGroup,
    Checkbox TO checkboxTempName,
    Answer TO answerTempName;

RENAME TABLE userTempName TO user,
    surveyTempName TO survey,
    questionTempName TO question,
    checkboxTempName TO checkbox,
    answerTempName TO answer;

ALTER TABLE user
    CHANGE Id id BIGINT AUTO_INCREMENT,
    CHANGE Creation_Time creationTime DATETIME NOT NULL,
    CHANGE Last_Updated lastUpdated DATETIME NOT NULL,
    CHANGE Version version INT NOT NULL,
    CHANGE Name name VARCHAR(255) NOT NULL;

ALTER TABLE survey
    CHANGE Id id BIGINT AUTO_INCREMENT,
    CHANGE Creation_Time creationTime DATETIME NOT NULL,
    CHANGE Last_Updated lastUpdated DATETIME NOT NULL,
    CHANGE Version version INT NOT NULL,
    CHANGE Name name VARCHAR(255) NOT NULL,
    CHANGE Description description VARCHAR(3000),
    CHANGE End_Date endDate DATETIME(6) NOT NULL,
    CHANGE Start_Date startDate DATETIME(6) NOT NULL,
    CHANGE Open openAccess BOOLEAN NOT NULL,
    CHANGE Access_Id accessId VARCHAR(255) NOT NULL,
    CHANGE Participation_Id participationId VARCHAR(255) NOT NULL,
    DROP FOREIGN KEY survey_ibfk_1,
    CHANGE User_Id userId BIGINT,
    ADD CONSTRAINT FOREIGN KEY (userId) REFERENCES user (id);

ALTER TABLE questionGroup
    CHANGE Id id BIGINT AUTO_INCREMENT,
    CHANGE Creation_Time creationTime DATETIME NOT NULL,
    CHANGE Last_Updated lastUpdated DATETIME NOT NULL,
    CHANGE Version version INT NOT NULL,
    CHANGE Title title VARCHAR(255) NOT NULL,
    DROP FOREIGN KEY questiongroup_ibfk_1,
    CHANGE Survey_Id surveyId BIGINT,
    ADD CONSTRAINT FOREIGN KEY (surveyId) REFERENCES survey (id);

ALTER TABLE question
    CHANGE Id id BIGINT AUTO_INCREMENT,
    CHANGE Creation_Time creationTime DATETIME NOT NULL,
    CHANGE Last_Updated lastUpdated DATETIME NOT NULL,
    CHANGE Version version INT NOT NULL,
    CHANGE Has_Checkbox hasCheckbox BOOLEAN NOT NULL,
    CHANGE Required required BOOLEAN NOT NULL,
    CHANGE Text text VARCHAR(500) NOT NULL,
    DROP FOREIGN KEY question_ibfk_1,
    CHANGE Question_Group_Id questionGroupId BIGINT,
    ADD CONSTRAINT FOREIGN KEY (questionGroupId) REFERENCES questionGroup (id);

ALTER TABLE checkboxGroup
    CHANGE Id id BIGINT AUTO_INCREMENT,
    CHANGE Creation_Time creationTime DATETIME NOT NULL,
    CHANGE Last_Updated lastUpdated DATETIME NOT NULL,
    CHANGE Version version INT NOT NULL,
    CHANGE Multiple_Select multipleSelect BOOLEAN NOT NULL,
    CHANGE Max_Select maxSelect INT,
    CHANGE Min_Select minSelect INT,
    DROP FOREIGN KEY checkboxgroup_ibfk_1,
    CHANGE Question_Id questionId BIGINT,
    ADD CONSTRAINT FOREIGN KEY (questionId) REFERENCES question (id);

ALTER TABLE checkbox
    CHANGE Id id BIGINT AUTO_INCREMENT,
    CHANGE Creation_Time creationTime DATETIME NOT NULL,
    CHANGE Last_Updated lastUpdated DATETIME NOT NULL,
    CHANGE Version version INT NOT NULL,
    CHANGE Text text VARCHAR(255) NOT NULL,
    CHANGE Has_Text_Field hasTextField BOOLEAN NOT NULL,
    DROP FOREIGN KEY checkbox_ibfk_1,
    CHANGE Checkbox_Group_Id checkboxGroupId BIGINT,
    ADD CONSTRAINT FOREIGN KEY (checkboxGroupId) REFERENCES checkboxGroup (id);

ALTER TABLE answer
    CHANGE Id id BIGINT AUTO_INCREMENT,
    CHANGE Creation_Time creationTime DATETIME NOT NULL,
    CHANGE Last_Updated lastUpdated DATETIME NOT NULL,
    CHANGE Version version INT NOT NULL,
    CHANGE Text text VARCHAR(1500),
    DROP FOREIGN KEY answer_ibfk_1,
    DROP FOREIGN KEY answer_ibfk_2,
    DROP FOREIGN KEY answer_ibfk_3,
    CHANGE Checkbox_Id checkboxId BIGINT,
    CHANGE Question_Id questionId BIGINT,
    CHANGE User_Id userId BIGINT,
    ADD CONSTRAINT FOREIGN KEY (checkboxId) REFERENCES checkbox (id),
    ADD CONSTRAINT FOREIGN KEY (questionId) REFERENCES question (id),
    ADD CONSTRAINT FOREIGN KEY (userId) REFERENCES user (id);




