\set ON_ERROR_STOP on

BEGIN;

TRUNCATE TABLE refresh_token, auth_user;

INSERT INTO auth_user (id, email, password, provider_id, role, created_at)
VALUES
  ('90000000-0000-0000-0000-000000000001', 'test@movie.local', '$2a$10$te1W.LSMS7ljZFvTO5TpD.ukbojVLydHGpijJQqAFKA.SBrWqxD5a', NULL, 0, now()),
  ('90000000-0000-0000-0000-000000000002', 'admin@movie.local', '$2a$10$te1W.LSMS7ljZFvTO5TpD.ukbojVLydHGpijJQqAFKA.SBrWqxD5a', NULL, 1, now());

COMMIT;
