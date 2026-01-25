# Email Sending Microservice Workflow

This document describes the workflow for sending emails using the **Email Microservice** in our ecosystem. The design follows an **event-driven architecture** and ensures clean separation of concerns, scalability, and reliability.

---

## Overview

The workflow involves three main services:

1. **dev-auth-server** – Prepares email requests and initiates sending.
2. **dev-integration** – Owns the email persistence (Elasticsearch) and acts as a central coordinator.
3. **dev-email-server** – Responsible for sending emails asynchronously and publishing status updates.

---

## Workflow Steps

1. **Email Preparation (dev-auth-server)**
    - Creates an `EmailDocument` containing email metadata (recipient, template, variables, category).
    - Generates a unique `eventId` for idempotency and correlation.
    - Sends the `EmailDocument` to **dev-integration** via synchronous gRPC call.
    - **dev-integration** saves the email with `status = PENDING` in Elasticsearch.

2. **Send Command (dev-auth-server → RMQ)**
    - After successful save, dev-auth-server publishes an `EmailSendEvent` to RabbitMQ (`email.send.exchange`).
    - This event contains all information required by **dev-email-server** to send the email (recipient, subject, template, variables, attachments, etc.).

3. **Email Sending (dev-email-server)**
    - Consumes `EmailSendEvent` from RabbitMQ asynchronously.
    - Sends the email via the configured provider (SMTP, SES, etc.), applying retry, idempotency, and throttling mechanisms.
    - After sending (success or failure), **dev-email-server** publishes an `EmailStatusEvent` to RabbitMQ (`email.status.exchange`).

4. **Status Update (dev-integration)**
    - Listens to `EmailStatusEvent`.
    - Updates the corresponding `EmailDocument` in Elasticsearch with `status = SENT/FAILED`, along with error messages and delivery metrics.
    - Ensures **dev-integration** remains the single source of truth for email lifecycle.

---

## Architecture Diagram

```mermaid
flowchart LR
    A[dev-auth-server] -->|gRPC: Save EmailDocument| B[dev-integration]
    A -->|RMQ: EmailSendEvent| C[dev-email-server]
    C -->|Send Email| External[Email Provider]
    C -->|RMQ: EmailStatusEvent| B
    B -->|ES Update| ES[Elasticsearch]

