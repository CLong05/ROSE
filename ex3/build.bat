@echo off
cd src
javac -d ../bin/ -classpath ../bin  *.java
cd ..
pause
@echo on
