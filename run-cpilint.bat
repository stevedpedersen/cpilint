@REM java -classpath "build\lib\cpilint-1.0.5.jar;lib\runtime\*;logback" org.cpilint.CliClient -rules rules.xml -key .env.trial -output json -packages Experiments1

@echo off
setlocal enabledelayedexpansion

:: Adjust paths if needed
set CPILINT_JAR=build\lib\cpilint-1.0.5.jar
set RUNTIME_LIB=lib\runtime\*
set LOGBACK_DIR=logback
set MAIN_CLASS=org.cpilint.CliClient

java -classpath "%CPILINT_JAR%;%RUNTIME_LIB%;%LOGBACK_DIR%" %MAIN_CLASS% %*


@REM cpilint-runner.bat -rules rules.xml -key .env.trial -output json -packages Experiments
