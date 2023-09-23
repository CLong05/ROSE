@echo off
REM Please adjust JFLEX_HOME and JFLEX_VERSION to suit your needs
REM (please do not add a trailing backslash)

set JAVA_HOME=D:\Java JDK
set JFLEX_HOME=D:\jflex-1.8.2
if not defined JFLEX_HOME set JFLEX_HOME=C:\JFLEX
if not defined JFLEX_VERSION set JFLEX_VERSION=1.8.2

java -Xmx128m -jar "%JFLEX_HOME%\lib\jflex-full-%JFLEX_VERSION%.jar" %*
