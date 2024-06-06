CREATE USER "rataplan-backend" WITH LOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE INHERIT NOREPLICATION CONNECTION LIMIT -1;
CREATE DATABASE "rataplan-backend" WITH OWNER = "rataplan-backend" ENCODING = 'UTF8' CONNECTION LIMIT = -1 IS_TEMPLATE = False;

DO $$BEGIN
    EXECUTE format('ALTER USER "rataplan-backend" PASSWORD %L', pg_read_file('/run/secrets/pg-backend-pw'));
END$$;
