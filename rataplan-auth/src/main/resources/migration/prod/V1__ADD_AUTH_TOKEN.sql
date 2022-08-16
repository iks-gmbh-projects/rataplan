CREATE TABLE IF NOT EXISTS auth_token (
    id integer NOT NULL PRIMARY KEY REFERENCES rataplanuser (id),
    token character varying(6),
    created_date_time timestamp
);

