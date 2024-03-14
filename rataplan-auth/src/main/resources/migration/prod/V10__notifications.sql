CREATE TABLE notification_category (
    id bigserial NOT NULL PRIMARY KEY,
    name varchar(64) NOT NULL UNIQUE,
    suppressAnonymous boolean NOT NULL,
    creationTime timestamp with time zone NOT NULL DEFAULT NOW(),
    lastUpdated timestamp with time zone NOT NULL DEFAULT NOW(),
    version integer NOT NULL
);

CREATE TABLE notification_setting (
    userId integer NOT NULL REFERENCES rataplanuser(id),
    categoryId bigint NOT NULL REFERENCES notification_category(id),
    emailCycle integer NOT NULL,
    PRIMARY KEY (userId, categoryId)
);

ALTER TABLE rataplanuser
    ADD COLUMN defaultEmailCycle integer NOT NULL DEFAULT 1;

CREATE TABLE notification_queue (
    id bigserial NOT NULL PRIMARY KEY,
    recipientId integer NOT NULL REFERENCES rataplanuser(id),
    categoryId bigint NOT NULL REFERENCES notification_category(id),
    creationTime timestamp with time zone NOT NULL DEFAULT NOW(),
    subject bytea NOT NULL,
    content bytea NOT NULL
);