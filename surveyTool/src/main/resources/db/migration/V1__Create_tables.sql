DROP TABLE IF EXISTS Answer;
DROP TABLE IF EXISTS Checkbox;
DROP TABLE IF EXISTS Checkbox_Group;
DROP TABLE IF EXISTS Question;
DROP TABLE IF EXISTS Question_Group;
DROP TABLE IF EXISTS Survey;
DROP TABLE IF EXISTS User;

CREATE TABLE User
(
    Id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    Creation_Time DATETIME     NULL,
    Last_Updated  DATETIME     NULL,
    Version       INT          NULL,
    Name          VARCHAR(255) NULL
);

CREATE TABLE Survey
(
    Id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    Creation_Time DATETIME     NULL,
    Last_Updated  DATETIME     NULL,
    Version       INT          NULL,
    Accessid      VARCHAR(255) NULL,
    Description   LONGTEXT     NULL,
    End_Date      DATETIME(6)  NULL,
    Name          VARCHAR(255) NOT NULL,
    Open          BOOLEAN      NOT NULL,
    Start_Date    DATETIME(6)  NULL,
    Uuid          VARCHAR(255) NULL,
    User_Id       BIGINT       NULL,
    FOREIGN KEY (User_Id) REFERENCES User (Id)
);

CREATE TABLE Question_Group
(
    Id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    Creation_Time DATETIME     NULL,
    Last_Updated  DATETIME     NULL,
    Version       INT          NULL,
    Title         VARCHAR(255) NULL,
    Survey_Id     BIGINT       NULL,
    FOREIGN KEY (Survey_Id) REFERENCES Survey (Id)
);

CREATE TABLE Question
(
    Id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    Creation_Time     DATETIME     NULL,
    Last_Updated      DATETIME     NULL,
    Version           INT          NULL,
    Has_Checkbox      BOOLEAN      NOT NULL,
    Required          BOOLEAN      NOT NULL,
    Text              VARCHAR(255) NOT NULL,
    Question_Group_Id BIGINT       NULL,
    FOREIGN KEY (Question_Group_Id) REFERENCES Question_Group (Id)
);

CREATE TABLE Checkbox_Group
(
    Id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    Creation_Time   DATETIME NULL,
    Last_Updated    DATETIME NULL,
    Version         INT      NULL,
    Max_Select      INT      NOT NULL,
    Min_Select      INT      NOT NULL,
    Multiple_Select BOOLEAN  NOT NULL,
    Question_Id     BIGINT   NULL,
    FOREIGN KEY (Question_Id) REFERENCES Question (Id)
);

CREATE TABLE Checkbox
(
    Id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    Creation_Time     DATETIME     NULL,
    Last_Updated      DATETIME     NULL,
    Version           INT          NULL,
    Has_Text_Field    BOOLEAN      NOT NULL,
    Text              VARCHAR(255) NOT NULL,
    Checkbox_Group_Id BIGINT       NULL,
    FOREIGN KEY (Checkbox_Group_Id) REFERENCES Checkbox_Group (Id)
);

CREATE TABLE Answer
(
    Id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    Creation_Time DATETIME NULL,
    Last_Updated  DATETIME NULL,
    Version       INT      NULL,
    Text          LONGTEXT NULL,
    Checkbox_Id   BIGINT   NULL,
    Question_Id   BIGINT   NULL,
    User_Id       BIGINT   NULL,
    FOREIGN KEY (User_Id) REFERENCES User (Id),
    FOREIGN KEY (Question_Id) REFERENCES Question (Id),
    FOREIGN KEY (Checkbox_Id) REFERENCES Checkbox (Id)
);






