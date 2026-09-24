# Quick Start

## Prerequisites

- JDK 17 or later and Maven 3.9 or later.
- Node.js matching `^22.18.0 || >=24.12.0` for the admin frontend.
- Running MySQL and Redis instances.

## Clone and configure

```bash
git clone https://github.com/geoearth-dev/admin-platform.git
cd admin-platform
```

Import `sql/demo-baseline.sql` into a dedicated development or demo database. Configure the database, Redis, and file storage in `admin-server/src/main/resources/application-dev.yml`.

Shared settings are in `application.yml`; production settings are in `application-prod.yml` in the same directory.

## Start the backend

Run from the repository root:

```bash
mvn clean package
java -jar admin-server/target/admin-server-0.1.0-SNAPSHOT.jar --spring.profiles.active=dev
```

The default backend port is `8080`. If the project version changes, use the actual JAR filename under `admin-server/target`. You can also run the main class in IntelliJ IDEA with the `dev` profile enabled.

## Start the frontend

Open another terminal at the repository root:

```bash
cd admin-ui
npm install
npm run dev
```

## Check and build

Run in `admin-ui`:

```bash
npm run type-check
npm run build
```

The frontend build is generated in `admin-ui/dist`. For more details, see the [frontend README](https://github.com/geoearth-dev/admin-platform/blob/main/admin-ui/README.md).
