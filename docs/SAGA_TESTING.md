# Movie Booking Saga - Postman Testing Guide

## Overview

This project implements a **choreography-based saga pattern** using RabbitMQ for distributed transaction management across microservices. The saga orchestrates the movie ticket booking flow: **Reservation → Ticket Reservation → Payment → Confirmation/Compensation**.

## Architecture

### Services Involved

| Service | Port | Role in Saga |
|---------|------|--------------|
| **API Gateway** | 8080 | Entry point, routes requests to downstream services |
| **Auth Service** | 8081 | User authentication, JWT token issuance |
| **Movie Service** | - | Movie catalog management |
| **Show Service** | - | Show schedules, ticket inventory, seat management |
| **Reservation Service** | - | Saga orchestrator, reservation state machine |
| **Payment Service** | - | Stripe checkout, payment webhook handling |

### RabbitMQ Topology

```
Exchanges:
  reservation.events  → routing keys: reservation.created
  ticket.events       → routing keys: ticket.reserved, ticket.reservation.failed
  payment.events      → routing keys: payment.succeeded, payment.failed

Queues:
  reservation.update.queue  ← bound to ticket.events + payment.events
  ticket.update.queue       ← bound to reservation.events + payment.events
  payment.process.queue     ← bound to ticket.events (ticket.reserved)
```

## Saga Flow Diagram

```
┌─────────────┐     reservation.created      ┌──────────────┐
│   Client    │ ────────────────────────────> │  Reservation │
│             │                               │   Service    │
└─────────────┘                               └──────┬───────┘
                                                    │
                                                    │ publishes
                                                    ▼
                                          ┌──────────────────┐
                                          │   Show Service    │
                                          │  (TicketSagaListener) │
                                          └────────┬─────────┘
                                                   │
                                    ┌──────────────┼──────────────┐
                                    │              │              │
                                    ▼              ▼              ▼
                            ticket.reserved   ticket.failed   (no event)
                                    │              │
                                    │              │
                                    ▼              ▼
                          ┌──────────────┐  ┌──────────────┐
                          │ Payment Service│  │ Reservation   │
                          │               │  │ (cancels)     │
                          └──────┬───────┘  └──────────────┘
                                 │
                          ┌──────┴───────┐
                          │              │
                    payment.succeeded  payment.failed
                          │              │
                          ▼              ▼
                  ┌──────────────┐  ┌──────────────┐
                  │ Show Service  │  │ Show Service  │
                  │ (books tickets│  │ (releases     │
                  │  BOOKED)      │  │  tickets)     │
                  └──────┬───────┘  └──────┬───────┘
                         │                 │
                         ▼                 ▼
                  ┌──────────────┐  ┌──────────────┐
                  │ Reservation  │  │ Reservation  │
                  │ (confirms)   │  │ (cancels)    │
                  └──────────────┘  └──────────────┘
```

## Saga States

| State | Description |
|-------|-------------|
| `PENDING` | Reservation created, waiting for ticket reservation |
| `CONFIRMED` | Payment succeeded, tickets booked |
| `CANCELLED` | Ticket reservation failed OR payment failed (compensation) |

## Ticket States

| State | Description |
|-------|-------------|
| `AVAILABLE` | Ticket is available for booking |
| `RESERVED` | Ticket is temporarily held during saga |
| `BOOKED` | Payment confirmed, ticket is finalized |
| `RELEASED` | Compensation executed, ticket returned to pool |

## Postman Collection

Import [`docs/postman-saga-flow.json`](docs/postman-saga-flow.json) into Postman.

### Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `baseUrl` | API Gateway base URL | `http://localhost:8080` |
| `authToken` | JWT access token (auto-set by Login request) | - |
| `userId` | User UUID (auto-set by Register/Login) | - |
| `movieId` | Movie UUID (auto-set by Create Movie) | - |
| `hallId` | Hall UUID (auto-set by Create Hall) | - |
| `showId` | Show UUID (auto-set by Create Show) | - |
| `ticketIds` | JSON array of ticket UUIDs | `["uuid1", "uuid2"]` |
| `reservationId` | Reservation UUID (auto-set) | - |
| `paymentId` | Payment UUID (auto-set) | - |
| `stripeSessionId` | Stripe checkout session ID (auto-set) | - |

### Collection Structure

#### Folder 0: Setup - Prerequisites
Run these first to create test data:

1. **Register User** - Creates a test user
2. **Login** - Authenticates and stores JWT token
3. **Create Movie** - Creates a movie for the show
4. **Create Hall** - Creates a cinema hall
5. **Create Show** - Schedules a show
6. **Create Tickets** - Generates tickets for the show

#### Folder 1: Saga - Happy Path (Payment Succeeds)
Tests the complete successful flow:

1. **1.1 Create Reservation** - Starts the saga (returns 202 Accepted)
2. **1.2 Wait for Ticket Reservation** - Polls reservation to see price populated
3. **1.3 Create Payment Checkout** - Creates Stripe checkout session
4. **1.4 Simulate Stripe Webhook (Success)** - Mocks `checkout.session.completed` with `paid` status
5. **1.5 Verify Reservation Confirmed** - Checks reservation status is `CONFIRMED`
6. **1.6 Verify Tickets are Booked** - Checks ticket status is `BOOKED`

#### Folder 2: Saga - Failure Path (Payment Fails)
Tests compensation when payment fails:

1. **2.1 Create Reservation** - Starts the saga
2. **2.2 Create Payment Checkout** - Creates Stripe checkout session
3. **2.3 Simulate Stripe Webhook (Failed)** - Mocks `checkout.session.completed` with `unpaid`/`expired` status
4. **2.4 Verify Reservation Cancelled** - Checks reservation status is `CANCELLED`
5. **2.5 Verify Tickets Released** - Checks ticket status is `AVAILABLE`

#### Folder 3: Saga - Ticket Reservation Failure
Tests immediate compensation when tickets can't be reserved:

1. **3.1 Create Reservation with Invalid Tickets** - Uses non-existent ticket IDs
2. **3.2 Verify Reservation Cancelled** - Checks reservation status is `CANCELLED`

#### Folder 4: Verification - List Reservations
Lists all reservations for the current user.

## How to Test

### Prerequisites

1. Start infrastructure:
   ```bash
   docker-compose up -d redis rabbitmq postgres
   ```

2. Start all services (or at minimum: api-gateway, auth-service, movie-service, show-service, reservation-service, payment-service)

3. Ensure RabbitMQ management UI is accessible at `http://localhost:15672` (guest/guest)

### Running the Happy Path

1. Import the collection into Postman
2. Create a new environment with `baseUrl = http://localhost:8080`
3. Run **Folder 0** sequentially (Setup)
4. Run **Folder 1** sequentially (Happy Path)
5. Observe the saga events in RabbitMQ management UI

### Running the Failure Path

1. After Setup, run **Folder 2** sequentially
2. Observe compensation events in RabbitMQ

### Running the Ticket Failure Path

1. After Setup, run **Folder 3** sequentially
2. This tests the case where `ticketService.reserveTickets()` throws an exception

## RabbitMQ Event Monitoring

Monitor the saga progress in RabbitMQ Management UI (`http://localhost:15672`):

1. Go to **Exchanges** tab
2. Click on `ticket.events` exchange
3. View **Publish** tab to see `ticket.reserved` or `ticket.reservation.failed` events
4. Click on `payment.events` exchange
5. View **Publish** tab to see `payment.succeeded` or `payment.failed` events

## Key Implementation Details

### Reservation Service
- Creates reservation with `PENDING` status
- Publishes `reservation.created` event to `reservation.events` exchange
- Listens on `reservation.update.queue` for:
  - `ticket.reserved` → updates `totalPrice`
  - `ticket.reservation.failed` → cancels reservation
  - `payment.succeeded` → confirms reservation
  - `payment.failed` → cancels reservation

### Show Service (TicketSagaListener)
- Listens on `ticket.update.queue` for:
  - `reservation.created` → calls `ticketService.reserveTickets()`
    - Success: publishes `ticket.reserved` with `totalPrice`
    - Failure: publishes `ticket.reservation.failed` with `reason`
  - `payment.succeeded` → calls `ticketService.bookTickets()` (RESERVED → BOOKED)
  - `payment.failed` → calls `ticketService.releaseTickets()` (RESERVED → AVAILABLE)

### Payment Service
- Listens on `payment.process.queue` for `ticket.reserved`
  - Creates/updates `PENDING` payment record
- `POST /api/payments/checkout` creates Stripe checkout session
- `POST /api/payments/webhook` receives Stripe events:
  - `checkout.session.completed` with `paid` → publishes `payment.succeeded`
  - `checkout.session.completed` with `unpaid`/`expired` → publishes `payment.failed`
  - `checkout.session.expired` → publishes `payment.failed`

## Notes

- The saga uses **choreography** (no central orchestrator). Each service reacts to events independently.
- All inter-service communication happens via RabbitMQ, not direct HTTP calls.
- The `ReservationSagaEvent` is the shared contract (defined in `shared-lib`).
- Idempotency is handled by checking current state before transitions (e.g., `confirmIfStillPending`, `cancelIfStillPending`).
- The payment webhook requires a valid Stripe signature in production. For testing, you can mock the webhook directly.
