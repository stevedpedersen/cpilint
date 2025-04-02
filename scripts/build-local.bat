@REM # Determine absolute path on local machine
$BASE_DIR = Split-Path -Path $MyInvocation.MyCommand.Path -Parent

echo "Setting environment variables..."

$env:CPILINT_HOME = $BASE_DIR

# Check JAVA_HOME and log warnings if necessary
if (-not $env:JAVA_HOME -or $env:JAVA_HOME -notlike "*17*") {
    Write-Host "Warning: JAVA_HOME is not set or does not contain '17': $($env:JAVA_HOME)"
} else {
    Write-Host "Java 17 found at $($env:JAVA_HOME)"
    $env:CPILINT_JAVA_HOME = $env:JAVA_HOME
}

$LAUNCH_SCRIPT = "$env:CPILINT_HOME\scripts\cpilint.bat"

echo "Copying runtime jars... $LAUNCH_SCRIPT"
New-Item -ItemType Directory -Path "$env:CPILINT_HOME\lib" -Force

Copy-Item -Path "$BASE_DIR\build\lib\cpilint-1.0.5.jar" -Destination "$env:CPILINT_HOME\lib\"
Copy-Item -Path "$BASE_DIR\lib\runtime\*.jar" -Destination "$env:CPILINT_HOME\lib\"
Copy-Item -Path "$BASE_DIR\dist-files\xerces-runtime-jars\*.jar" -Destination "$env:CPILINT_HOME\lib\"

echo ""
echo "------ Example Commands ------"
echo ""
echo ".\scripts\cpilint.bat -help"
echo ".\scripts\cpilint.bat -debug -output json -rules rulesets\default.xml -key .env.json"
echo ".\scripts\cpilint.bat -debug -boring -output json -rules rulesets\default.xml -key .env.json -packages SPJPMTests"
echo ".\scripts\cpilint.bat -rules rulesets\default.xml -key .env.json -output json"

@REM @echo off

@REM setlocal

@REM echo "Setting env variables...."
@REM cd d:\workspace\cpilint_project\CPILint_With_JPM\cpilint
@REM set CPILINT_HOME=d:\workspace\cpilint_project\CPILint_With_JPM\cpilint
@REM @REM set CPILINT_JAVA_HOME=%JAVA_17%
@REM cd d:\workspace\cpilint_project\CPILint_With_JPM\cpilint
@REM set CPILINT_JAVA_HOME=C:\Users\Steve\.jdks\jdk-17
@REM cd d:\workspace\cpilint_project\CPILint_With_JPM\cpilint
@REM set LAUNCH_SCRIPT=.\scripts\cpilint.bat

@REM echo "Copying runtime jars...."
@REM cd d:\workspace\cpilint_project\CPILint_With_JPM\cpilint
@REM mkdir -p %CPILINT_HOME%\lib
@REM copy d:\workspace\cpilint_project\CPILint_With_JPM\cpilint\build\lib\cpilint-1.0.5.jar %CPILINT_HOME%\lib\
@REM cd d:\workspace\cpilint_project\CPILint_With_JPM\cpilint
@REM copy lib\runtime\*.jar %CPILINT_HOME%\lib\
@REM cd d:\workspace\cpilint_project\CPILint_With_JPM\cpilint
@REM copy dist-files\xerces-runtime-jars\*.jar %CPILINT_HOME%\lib\


@REM cd d:\workspace\cpilint_project\CPILint_With_JPM\cpilint

@REM echo .\scripts\cpilint.bat -help
@REM echo .\scripts\cpilint.bat -debug -rules rulesets\default.xml -key .env.json
@REM echo .\scripts\cpilint.bat -debug -rules rulesets\default.xml -key .env.json -packages SP_Dev_Package
@REM echo .\scripts\cpilint.bat-rules rulesets\default.xml -key .env.json -output json

@REM endlocal
