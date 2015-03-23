@ echo off
@setlocal enabledelayedexpansion
@set classpath=.
@if not "%JAVA_HOME%" == "" goto gotJdkHome
@goto noJavac

:gotJdkHome
@if not exist "%JAVA_HOME%\lib\tools.jar" goto noJavac
@set classpath=!classpath!;%JAVA_HOME%\lib\tools.jar

:noJavac
@set CURRENT_DIR=%cd%
@cd ..
@set HOME=%cd%
@set classpath=!classpath!;%HOME%\WEB-INF\classes
@cd %CURRENT_DIR%
for %%c in (..\WEB-INF\lib\*) do set classpath=!classpath!;%%c
::@echo %classpath%
@set classpath=%classpath%;
java  aurora.application.admin.ServerAdmin 18080 %HOME%