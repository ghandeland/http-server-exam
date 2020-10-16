# pgr203innlevering2-ghandeland

![Java CI with Maven](https://github.com/kristiania/pgr203innlevering2-ghandeland/workflows/Java%20CI%20with%20Maven/badge.svg)

BESKRIVELSE: Http-server som svarer på simple forespørseler. Kan brukes via nettleser eller HttpClient-klassen i kildekoden. Serveren håndterer parametre, og parser enkle GET- og POST-requests, og gir respons på disse. Den kan også skrive ut forskjellige filer basert på filtype (html/plaintext).På index.html kan skrive inn verdier som lagres som User-objekter, og disse listes ut når serveren for hver Post-request.

KJØRE SERVER: 1.) Bygge Maven package  2) java -jar target\http-client-1.0-SNAPSHOT.jar i terminal

Serveren kjører på port 8080
--> http://localhost:8080/
