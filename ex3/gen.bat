@echo off
cd src
jflex oberon.flex
java -jar ../javacup/java-cup-11b.jar -parser Parser -symbols Symbol -nonterms oberon.cup 
cd ..
pause
@echo on