# Clínica Médica

## Requisitos

- JDK 25 (LTS)
- Apache Maven 3.9.12 or newer
- MySQL with the schema and sample data in `db/01_schema.sql` and `db/02_carga.sql`

The Maven build compiles the application for Java 25 using `--release 25`.

## Build and validation

From the project root, run:

```sh
mvn clean test-compile
mvn clean test
```

Configure the local MySQL connection in `br.com.entidade.DAO` before running the
desktop application.