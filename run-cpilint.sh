#!/usr/bin/env bash

# Define clearly where your CPILint jar and dependencies are located
CPILINT_JAR="build/lib/cpilint-1.0.5.jar"
RUNTIME_LIB="lib/runtime/*"
LOGBACK_DIR="logback"
MAIN_CLASS="org.cpilint.CliClient"

# Run CPILint clearly with all passed arguments
java -classpath "$CPILINT_JAR:$RUNTIME_LIB:$LOGBACK_DIR" "$MAIN_CLASS" "$@"


# chmod +x cpilint-runner.sh
# ./cpilint-runner.sh -rules rules.xml -key .env.trial -output json -packages Experiments

