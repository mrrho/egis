# EGIS Assessment

## Build from Source
To build the project from sources, simply use Maven:

    mvn clean compile package shade:shade

This will produce an executable jar `egis-1.0.jar` in the `target` directory.

## Execution
The binary can be executed by running the following command:

    java -jar egis-1.0.jar

Ensure you are in the correct directory.
