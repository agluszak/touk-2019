# Ticket booking app

## Installation:
App requires a postgres DB and proper configuration in application.conf for main and test

## Running:
```sh
sbt run
```
("Production" version - doesn't get cleaned after each run, but not interesting because of empty DB.
 Bring your own migrations!)

## Demo:
```sh
sudo apt install jq
sbt "test:runMain ManualTest"
./demo.sh
```
(Starts with a freshly populated DB)

## Additional assumptions:
-   Example data for tests is static (it is the same every time you run the tests),
    so in order to be able to make reservations which won't be rejected (because of the time limit)
    you must reach into the far future of year 2119...
    (I could have made the data dynamic so that the dates are relative to the current moment
    or used mocks, but I chose the simplest way)
-   For the sake of simplicity all seats being reserved in a row must form an interval

## Things that I know that could be improved:
-   Compiled queries (because it's pain in the ass and I started thinking about switching to plain SQL already...)
-   Validation errors. Right now they are stringly-typed, but I wanted to give some love to my (nonexistent) frontend
-   Magic constants (like the limit of how many minutes before a screening you can book a ticket --
    it wasn't easy to come up with a name for this variable) could be loaded from config