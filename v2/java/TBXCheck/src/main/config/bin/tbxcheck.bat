@echo off
setlocal EnableDelayedExpansion

REM ** Remove all quotes from enviornment vars and turn them into short names. 
for /f "tokens=1* delims=," %%I in ("%AMP_HOME%") do set SHORT_HOME=%%~dsfI
for /f "tokens=1* delims=," %%I in ("%AMP_HOME%\jre") do set SHORT_JAVA=%%~dsfI
for /f "tokens=1* delims=," %%I in ("%AMP_HOME%\lib") do set LIB=%%~dsfI

set AMP_VERSION=${project.version}

set JARS=%LIB%\authzgenerator-%AMP_VERSION%.jar
set JARS=!JARS!;%LIB%\authztypes-%AMP_VERSION%.jar
set JARS=!JARS!;%LIB%\connectionpool-%AMP_VERSION%.jar
set JARS=!JARS!;%LIB%\dbconn-%AMP_VERSION%.jar
set JARS=!JARS!;%LIB%\amp-common-%AMP_VERSION%.jar

set JARS=!JARS!;%LIB%\args4j-2.0.8.jar
set JARS=!JARS!;%LIB%\log4j-1.2.13.jar
set JARS=!JARS!;%LIB%\antlr-runtime-3.1.3.jar
set JARS=!JARS!;%LIB%\commons-digester-1.8.jar
set JARS=!JARS!;%LIB%\commons-dbcp-1.2.2.jar
set JARS=!JARS!;%LIB%\commons-pool-1.3.jar
set JARS=!JARS!;%LIB%\commons-collections-3.2.jar
set JARS=!JARS!;%LIB%\commons-logging-1.1.jar
set JARS=!JARS!;%LIB%\commons-beanutils-core.jar

set JARS=!JARS!;%LIB%\postgresql-8.2-505.jdbc4.jar
set JARS=!JARS!;%LIB%\jtds-1.2.5.jar
set JARS=!JARS!;%LIB%\ojdbc14.jar


%SHORT_JAVA%\bin\java -classpath %JARS% com.avocent.amp.authorization.generator.Main %1 %2 %3 %4 %5 %6 %7 %8 %9


