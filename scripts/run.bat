@echo off

setlocal

echo "Setting env variables...."
cd d:\workspace\cpilint_project\CPILint_With_JPM\cpilint
set CPILINT_HOME=d:\workspace\cpilint_project\CPILint_With_JPM\cpilint
@REM set CPILINT_JAVA_HOME=%JAVA_17%
cd d:\workspace\cpilint_project\CPILint_With_JPM\cpilint
set CPILINT_JAVA_HOME=C:\Users\Steve\.jdks\jdk-17
cd d:\workspace\cpilint_project\CPILint_With_JPM\cpilint
set LAUNCH_SCRIPT=.\scripts\cpilint.bat

echo "Copying runtime jars...."
cd d:\workspace\cpilint_project\CPILint_With_JPM\cpilint
mkdir -p %CPILINT_HOME%\lib
copy d:\workspace\cpilint_project\CPILint_With_JPM\cpilint\build\lib\cpilint-1.0.5.jar %CPILINT_HOME%\lib\
cd d:\workspace\cpilint_project\CPILint_With_JPM\cpilint
copy lib\runtime\*.jar %CPILINT_HOME%\lib\
cd d:\workspace\cpilint_project\CPILint_With_JPM\cpilint
copy dist-files\xerces-runtime-jars\*.jar %CPILINT_HOME%\lib\


cd d:\workspace\cpilint_project\CPILint_With_JPM\cpilint

echo .\scripts\cpilint.bat -help
echo .\scripts\cpilint.bat -debug -rules dist-files\samples\misc-rules.xml -key .env.trial
echo .\scripts\cpilint.bat -debug -rules dist-files\samples\misc-rules.xml -key .env.trial -packages Experiments
echo .\scripts\cpilint.bat-rules dist-files\samples\misc-rules.xml -key .env.trial -output json

endlocal
