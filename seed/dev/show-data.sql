\set ON_ERROR_STOP on

BEGIN;

TRUNCATE TABLE ticket;
TRUNCATE TABLE "show";
TRUNCATE TABLE hall;

INSERT INTO hall (id, name, total_seats, row_count, seats_per_row, screen_type, created_at)
VALUES
  ('20000000-0000-0000-0000-000000000001', 'Hall A - Dolby Atmos', 40, 5, 8, 'Dolby Atmos', now()),
  ('20000000-0000-0000-0000-000000000002', 'Hall B - IMAX', 60, 6, 10, 'IMAX Laser', now()),
  ('20000000-0000-0000-0000-000000000003', 'Hall C - VIP', 24, 4, 6, 'VIP Recliner', now());

INSERT INTO "show" (id, movie_id, hall_id, start_time, end_time, price, created_at)
VALUES
  ('30000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', '2026-07-08 18:30:00', '2026-07-08 20:42:00', 15.50, now()),
  ('30000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000003', '2026-07-09 21:15:00', '2026-07-09 23:27:00', 22.00, now()),
  ('30000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000003', '2026-07-08 16:00:00', '2026-07-08 17:58:00', 13.00, now()),
  ('30000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000002', '2026-07-08 20:00:00', '2026-07-08 22:21:00', 17.75, now()),
  ('30000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000001', '2026-07-09 14:30:00', '2026-07-09 16:06:00', 10.50, now()),
  ('30000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000001', '2026-07-10 19:00:00', '2026-07-10 20:44:00', 12.50, now()),
  ('30000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000002', '2026-07-10 21:30:00', '2026-07-10 23:36:00', 16.25, now()),
  ('30000000-0000-0000-0000-000000000008', '10000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000003', '2026-07-11 17:45:00', '2026-07-11 19:51:00', 21.00, now());

CREATE OR REPLACE FUNCTION seed_ticket_uuid(show_uuid uuid, seat text)
RETURNS uuid
LANGUAGE sql
IMMUTABLE
AS $$
  SELECT (
    substr(md5(show_uuid::text || ':' || seat), 1, 8) || '-' ||
    substr(md5(show_uuid::text || ':' || seat), 9, 4) || '-' ||
    substr(md5(show_uuid::text || ':' || seat), 13, 4) || '-' ||
    substr(md5(show_uuid::text || ':' || seat), 17, 4) || '-' ||
    substr(md5(show_uuid::text || ':' || seat), 21, 12)
  )::uuid;
$$;

WITH show_seats AS (
  SELECT s.id AS show_id, s.price, h.row_count, h.seats_per_row
  FROM "show" s
  JOIN hall h ON h.id = s.hall_id
),
seat_grid AS (
  SELECT
    show_id,
    price,
    chr(64 + row_index) || seat_index::text AS seat_number
  FROM show_seats
  CROSS JOIN LATERAL generate_series(1, row_count) AS row_index
  CROSS JOIN LATERAL generate_series(1, seats_per_row) AS seat_index
)
INSERT INTO ticket (id, show_id, seat_number, price, status, created_at)
SELECT
  seed_ticket_uuid(show_id, seat_number),
  show_id,
  seat_number,
  price,
  CASE
    WHEN show_id = '30000000-0000-0000-0000-000000000001' AND seat_number IN ('A1', 'A2') THEN 'BOOKED'
    WHEN show_id = '30000000-0000-0000-0000-000000000001' AND seat_number IN ('B4') THEN 'RESERVED'
    WHEN show_id = '30000000-0000-0000-0000-000000000004' AND seat_number IN ('C5', 'C6', 'D5') THEN 'BOOKED'
    WHEN show_id = '30000000-0000-0000-0000-000000000008' AND seat_number IN ('A1') THEN 'RESERVED'
    ELSE 'AVAILABLE'
  END,
  now()
FROM seat_grid;

COMMIT;
