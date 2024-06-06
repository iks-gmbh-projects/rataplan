CREATE USER "rataplan-auth" WITH LOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE INHERIT NOREPLICATION CONNECTION LIMIT -1;
CREATE DATABASE "rataplan-auth" WITH OWNER = "rataplan-auth" ENCODING = 'UTF8' CONNECTION LIMIT = -1 IS_TEMPLATE = False;

DO $$BEGIN
    EXECUTE format('ALTER USER "rataplan-auth" PASSWORD %L', pg_read_file('/run/secrets/pg-auth-pw'));
    END$$;
