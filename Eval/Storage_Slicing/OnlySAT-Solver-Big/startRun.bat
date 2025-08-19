@echo off

java -Xms8192M -jar JMH-jmh.jar Paper.Handcraftet.Handcrafted_OnlySATSolver -t 4 -o "%~dp0/jmh-results.csv" -rf CSV