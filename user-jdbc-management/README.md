# User Management — JDBC, Stored Procedures, Transactions

Original Java 17 / Tomcat 10.1+ application with Servlet controllers, JSP/JSTL views, User model and IUserDAO/UserDAO data layer. It covers activities 510, 511, 524, 525, 526, 527, 528 and 529 in one working application.

1. In a fresh MySQL practice instance, import `database.sql` using the MySQL client (supports DELIMITER). Do not import into a production schema. No existing tables are dropped.
2. Configure the Tomcat process environment: `USER_DB_URL=jdbc:mysql://127.0.0.1:3306/codegym_java?characterEncoding=UTF-8`, `DB_USER=your_user`, `DB_PASSWORD=your_password`. No credentials are committed.
3. Run `mvn clean package`; deploy `target/users.war`; open `http://localhost:8080/users/`.

| Activity | Implementation |
| --- | --- |
| 510 | PreparedStatement CRUD, `users?mode=sql` |
| 511 | Country keyword search and name sorting, in both modes |
| 524 | CallableStatement `get_user_by_id` and `insert_user`, `mode=sp` |
| 525 | Create form permission checkboxes; Transaction button calls addUserTransaction with generated keys, batch inserts, explicit commit/rollback |
| 526 | POST experiment without transaction; intentionally missing UPDATE parameter leaves the two INSERTs committed |
| 527 | POST experiment with transaction; failing UPDATE explicitly rolls back; success button commits both INSERTs and salary update |
| 528 | CallableStatement list/update/delete procedures in SP mode |
| 529 | Invalid permission 9999 creates an actual FK error after inserting a user; transaction rolls back and verifies no user remains |

Experiments append uniquely named fictional Employee rows to a dedicated training table; they do not DROP or recreate tables. DDL is outside transaction boundaries. Mutation experiments use POST with CSRF rather than unsafe GET. Real SQL errors are logged server-side, not printed to users. Inputs are validated; SQL uses bound values; sort fragments are fixed; JSP text is escaped; deletion has a confirmation page and CSRF-protected POST; successful CRUD uses PRG.

`mvn test` runs eight real-MySQL integration tests when USER_DB_URL is set; otherwise they are explicitly skipped. Tests cover prepared and stored CRUD, country/sort, atomic user+permissions, FK rollback, partial writes without transaction, rollback with transaction, and successful commit. Run only on a dedicated practice database, as tests intentionally add demonstration Employee records.

Implementation and verification performed with Codex assistance according to CodeGym requirements. No fabricated learner prompt history or production data is included.

## Validation — 20 October 2026

Java 17, Tomcat 10.1.60, dedicated MySQL 26.7 schema. MVC: 3 JUnit tests passed. JDBC: 8 integration tests passed, 0 skipped. Both WAR builds succeeded. The browser rendered all JSP pages. Automated HTTP checks: 69/69 passed across MVC customer/product CRUD, prepared/stored user CRUD, country search, CSRF, escaped output, 400/404 responses, and transaction experiments (partial commit, rollback, full commit, invalid permission FK rollback).

Reproduce HTTP checks after deploying both WARs on localhost port 18091: run the parent verify-mvc-jdbc-http.py with Python 3. It uses fictional test data and removes its CRUD records; transaction demonstrations intentionally retain Employee rows. No credentials or machine-specific connection files are published.
