@echo off
set JAVA_HOME=C:\Program Files\Apache NetBeans\jdk
set PATH=%JAVA_HOME%\bin;%PATH%
cd "D:\USUARIOS\CAHR MASTER RACE\Escritorio\vaina progra\ProyectoGestionLab"
java -cp target/classes;target/dependency/* com.labcontrol.app.Main