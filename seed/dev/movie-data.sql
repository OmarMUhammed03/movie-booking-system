\set ON_ERROR_STOP on

BEGIN;

TRUNCATE TABLE movie;

INSERT INTO movie (id, title, description, genre, duration_minutes, poster_url, release_date, created_at)
VALUES
  ('10000000-0000-0000-0000-000000000001', 'Neon Harbor', 'A detective follows a stolen memory through a rain-soaked future city.', 'Sci-Fi', 132, 'https://picsum.photos/seed/neon-harbor/500/750', '2026-06-12', now()),
  ('10000000-0000-0000-0000-000000000002', 'The Last Orchard', 'A family drama about rebuilding an old cinema beside an abandoned apple farm.', 'Drama', 118, 'https://picsum.photos/seed/last-orchard/500/750', '2026-05-22', now()),
  ('10000000-0000-0000-0000-000000000003', 'Midnight Formula', 'Rival engineers race across Europe to recover a missing prototype engine.', 'Action', 141, 'https://picsum.photos/seed/midnight-formula/500/750', '2026-07-03', now()),
  ('10000000-0000-0000-0000-000000000004', 'Tiny Comets', 'Two kids discover that their backyard telescope can bend time by ten seconds.', 'Family', 96, 'https://picsum.photos/seed/tiny-comets/500/750', '2026-04-18', now()),
  ('10000000-0000-0000-0000-000000000005', 'Laugh Track Live', 'A stand-up comic must save opening night when every joke starts coming true.', 'Comedy', 104, 'https://picsum.photos/seed/laugh-track-live/500/750', '2026-03-28', now()),
  ('10000000-0000-0000-0000-000000000006', 'The Glass Tide', 'A marine biologist uncovers a glowing reef and the old secret buried beneath it.', 'Adventure', 126, 'https://picsum.photos/seed/glass-tide/500/750', '2026-06-26', now());

COMMIT;
