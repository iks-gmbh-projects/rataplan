CREATE TABLE notification_category
(
    id           bigint                  NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name         varchar(64)             NOT NULL UNIQUE,
    creationTime timestamp with time zone NOT NULL DEFAULT NOW(),
    lastUpdated  timestamp with time zone NOT NULL DEFAULT NOW(),
    version      integer                 NOT NULL
);

CREATE TABLE notification_category_setting
(
    userId     integer NOT NULL REFERENCES rataplanuser (id) ON DELETE CASCADE,
    categoryId bigint  NOT NULL REFERENCES notification_category (id) ON DELETE CASCADE,
    emailCycle integer NOT NULL DEFAULT 1,
    PRIMARY KEY (userId, categoryId)
);

CREATE TABLE notification_type
(
    id                bigint                  NOT NULL PRIMARY KEY AUTO_INCREMENT,
    categoryId        bigint                  NOT NULL REFERENCES notification_category (id) ON DELETE CASCADE,
    name              varchar(64)             NOT NULL UNIQUE,
    suppressAnonymous boolean                 NOT NULL DEFAULT FALSE,
    creationTime      timestamp with time zone NOT NULL DEFAULT NOW(),
    lastUpdated       timestamp with time zone NOT NULL DEFAULT NOW(),
    version           integer                 NOT NULL

);

CREATE TABLE notification_type_setting
(
    userId     integer NOT NULL REFERENCES rataplanuser (id) ON DELETE CASCADE,
    typeId     bigint  NOT NULL REFERENCES notification_type (id) ON DELETE CASCADE,
    emailCycle integer NOT NULL,
    PRIMARY KEY (userId, typeId)
);

ALTER TABLE rataplanuser
    ADD COLUMN defaultEmailCycle integer NOT NULL DEFAULT 1;

CREATE TABLE notification_queue
(
    id           bigint                  NOT NULL PRIMARY KEY AUTO_INCREMENT,
    recipientId  integer                 NOT NULL REFERENCES rataplanuser (id) ON DELETE CASCADE,
    typeId       bigint                  NOT NULL REFERENCES notification_type (id) ON DELETE CASCADE,
    creationTime timestamp with time zone NOT NULL DEFAULT NOW(),
    subject      bytea                   NOT NULL,
    content      bytea                   NOT NULL
);