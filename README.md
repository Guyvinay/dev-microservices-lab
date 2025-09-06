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

#### **dev-auth**
- Central authentication & identity service.
- Handles:
    - User login
    - Tenant/org management
    - Token generation (JWT or opaque)
- Provides:
    - Token introspection API
    - User info API
- Manages multi-tenant claims in tokens.

#### **dev-takeaway**
- Business microservice (domain-driven service).
- Secured using **dev-auth-starter**.
- Depends on **dev-utility** for shared DTOs and gRPC stubs.
- Publishes/consumes events via RabbitMQ/Kafka.
- Uses **dev-starter** for infra configs.

#### **dev-revised**
- Sandbox microservice for integration & communication testing.
- Connects with **dev-takeaway** via RabbitMQ/gRPC.
- Used to validate:
    - Cross-service security
    - Messaging
    - Multi-tenant context

---

### 2. Starters (Reusable Libraries)

#### **dev-auth-starter**
- Provides request filters for JWT validation.
- Populates `SecurityContext` + `TenantContext`.
- Handles authentication exceptions globally.
- Enables plug-and-play security across services.

#### **dev-utility**
- Shared DTOs, gRPC stubs, and serialization utilities.
- Provides gRPC interceptors for tenant/auth context propagation.
- Central place for **common contracts**.

#### **dev-starter**
- Infra starter including:
    - Elasticsearch configuration
    - RabbitMQ configuration
    - Kafka producer/consumer configuration
    - Liquibase configuration (DB migrations)
- Bundles:
    - **dev-auth-starter** (security)
    - **dev-utility** (DTOs, gRPC stubs)
- Provides **context propagation interceptors** (HTTP, gRPC, Rabbit, Kafka).

---

## Communication Workflow

### Authentication Flow
1. Client logs in via **dev-auth** → receives JWT with tenant/org claims.
2. Client calls **dev-takeaway** (or any service) with:
    - `Authorization: Bearer <token>`
    - `X-Tenant-Id: <tenant-id>` (optional if tenant claim in token)
3. **dev-auth-starter** filter validates token & sets:
    - `SecurityContext` (user, roles)
    - `TenantContext` (current tenant)
4. Request flows through service logic with **tenant scoping enforced**.

### Inter-service Communication

#### HTTP
- Headers:  
  `Authorization`, `X-Tenant-Id`, `X-Request-Id`
- Contexts (auth, tenant, tracing) propagated automatically by **starter interceptors**.

#### gRPC
- Metadata carries:  
  `authorization`, `x-tenant-id`, `x-request-id`
- **dev-utility** provides `GrpcAuthTenantInterceptor` to extract & validate.

#### RabbitMQ
- Messages published with headers:  
  `x-tenant-id`, `x-request-id`, `x-principal`
- Consumers validate headers and set contexts before processing.
- Exchange/queue strategies:
    - Shared (headers control tenant)
    - Per-tenant (higher isolation)

#### Kafka
- Topics: `orders.events`, `payments.events` (domain-driven naming).
- Headers: `tenant-id`, `request-id`, `principal`.
- Partitioning may use **tenant-id** for ordering guarantees.

---

## 🗄️ Data & Multi-Tenancy

### Database
- Learning both approaches:
    - **Shared-schema** with `tenant_id` column
    - **Schema-per-tenant**
- **Liquibase** integrated for migrations.
- Tenant-aware migrations possible.

### Elasticsearch
- Option A: **Single index** with `tenantId` field (simpler).
- Option B: **Index-per-tenant** (`orders-tenant-123-v1`) for stronger isolation.

---

## Key Technical Patterns

### Context Management
- **TenantContext**: ThreadLocal holder for tenant.
- **SecurityContext**: Spring’s `SecurityContextHolder` for auth.

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
