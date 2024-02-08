CREATE TABLE notification_category (
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name varchar(64) NOT NULL UNIQUE,
    suppressAnonymous boolean NOT NULL DEFAULT FALSE,
    creationTime timestamp with timezone NOT NULL DEFAULT NOW(),
    lastUpdated timestamp with timezone NOT NULL DEFAULT NOW(),
    version integer NOT NULL
);

CREATE TABLE notification_setting (
    userId integer NOT NULL REFERENCES rataplanuser(id),
    categoryId bigint NOT NULL REFERENCES notification_category(id),
    emailCycle integer NOT NULL DEFAULT 1,
    PRIMARY KEY (userId, categoryId)
);

ALTER TABLE rataplanuser
    ADD COLUMN defaultEmailCycle integer NOT NULL DEFAULT 1;

CREATE TABLE notification_queue (
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    recipientId integer NOT NULL REFERENCES rataplanuser(id),
    categoryId bigint NOT NULL REFERENCES notification_category(id),
    creationTime timestamp with timezone NOT NULL DEFAULT NOW(),
    subject bytea NOT NULL,
    content bytea NOT NULL
);