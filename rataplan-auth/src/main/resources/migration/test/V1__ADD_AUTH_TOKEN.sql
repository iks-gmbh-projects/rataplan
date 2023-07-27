CREATE TABLE IF NOT EXISTS auth_token (
    id integer NOT NULL PRIMARY KEY,
    token character varying(6),
    created_date_time timestamp,
    CONSTRAINT auth_token_id_fkey FOREIGN KEY (id) REFERENCES rataplanuser (id)
);

