# SecureHub

Minimal Spring Boot 3 (Java 17) app showcasing stateless Spring Security with JWT auth, role-based authorization, method security, and CORS. Uses an in-memory H2 database and seeds an ADMIN user for quick testing.

## Contents

- Quick Start
- API Endpoints
- Testing with curl
- Security Overview
- Troubleshooting
- Build Info

## Quick Start

- Prerequistes: <strong>Java 17+</strong> first run needs internet for dependencies
- Start<br>
  `./gradlew bootRun`

- H2 Console<br>`http://localhost:8080/h2-console`<br>

  - JDBC URL: `jdbc:h2:mem:securitylab`
  - User: `sa` | Password: (empty)

- Port: `8080`

Sample credentials (seeded at `src/main/java/com/cloudpiercer/SecureHub/SecureHubApplication.java:14`)

- Admin
  - Username: `admin`
  - Password: `password123`
  - Role: `ADMIN`
- User
  - Username: `user`
  - Password: `password123`
  - Role: `USER`

## API Endpoints

- Public: `GET /public/ping` (no auth)

- Auth: `POST /auth/login` → `{ "accessToken": "..." }`
- User: `GET /user/me` (requires Bearer token)
- Admin: `GET /admin/stats` (requires ADMIN role)
- CORS demo: `GET /cors-demo/hello` (allows origin `http://localhost:3000`)
- H2 console: `GET /h2-console` (CSRF ignored, frames allowed)

## Testing with curl

1. Public endpoint (no token)

```bash
curl -i http://localhost:8080/public/ping
```

2. Login to obtain a JWT (admin)

```bash
curl -s -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"password123"}'
```

Example response:

```json
{ "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." }
```

Capture the token (requires jq):

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"password123"}' | jq -r .accessToken)
```

No `jq`? Copy the `accessToken` string and replace `$TOKEN` manually below.

3. Protected endpoint

```bash
curl -i http://localhost:8080/user/me \
  -H "Authorization: Bearer $TOKEN"
```

4. Admin-only endpoint

```bash
curl -i http://localhost:8080/admin/stats \
  -H "Authorization: Bearer $TOKEN"
```

Without a token → `401 Unauthorized`. With a non-admin token → `403 Forbidden`.

4b) Show 401 on protected route (no token)

```bash
curl -i http://localhost:8080/user/me
```

5. CORS: preflight and simple request
   Preflight:

```bash
curl -i -X OPTIONS http://localhost:8080/cors-demo/hello \
  -H 'Origin: http://localhost:3000' \
  -H 'Access-Control-Request-Method: GET'
```

Simple CORS GET:

```bash
curl -i http://localhost:8080/cors-demo/hello \
  -H 'Origin: http://localhost:3000'
```

Expect `Access-Control-Allow-Origin: http://localhost:3000`.

Use JDBC URL `jdbc:h2:mem:securitylab`, user `sa`, empty password.

## Security Overview

- Security filter chain — `src/main/java/com/cloudpiercer/SecureHub/config/SecurityConfig.java:1`

  - Stateless: disables CSRF (except H2), sets `SessionCreationPolicy.STATELESS`

  - AuthZ: permits `/public/**`, `/auth/**`, H2; `/admin/**` requires `ROLE_ADMIN`; others authenticated
  - Exceptions: 401 unauthenticated, 403 access denied
  - JWT: adds `JwtAuthFilter` before `UsernamePasswordAuthenticationFilter`
  - Method security: `@EnableMethodSecurity` enables `@PreAuthorize`
  - Headers: frame options `sameOrigin` for H2

- Auth manager & provider — `src/main/java/com/cloudpiercer/SecureHub/config/AuthManagerConfig.java:1`

  - BCrypt `PasswordEncoder`, `DaoAuthenticationProvider` with `UserDetailsService`, `AuthenticationManager`

- Role hierarchy — `src/main/java/com/cloudpiercer/SecureHub/config/RoleHierarchyConfig.java:1`

  - `ROLE_ADMIN > ROLE_USER`

- CORS — `src/main/java/com/cloudpiercer/SecureHub/config/CorsConfig.java:1`

  - Allows origin `http://localhost:3000`, common methods/headers, credentials

- JWT filter — `src/main/java/com/cloudpiercer/SecureHub/filter/JwtAuthFilter.java:1`

  - Extracts/validates Bearer token; sets `SecurityContext`

- JWT service — `src/main/java/com/cloudpiercer/SecureHub/service/JwtService.java:1`

  - Issues HS256 tokens (`sub`, `scope`, 1h expiry); parses/verifies

- User Details — `src/main/java/com/cloudpiercer/SecureHub/service/CustomUserDetailsService.java:1`

  - Maps `AppUser` to Spring Security user

- Domain & persistence — `src/main/java/com/cloudpiercer/SecureHub/model/AppUser.java:1`, `src/main/java/com/cloudpiercer/SecureHub/repository/AppUserRepository.java:1`

- App Bootstrap — `src/main/java/com/cloudpiercer/SecureHub/SecureHubApplication.java:1`

  - Seeds `ADMIN` user at startup

- Config — `src/main/resources/application.yaml:1`
  - H2 (in-memory), JPA `ddl-auto: update`, H2 console, JWT secret, port 8080

## Troubleshooting

- 401 Unauthorized: Missing/invalid/expired token → add `Authorization: Bearer <token>`

- 403 Forbidden: Authenticated but lacks role → `/admin/**` needs `ROLE_ADMIN`
- Tokens change after restart: ensure `security.jwt.secret-base64` is set (already set here)
- CORS blocked: use `http://localhost:3000` or update `CorsConfig`

## Build Info

- Spring Boot and plugins: `build.gradle:1`
- Java toolchain: Java 17 (`build.gradle:13`)
- Dependencies: Spring Web/Security/Data JPA/Validation, JJWT 0.12.x, H2, springdoc OpenAPI
