CREATE USER "rataplan-survey" WITH LOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE INHERIT NOREPLICATION CONNECTION LIMIT -1;
CREATE DATABASE "rataplan-survey" WITH OWNER = "rataplan-survey" ENCODING = 'UTF8' CONNECTION LIMIT = -1 IS_TEMPLATE = False;

DO $$BEGIN
    EXECUTE format('ALTER USER "rataplan-survey" PASSWORD %L', pg_read_file('/run/secrets/pg-survey-pw'));
END$$;
