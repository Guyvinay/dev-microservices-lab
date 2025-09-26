
# **Dev-Auth: Authentication & Authorization Service**

## **1. Project Overview**
**Dev-Auth** is a Spring Boot-based authentication and authorization service that provides user, role, privilege, tenant, and organization management. It uses **JWT tokens with Nimbus JOSE** for authentication and secures API access.

## **2. Tech Stack**
- **Spring Boot** (Core Framework)
- **Spring Security** (Authentication & Authorization)
- **Nimbus JOSE + JWT** (Token Handling)
- **Hibernate & JPA** (ORM & Entity Management)
- **Hibernate Envers** (Auditing)
- **Liquibase** (Database Version Control)
- **PostgreSQL** (Database)
- **Docker / Podman** (Containerization)
- **Swagger / OpenAPI** (API Documentation)

## **3. Features & Capabilities**
- **User Management** (Registration, Login, Profile Management)
- **Role-Based Access Control (RBAC)**
- **Privileges & Authorization**
- **Tenant & Organization Multi-Tenancy Support**
- **JWT-Based Authentication with Nimbus JOSE**
- **Auditing with Hibernate Envers**
- **Database Schema Management with Liquibase**

## **4. Entity-Relationship Model & Database Design**
Refer to the ER diagram in `ER_Diagram.md` (Generated using Mermaid.js).

## **5. Authentication Flow**
### **5.1 User Authentication (Login)**
1. The user provides login credentials (username/password).
2. Dev-Auth verifies the credentials against the database.
3. If valid, generates a **JWT Token** using Nimbus JOSE.
4. The token is returned to the client and used for subsequent requests.

### **5.2 JWT Token Handling**
- JWT tokens are generated and signed using Nimbus JOSE.
- Tokens contain user details, roles, and privileges.
- Expiration and validation are managed using Spring Security filters.

## **6. User, Role, Privilege, Tenant, and Organization Management**
### **6.1 User Management**
- Users can register and be assigned roles/privileges.
- Active/Inactive status is maintained.

### **6.2 Role Management**
- Roles define a set of privileges.
- Mapped with users and tenants.

### **6.3 Privileges & Authorization**
- Each role has associated privileges (Read, Write, Execute).
- Secured endpoints use role-based access.

### **6.4 Tenant & Organization Management**
- Multi-tenant system with organization hierarchy.
- Users can belong to multiple tenants.

## **7. Database Management with Liquibase**
- All database schema changes are managed with **Liquibase Changelog XML**.
- Helps maintain version control and rollback capabilities.

## **8. Auditing with Hibernate Envers**
- Tracks changes to entities (Create, Update, Delete).
- Maintains history of important tables.

## **9. API Endpoints (Swagger)**
- API documentation is available using **Swagger / OpenAPI**.
- Accessible at `/swagger-ui.html` after service startup.

## **10. Setup & Deployment Instructions**
### **10.1 Local Development**
1. Clone the repository:
   ```sh
   git clone <repo-url>
   cd dev-auth
   ```
2. Build & Run:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

### **10.2 Running with Docker / Podman**
1. Build the Docker image:
   ```sh
   docker build -t dev-auth .
   ```
2. Run the container:
   ```sh
   docker run -p 8080:8080 dev-auth
   ```

### **10.3 Environment Variables**
| Variable | Description |
|----------|-------------|
| `JWT_SECRET` | Secret key for JWT signing |
| `DB_URL` | Database connection string |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |

## **11. Contribution & Future Enhancements**
- Open for contributions on GitHub.
- Future improvements include OAuth2 support and external authentication providers.

# **Entity Relationship Documentation**

## **1. OrganizationModel**
Stores organization details such as contact information, website, and timestamps.

- **Primary Key:** `organizationId`
- **Fields:**
    - `organizationId (Long)`: Unique identifier for the organization.
    - `contactNumber (Long)`: Contact number of the organization.
    - `tenantId (String)`: Identifier for the tenant associated with the organization.
    - `logoUrl (String)`: URL of the organization's logo.
    - `name (String)`: Name of the organization.
    - `websiteUrl (String)`: Official website URL of the organization.
    - `industryType (String)`: Industry type of the organization.
    - `billingEmail (String)`: Billing email address.
    - `createdAt (Long)`: Timestamp of record creation.
    - `updatedAt (Long)`: Timestamp of last update.
    - `createdBy (String)`: User who created the record.
    - `updatedBy (String)`: User who last updated the record.

### **Relationships:**
- One-to-Many with `OrganizationTenantMapping` (`organizationId`).
- One-to-Many with `UserProfileTenantMapping` (`organizationId`).

---

## **2. OrganizationTenantMapping**
Maps organizations to tenants.

- **Primary Key:** `tenantId`
- **Fields:**
    - `tenantId (String)`: Unique identifier for the tenant.
    - `organizationId (Long)`: Associated organization ID.
    - `tenantName (String)`: Name of the tenant.
    - `createdAt (Long)`: Timestamp of record creation.
    - `updatedAt (Long)`: Timestamp of last update.
    - `active (Boolean)`: Indicates if the tenant is active.
    - `defaultLanguage (String)`: Default language for the tenant.
    - `masterTenant (Boolean)`: Indicates if this is a master tenant.

### **Relationships:**
- Many-to-One with `OrganizationModel` (`organizationId`).

---

## **3. UserProfileModel**
Stores user authentication and profile details.

- **Primary Key:** `id`
- **Fields:**
    - `id (UUID)`: Unique identifier for the user.
    - `username (String)`: Username of the user.
    - `password (String)`: Hashed password of the user.
    - `email (String)`: Email address of the user.
    - `firstName (String)`: First name of the user.
    - `lastName (String)`: Last name of the user.
    - `isActive (Boolean)`: Indicates if the user account is active.
    - `createdAt (Long)`: Timestamp of user account creation.
    - `updatedAt (Long)`: Timestamp of last update.

### **Relationships:**
- One-to-Many with `UserProfileRoleMapping` (`userId`).
- One-to-Many with `UserProfileTenantMapping` (`userId`).

---

## **4. UserProfilePrivilegeModel**
Defines user privileges related to roles.

- **Primary Key:** `id`
- **Fields:**
    - `id (UUID)`: Unique identifier for the privilege record.
    - `roleId (Long)`: Associated role ID.
    - `privilege (Enum)`: Type of privilege assigned.
    - `action (String)`: Specific action associated with the privilege.
    - `area (Enum)`: Area in which the privilege applies.
    - `assignedAt (Long)`: Timestamp of privilege assignment.
    - `assignedBy (Long)`: ID of the user who assigned the privilege.

### **Relationships:**
- Many-to-One with `UserProfileRoleModel` (`roleId`).

---

## **5. UserProfileRoleMapping**
Maps users to roles.

- **Primary Key:** `id`
- **Fields:**
    - `id (UUID)`: Unique identifier for the role mapping.
    - `userId (String)`: ID of the associated user.
    - `roleId (Long)`: ID of the assigned role.
    - `defaultRole (Boolean)`: Indicates if this is the default role for the user.
    - `tenantId (String)`: Tenant ID associated with this role.

### **Relationships:**
- Many-to-One with `UserProfileModel` (`userId`).
- Many-to-One with `UserProfileRoleModel` (`roleId`).

---

## **6. UserProfileTenantMapping**
Maps users to tenants.

- **Primary Key:** `id`
- **Fields:**
    - `id (UUID)`: Unique identifier for the mapping.
    - `tenantId (String)`: ID of the associated tenant.
    - `userId (String)`: ID of the associated user.
    - `createdAt (LocalDateTime)`: Timestamp of mapping creation.
    - `organizationId (String)`: ID of the associated organization.

### **Relationships:**
- Many-to-One with `UserProfileModel` (`userId`).
- Many-to-One with `OrganizationModel` (`organizationId`).

---

## **7. UserProfileRoleModel**
Defines user roles.

- **Primary Key:** `roleId`
- **Fields:**
    - `roleId (Long)`: Unique identifier for the role.
    - `roleName (String)`: Name of the role.
    - `isActive (Boolean)`: Indicates if the role is active.
    - `tenantId (String)`: Tenant ID associated with this role.
    - `adminFlag (Boolean)`: Indicates if the role has admin privileges.
    - `lastUpdated (Long)`: Timestamp of last update.
    - `createdOn (Long)`: Timestamp of creation.
    - `description (String)`: Description of the role.
    - `createdBy (UUID)`: ID of the user who created the role.
    - `modifiedBy (UUID)`: ID of the user who last modified the role.

### **Relationships:**
- One-to-Many with `UserProfilePrivilegeModel` (`roleId`).
- One-to-Many with `UserProfileRoleMapping` (`roleId`).

---

# **Entity Relationship Diagram**

Below is the ER diagram represented using Mermaid.js:

```mermaid
erDiagram
    ORGANIZATION_MODEL {
        Long organizationId PK
        Long contactNumber
        String tenantId
        String logoUrl
        String name
        String websiteUrl
        String industryType
        String billingEmail
        Long createdAt
        Long updatedAt
        String createdBy
        String updatedBy
    }
    
    ORGANIZATION_TENANT_MAPPING {
        String tenantId PK
        Long organizationId FK
        String tenantName
        Long createdAt
        Long updatedAt
        Boolean active
        String defaultLanguage
        Boolean masterTenant
    }

    USER_PROFILE_MODEL {
        UUID id PK
        String username
        String password
        String email
        String firstName
        String lastName
        Boolean isActive
        Long createdAt
        Long updatedAt
    }
    
    USER_PROFILE_PRIVILEGE_MODEL {
        UUID id PK
        Long roleId FK
        Enum privilege
        String action
        Enum area
        Long assignedAt
        Long assignedBy
    }

    USER_PROFILE_ROLE_MAPPING {
        UUID id PK
        String userId FK
        Long roleId FK
        Boolean defaultRole
        String tenantId
    }

    USER_PROFILE_TENANT_MAPPING {
        UUID id PK
        String tenantId FK
        String userId FK
        LocalDateTime createdAt
        String organizationId FK
    }

    USER_PROFILE_ROLE_MODEL {
        Long roleId PK
        String roleName
        Boolean isActive
        String tenantId
        Boolean adminFlag
        Long lastUpdated
        Long createdOn
        String description
        UUID createdBy
        UUID modifiedBy
    }

    // Relationships
    ORGANIZATION_MODEL ||--o{ ORGANIZATION_TENANT_MAPPING : "has many"
    ORGANIZATION_MODEL ||--o{ USER_PROFILE_TENANT_MAPPING : "has many"
    ORGANIZATION_TENANT_MAPPING }o--|| ORGANIZATION_MODEL : "belongs to"
    
    USER_PROFILE_MODEL ||--o{ USER_PROFILE_ROLE_MAPPING : "has many"
    USER_PROFILE_MODEL ||--o{ USER_PROFILE_TENANT_MAPPING : "has many"
    
    USER_PROFILE_ROLE_MODEL ||--o{ USER_PROFILE_PRIVILEGE_MODEL : "has many"
    USER_PROFILE_ROLE_MODEL ||--o{ USER_PROFILE_ROLE_MAPPING : "has many"

    USER_PROFILE_ROLE_MAPPING }o--|| USER_PROFILE_MODEL : "belongs to"
    USER_PROFILE_ROLE_MAPPING }o--|| USER_PROFILE_ROLE_MODEL : "has role"

    USER_PROFILE_TENANT_MAPPING }o--|| USER_PROFILE_MODEL : "belongs to"
    USER_PROFILE_TENANT_MAPPING }o--|| ORGANIZATION_MODEL : "belongs to"

┌────────────────────────┐
│    OrganizationModel   │
├──────────┬────────────┤
│orgId (PK)│ contactNo  │
│tenantId  │ logoUrl    │
│name      │ websiteUrl │
│industry  │ billingEmail │
│createdAt │ updatedAt  │
│createdBy │ updatedBy  │
└──────────┴────────────┘
        │
        │  (1:M)
        ▼
┌──────────────────────────────┐
│   OrganizationTenantMapping  │
├──────────┬──────────────────┤
│tenantId (PK)│ organizationId│
│tenantName   │ createdAt     │
│updatedAt    │ active        │
│defaultLang  │ masterTenant  │
└──────────┴──────────────────┘
        │
        │  (1:M)
        ▼
┌────────────────────────┐
│   UserProfileTenantMapping  │
├──────────┬────────────────┤
│id (PK)   │ tenantId       │
│userId    │ organizationId │
│createdAt │               │
└──────────┴────────────────┘
        │
        │  (M:1)
        ▼
┌──────────────────────┐
│   UserProfileModel   │
├──────────┬──────────┤
│id (PK)   │ username │
│password  │ email    │
│firstName │ lastName │
│isActive  │ createdAt│
│updatedAt │         │
└──────────┴──────────┘
        │
        │  (1:M)
        ▼
┌──────────────────────────┐
│   UserProfileRoleMapping │
├──────────┬──────────────┤
│id (PK)   │ userId       │
│roleId    │ tenantId     │
│defaultRole │           │
└──────────┴──────────────┘
        │
        │  (M:1)
        ▼
┌────────────────────────┐
│   UserProfileRoleModel │
├──────────┬────────────┤
│roleId (PK) │ roleName │
│isActive    │ tenantId │
│adminFlag   │ createdOn│
│lastUpdated │          │
│description │          │
│createdBy   │ modifiedBy│
└──────────┴────────────┘
        │
        │  (1:M)
        ▼
┌─────────────────────────────┐
│   UserProfilePrivilegeModel │
├──────────┬──────────────────┤
│id (PK)   │ roleId           │
│privilege │ action           │
│area      │ assignedAt       │
│assignedBy│                  │
└──────────┴──────────────────┘


```

> **Note:** This Mermaid diagram will render correctly in environments that support Mermaid.js, such as GitHub, GitLab, and some Markdown editors.