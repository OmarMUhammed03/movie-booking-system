\set ON_ERROR_STOP on

BEGIN;

CREATE TABLE IF NOT EXISTS users (
  id uuid PRIMARY KEY,
  auth_user_id uuid NOT NULL UNIQUE,
  first_name varchar(255),
  last_name varchar(255),
  phone varchar(255)
);

TRUNCATE TABLE users;

INSERT INTO users (id, auth_user_id, first_name, last_name, phone)
VALUES
  ('91000000-0000-0000-0000-000000000001', '90000000-0000-0000-0000-000000000001', 'Frontend', 'Tester', '+201000000001'),
  ('91000000-0000-0000-0000-000000000002', '90000000-0000-0000-0000-000000000002', 'Cinema', 'Admin', '+201000000002');

COMMIT;
