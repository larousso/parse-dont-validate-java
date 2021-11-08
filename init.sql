ALTER SYSTEM SET max_connections = 1000;
ALTER SYSTEM RESET shared_buffers;
CREATE DATABASE parse_dont_validate;
CREATE USER parse_dont_validate WITH PASSWORD 'parse_dont_validate';
GRANT ALL PRIVILEGES ON DATABASE "parse_dont_validate" to parse_dont_validate;