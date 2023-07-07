ALTER TABLE auth_token
    DROP CONSTRAINT auth_token_id_fkey,
    ADD FOREIGN KEY (id) REFERENCES rataplanUser(id) ON DELETE CASCADE;