# Money Transfer System

## Requirements

Design and implement a RESTful API (including data model and the backing implementation) for money transfers between internal users/accounts.

Explicit requirements:
* keep it simple and to the point (e.g. no need to implement any authentication, assume the APi is invoked by another internal system/service)
* use whatever frameworks/libraries you like (except Spring, sorry!) but don't forget about the requirement #1
* the datastore should run in­memory for the sake of this test
* the final result should be executable as a standalone program (should not require a pre­installed container/server)
* demonstrate with tests that the API works as expected

Implicit requirements:
* the code produced by you is expected to be of good quality.
* there are no detailed requirements, use common sense.

## Installation

Use '**gradlew fatJar**' to build a standalone jar file, after that you can simply run application using '**java -jar mts-1.0-SNAPSHOT.jar**'

## Tests

Use '**gradlew check**' to run unit & integration tests

## License

Apache License Version 2.0