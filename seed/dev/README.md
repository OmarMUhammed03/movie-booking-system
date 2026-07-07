# Dev seed data

Run `.\seed\dev\reset-dev-data.ps1` from the repository root to purge local RabbitMQ saga queues and reset local Docker Postgres data for frontend testing.

Test accounts:

- `test@movie.local` / `Test@12345`
- `admin@movie.local` / `Test@12345`

The seed includes movies, halls, shows, generated tickets, and a few reservations for `test@movie.local`.
