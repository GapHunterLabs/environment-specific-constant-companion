# Demo data for screenshots

`OrderClient.java` — `API_URL` flagged (identical to `api.endpoint` in
`application.properties`); `DB_HOST_PORT` flagged (identical to
`db.host` in `application.yml`); `LOCAL_PORT` not flagged (a bare port,
no host context).

## How to get the screenshot

1. `./gradlew runIde` from `environment-specific-constant-companion`,
   open this `demo/` folder as the project.
2. Full Screen, open `OrderClient.java` — warnings should appear on
   `API_URL` and `DB_HOST_PORT` but not on `LOCAL_PORT`.
3. Screenshot with all three fields visible, save into
   `environment-specific-constant-companion/docs/screenshots/`. Close
   the sandbox.
