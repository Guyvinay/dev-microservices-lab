# Multi-Tenant Microservice Ecosystem (Learning Project)

## Project Purpose
A **learning-focused, production-inspired microservice architecture** built with **Spring Boot** to experiment with real-world backend concerns such as security, multi-tenancy, messaging, and service-to-service communication.

This project is intentionally designed to **mirror enterprise-grade systems** while remaining flexible for experimentation and iteration.

---

## Core Learning Goals

- Authentication & Authorization using **JWT / OAuth2 concepts**
- Designing **custom Spring Boot starters**
- End-to-end **multi-tenancy** (API, messaging, data)
- Secure service-to-service communication:
    - HTTP
    - gRPC
    - RabbitMQ
    - Kafka
- Operational & infrastructure patterns:
    - Liquibase migrations
    - Elasticsearch indexing
    - Context propagation
    - Observability & tracing (future)

---

# System Architecture Overview

All services share:
- Common security model
- Tenant context
- Infrastructure abstractions via starters

---

# Global Platform Capabilities

## Features
- Centralized authentication & token issuance
- Tenant-aware request processing
- Secure internal service calls using **service tokens**
- Unified context propagation across protocols

## Implemented
- JWT-based authentication
- Tenant context propagation (HTTP, RMQ, gRPC)
- Shared infrastructure via custom starters

## To Be Implemented
- OAuth2 Client Credentials flow
- Token introspection & revocation
- Key rotation (JWKS / `kid`)
- mTLS for internal traffic
- Distributed tracing (OpenTelemetry)

---

# Authentication & Authorization Model

## Token Types
- **ACCESS** → User authentication
- **REFRESH** → Token renewal only
- **SERVICE** → Internal service-to-service calls

## Key Principles
- Strict token-type validation
- Service tokens cannot authenticate users
- Refresh tokens cannot access APIs
- Explicit scopes & claims enforcement

---

# Microservices

## dev-auth-server

### Description
Central **authentication and identity service** responsible for user, tenant, and token management.

### Features
- User authentication
- Tenant & organization management
- Role & permission handling
- JWT / service token issuance
- Token validation & introspection

### Implemented
- JWT signing & validation (Nimbus)
- Multi-tenant claims in tokens
- Access / Refresh token separation
- Service token support

### To Be Implemented
- OAuth2 authorization server features
- Token introspection endpoint
- Token revocation / blacklist
- Key rotation (JWKS)
- Admin APIs for tenant & role management

---

## dev-sandbox

### Description
Experimental service to **learn and validate communication patterns** and security enforcement.

### Features
- HTTP APIs
- RabbitMQ consumers & producers
- Kafka producers / consumers
- gRPC server & client

### Implemented
- JWT validation via `dev-auth-starter`
- Tenant context enforcement
- Role-based access control
- Context propagation across protocols

### To Be Implemented
- Fine-grained permission annotations
- Async authorization for messaging
- Circuit breaker & retry strategies
- Observability & metrics

---

## dev-integration

### Description
End-to-end **integration testing service** to validate cross-service flows.

### Features
- Consumes messages from dev-sandbox
- Calls dev-sandbox via gRPC / HTTP
- Validates security & tenancy boundaries

### Implemented
- Service-to-service JWT authentication
- Tenant context propagation
- Messaging-based workflows

### To Be Implemented
- Chaos testing scenarios
- Contract testing (gRPC / events)
- Performance & load testing

---

# Starters & Shared Libraries

## dev-auth-starter

### Description
Plug-and-play **security starter** for all services.

### Features
- JWT validation filters
- SecurityContext population
- TenantContext extraction
- Global auth exception handling

### Implemented
- Access / Service token validation
- Token-type enforcement
- Tenant & user context setup

### To Be Implemented
- OAuth2 resource server support
- Annotation-based permission checks
- Token introspection fallback
- Rate limiting hooks

---

## dev-infra-starter

### Description
Reusable **infrastructure abstraction layer** for microservices.

### Features
- Messaging configuration
- Database & migration support
- Context propagation utilities

### Implemented
- RabbitMQ configuration
- Kafka configuration
- Liquibase multi-tenant support
- Tenant context propagation

### To Be Implemented
- Dead-letter queue strategies
- Retry & backoff policies
- Distributed tracing integration
- Centralized configuration support

---

## dev-shared-utility

### Description
Common utilities & shared contracts across all services.

### Features
- Shared DTOs
- gRPC stubs & proto files
- Serialization helpers
- Context propagation helpers

### Implemented
- Shared DTO models
- gRPC interceptors
- HTTP / RMQ / Kafka context propagation

### To Be Implemented
- Schema registry integration
- Versioned contract management
- Common validation utilities

---

# Security, Authorization & Caching (Extended Design)

This section describes **security enforcement**, **authorization model**, and **Redis usage** across the platform.

---

# Security Architecture

## Security Principles
- **Zero trust between services**
- Every request (HTTP, gRPC, RMQ, Kafka) must carry identity
- Identity is validated **at service boundary**
- No implicit trust based on network location

---

## Authentication Types

### User Authentication
- JWT **ACCESS tokens**
- Issued by `dev-auth-server`
- Contains:
    - User identity
    - Tenant & org info
    - Roles / privileges
- Used for:
    - External API access
    - User-initiated workflows

### Service Authentication
- JWT **SERVICE tokens**
- Issued internally
- Contains:
    - `serviceName`
    - `scopes`
- Used for:
    - Service-to-service calls
    - Public API triggered workflows
- Cannot authenticate as a user

### Refresh Tokens
- JWT **REFRESH tokens**
- Used only for token renewal
- Explicitly blocked from API access

---

# Authorization Model

## Authorization Layers

1. **Endpoint-level authorization**
2. **Business-level authorization (TODO)**
3. **Messaging-level authorization (TODO)**

---

## Annotation-Based Authorization

### `@Requires` Annotation

Used to enforce **fine-grained, declarative authorization** at method.

```java
@Requires(
    match = MatchMode.ALL,
    value = {
        @Requires.Require(
            privilege = Privilege.MANAGE_USERS,
            actions = {
                Action.VIEW_USERS,
                Action.CREATE_USER
            }
        ),
        @Requires.Require(
            privilege = Privilege.VIEW_REPORTS,
            actions = { Action.EXPORT_REPORT }
        )
    }
)
public UserPrivileges getUserPrivileges(UUID userId) {
    return privilegeRepository.findByUserId(userId);
}
```
### Redis Cache Adapter – Usage Overview

The `@RedisCacheAdapter` annotation provides a **declarative way to interact with Redis** using AOP, without writing Redis-specific code in business logic.

---

## Common Usage Patterns

### Read-Through Cache (Default)

Caches method result and returns cached value on subsequent calls.

```java
@RedisCacheAdapter(
    name = "user:privileges",
    ttl = 300,
    log = true,
    operation = RedisCacheOperation.CACHE
)
public UserPrivileges getUserPrivileges(UUID userId) {
    return privilegeRepository.findByUserId(userId);
}
```

# Communication Patterns

## HTTP
**Headers**
- `Authorization`

**Status**
- Implemented: JWT validation & context propagation
- TODO: Rate limiting, API gateway simulation

---

## gRPC
**Metadata**
- `authorization`

**Status**
- Implemented: Security & tenant interceptors
- TODO: Deadline propagation, retries

---

## RabbitMQ
**Message Headers**
- `x-tenant-id`

**Queue Strategy**
- Optional per-tenant queues

**Status**
- Implemented: Context propagation & validation
- TODO: DLQ, retry semantics

---

## Kafka

**Status**
- To be implemented
---

# Data & Multi-Tenancy

## Database
### Approaches
- Shared schema with `tenant_id`
- Schema-per-tenant

### Status
- Implemented: Tenant-aware Liquibase migrations
- TODO: Runtime tenant routing strategies

---

## Elasticsearch
### Strategies
- Index-per-tenant

### Status
- TODO: Index lifecycle management, reindexing strategy

---

# Long-Term Roadmap

- OAuth2 Authorization Server
- Client Credentials Flow
- OpenTelemetry tracing
- Configuration SAML authentication
- Chat improvement (like user offline, sessions when ms restarts)

---

## Summary
This project acts as a **hands-on sandbox for real-world microservice architecture**, focusing on **security, multi-tenancy, and distributed systems** while keeping extensibility and correctness as first-class goals.
