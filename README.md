![Java CI with Maven](https://github.com/kristiania/pgr203eksamen-97krihop/workflows/Java%20CI%20with%20Maven/badge.svg?branch=main) | [Github Actions](https://github.com/kristiania/pgr203eksamen-97krihop/actions)

# PGR203 exam HTTP Server 
## Description:
This is a Http-server. The code hosts various documents that build up a project management tool. The tool contains functionality like creating project members, departments and tasks. The user can edit them and assign them to each other. The objects are added to a local database. The server also handles a range of other requests (URL-echo etc), and includes client for creating custom requests.

## How to build and run

####maven
1. run Clean with Maven
2. run Package with Maven
3. Place .jar in folder containing properties file with database values:
    	- Filename: `pgr203.properties`
    	- Properties: `dataSource.url`, `dataSource.username` and `dataSource.password` 
4. Run `java -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -jar target\http-server-1.0-SNAPSHOT.jar` in terminal
5. Open a web browser and go to [http://localhost:8080](http://localhost:8080)
6. *High five!*

####intellij
Alternatively run HttpServer.main and go link listed above.

## Additional features
- Aditional entity relation `department` with possibility to assign to member and delete
- Simultaneous filtering by both member and status
- Thorough abstractions in `AbstractDao`
- Good use of controllers to avoid cluttering in server class
- Delete functionality in relations `member`, `department`, and `task`
- Deleting from database also deletes relevant relations
- Handling request tagets `/` and empty requests
- Handling Norwegian-specific letters - `http://localhost:8080/æøå.html`
- Correct handling and use of a range of HTTP status codes
- Filtering is saved as a static variable and select-menu is dynamically changed based on filtering (after refreshing) 
- Java classes not accessable in browser
- Thorough logging throughout the server (Only logs if database insert is successful)
- Exceptions are catched and logged
- `index.html` contains buttons for quickly resetting the filters and deleting finished tasks

Database Structure
==
![database Structure](docs/database_structure.png)

Server Structure
==
![Server Structure](docs/server_structure.png)

Dao Structure
==
![Dao Structure](docs/dao_structure.png)

ObjectClass Structure
==
![ObjectClass Structure](docs/member_structure.png)

## Arbeidserfaring:
Erfaringen med prosjektet har først og fremst vært lærerik. Vi har tatt i bruk GitHub i svært stor grad, og vi har fått et nytt syn på mulighetene og bruksområdet Git tilbyr. Vi har benyttet oss GitHub issues, der vi havnet på omkring 70 issues. Issues gir oss god oversikt over gjeldende arbeidsoppgaver og har hjulpet oss med å drive utviklingen videre. Vi har vært konsistente i bruk av branches i GitHub. For å unngå å jobbe på samme issue har vi hatt kontinuerlig kommunikasjon innad i gruppen. Deretter lagde vi separate brancher med forståelige og relevante navn som vi jobbet på. Det var veldig greit å kunne forsikre seg om at alt er funksjonelt før man merger til main-branchen. Dette bidro til at det var lite problemer relatert til commits og pushes når vi jobbet samtidig. Utover dette har det vært god kommunikasjon og godt samarbeid blant gruppemedlemmene. Før vi startet satte vi noen rammer relatert til hvordan arbeidet skulle foregå på ryddigst mulig måte. Dette har alle medlemmene opprettholdt. Alle tre gruppemedlemmer var veldig fornøyde med formatet på eksamen, og synes det en bra erfaring å sette sammen alt vi lærte i løpet av semesteret i et større prosjekt.
