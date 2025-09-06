# Project Overview: Multi-Tenant Microservice Ecosystem (Learning Project)

## Goal
This project is a **learning-oriented microservice architecture** built with **Spring Boot**, designed to experiment with:

- Authentication & authorization with **JWT/OAuth2**.
- **Custom Spring Boot starters** for reusable infrastructure (security, messaging, observability).
- **Multi-tenancy** at API, messaging, and data levels.
- Service-to-service communication via **HTTP, gRPC, RabbitMQ, Kafka**.
- Operational concerns like:
    - Liquibase migrations
    - Elasticsearch indexing
    - Context propagation
    - Secure inter-service communication

The ultimate aim is to **simulate real-world production architecture** for future professional use.

---

## Components

### 1. Microservices

#### **dev-auth-server**
- Central authentication & identity service.
- Handles:
    - User login
    - Tenant/org management
    - Roles & permissions
    - Token signing & validation (JWT or opaque)
- Provides:
    - Token introspection API
    - User info API
- Manages multi-tenant claims in tokens.

#### **dev-sandbox**
- Experimental service for learning **communication patterns**:
    - RabbitMQ
    - Kafka
    - gRPC
    - HTTP APIs
- Secured using `dev-auth-starter` (delegates auth/role checks to `dev-auth-server`).
- Uses annotations/AOP for authorization enforcement.
- Depends on `dev-shared-utility` for shared DTOs and gRPC stubs.

#### **dev-integration**
- Sandbox service for **integration and end-to-end testing**.
- Connects with `dev-sandbox` via RabbitMQ/gRPC.
- Validates:
    - Cross-service authentication
    - Multi-tenant context propagation
    - Messaging patterns

---

### 2. Starters & Shared Libraries

#### **dev-auth-starter**
- Provides filters/interceptors for JWT validation.
- Populates `SecurityContext` and `TenantContext`.
- Handles authentication exceptions globally.
- Plug-and-play security across services.

#### **dev-infra-starter**
- Provides reusable **infrastructure configurations**:
    - RabbitMQ
    - Kafka
    - Liquibase migrations
    - Multi-tenant context handling
- Bundles `dev-auth-starter` and `dev-shared-utility`.

#### **dev-shared-utility**
- Contains shared resources used across all services:
    - DTOs
    - gRPC messages & stubs
    - Serialization utilities
    - Context propagation interceptors (HTTP, gRPC, RabbitMQ, Kafka)

---

## Communication Workflow

### Authentication Flow
1. Client logs in via **dev-auth-server** → receives JWT with tenant/org claims.
2. Client calls **dev-sandbox** (or any other service) with:
    - `Authorization: Bearer <token>`
    - `X-Tenant-Id: <tenant-id>` (optional if tenant claim is inside token)
3. **dev-auth-starter** validates token & sets:
    - `SecurityContext` (user, roles)
    - `TenantContext` (current tenant)
4. Request flows through service logic with **tenant scoping enforced**.

---

### Inter-service Communication

#### HTTP
- Headers:  
  `Authorization`, `X-Tenant-Id`, `X-Request-Id`
- Contexts (auth, tenant, tracing) propagated automatically by `dev-infra-starter` interceptors.

#### gRPC
- Metadata carries:  
  `authorization`, `x-tenant-id`, `x-request-id`
- `dev-shared-utility` provides `GrpcAuthTenantInterceptor` for context propagation.

#### RabbitMQ
- Messages published with headers:  
  `x-tenant-id`, `x-request-id`, `x-principal`
- Consumers validate headers and set contexts before processing.
- Queue strategies:
    - Shared queues (headers control tenant access)
    - Per-tenant queues (stronger isolation)

#### Kafka
- Topics: `orders.events`, `payments.events` (domain-driven style naming).
- Headers: `tenant-id`, `request-id`, `principal`.
- Partitioning may use `tenant-id` for ordering guarantees.

---

## Data & Multi-Tenancy

### Database
- Exploring both approaches:
    - **Shared schema** with `tenant_id` column
    - **Schema-per-tenant**
- Managed with **Liquibase** migrations.
- Tenant-aware migrations supported.

### Elasticsearch
- Option A: **Single index** with `tenantId` field (simpler).
- Option B: **Index-per-tenant** (`orders-tenant-123-v1`) for stronger isolation.

---

## Key Technical Patterns

### Context Management
- **TenantContext**: ThreadLocal holder for tenant.
- **SecurityContext**: Spring’s `SecurityContextHolder` for authentication.

#### Example Filter (from `dev-auth-starter`)
```java
@Component
public class TokenAndTenantFilter extends OncePerRequestFilter {
    private final JwtVerifier jwtVerifier;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
                                    throws IOException, ServletException {
        try {
            String token = extractBearerToken(request);
            String tenantId = request.getHeader("X-Tenant-Id");

            if (token != null) {
                JwtClaims claims = jwtVerifier.verify(token);
                tenantId = tenantId != null ? tenantId : claims.getClaim("tenant").asString();
                SecurityContextHolder.getContext().setAuthentication(buildAuth(claims));
                TenantContext.setTenantId(tenantId);
            }

            chain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
            TenantContext.clear();
        }
    }
}
