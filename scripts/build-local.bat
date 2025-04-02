@echo off
setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set SCRIPT_PATH=%~f0

:: Remove trailing backslash from directory path
set "BASE_DIR=%SCRIPT_DIR:~0,-1%"
:: Go up one directory to get CPILint home
for %%A in ("%BASE_DIR%") do set "CPILINT_HOME=%%~dpA"
:: Remove trailing backslash
set "CPILINT_HOME=%CPILINT_HOME:~0,-1%"

echo Setting environment variables for CPILint
echo Base directory: %BASE_DIR%
echo CPILint home: %CPILINT_HOME%

:: Check JAVA_HOME
if not defined JAVA_HOME (
    echo Warning: JAVA_HOME is not set.
) else (
    echo Using Java from: %JAVA_HOME%
    set CPILINT_JAVA_HOME=%JAVA_HOME%
)

set LAUNCH_SCRIPT=%BASE_DIR%\cpilint.bat
echo Launch script: %LAUNCH_SCRIPT%

:: Create lib directory if it doesn't exist
if not exist "%CPILINT_HOME%\lib" (
    echo Creating lib directory
    mkdir "%CPILINT_HOME%\lib"
) else (
    echo Lib directory already exists
)

:: Copy JAR files with error handling
echo Copying runtime JAR files...

:: Possible source paths for CPILint JAR
set SOURCES[0]=%CPILINT_HOME%\build\lib\cpilint-1.0.5.jar
set SOURCES[1]=%CPILINT_HOME%\lib\runtime\*.jar
set SOURCES[2]=%CPILINT_HOME%\dist-files\xerces-runtime-jars\*.jar

:: Check and copy each source
for /L %%i in (0,1,2) do (
    set "source=!SOURCES[%%i]!"
    if exist "!source!" (
        echo Copying !source! to %CPILINT_HOME%\lib\
        copy "!source!" "%CPILINT_HOME%\lib\" > nul 2>&1
        if !errorlevel! neq 0 (
            echo Warning: Failed to copy !source!
        ) else (
            echo Successfully copied !source!
        )
    ) else (
        echo Warning: Source not found - !source!
    )
)

echo.
echo ------ Example Commands ------
echo.
echo .\scripts\cpilint.bat -help
echo .\scripts\cpilint.bat -debug -output json -rules rulesets\default.xml -key .env.json
echo .\scripts\cpilint.bat -debug -boring -output json -rules rulesets\default.xml -key .env.json -packages SPJPMTests
echo .\scripts\cpilint.bat -rules rulesets\default.xml -key .env.json -output json

endlocal
