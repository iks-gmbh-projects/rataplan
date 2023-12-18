CREATE TABLE feedback
(
    id           bigserial                NOT NULL PRIMARY KEY,
    creationTime timestamp WITH TIMEZONE NOT NULL DEFAULT NOW(),
    lastUpdated  timestamp WITH TIMEZONE NOT NULL DEFAULT NOW(),
    version      int                      NOT NULL DEFAULT 1,
    title        bytea                    NOT NULL,
    text         bytea                    NOT NULL,
    rating       tinyint                 NOT NULL
        CONSTRAINT rating_valid CHECK (rating BETWEEN 0 AND 5),
    category     int                      NOT NULL,
    sent         bool                     NOT NULL DEFAULT FALSE
);