CREATE TABLE notification_category
(
    id   bigserial   NOT NULL PRIMARY KEY,
    name varchar(64) NOT NULL UNIQUE,
    creationTime timestamp with time zone NOT NULL DEFAULT NOW(),
    lastUpdated timestamp with time zone NOT NULL DEFAULT NOW(),
    version integer NOT NULL
);

CREATE TABLE notification_category_setting
(
    userId     integer NOT NULL REFERENCES rataplanuser (id) ON DELETE CASCADE,
    categoryId bigint  NOT NULL REFERENCES notification_category (id) ON DELETE CASCADE,
    emailCycle integer NOT NULL,
    PRIMARY KEY (userId, categoryId)
);

CREATE TABLE notification_type
(
    id         bigserial   NOT NULL PRIMARY KEY,
    categoryId bigint      NOT NULL REFERENCES notification_category (id) ON DELETE CASCADE,
    name       varchar(64) NOT NULL UNIQUE,
    suppressAnonymous boolean NOT NULL,
    creationTime timestamp with time zone NOT NULL DEFAULT NOW(),
    lastUpdated timestamp with time zone NOT NULL DEFAULT NOW(),
    version integer NOT NULL
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
    id           bigserial                NOT NULL PRIMARY KEY,
    recipientId  integer                  NOT NULL REFERENCES rataplanuser (id) ON DELETE CASCADE,
    typeId       bigint                   NOT NULL REFERENCES notification_type (id) ON DELETE CASCADE,
    creationTime timestamp WITH TIME ZONE NOT NULL DEFAULT NOW(),
    subject      bytea                    NOT NULL,
    content      bytea                    NOT NULL
);