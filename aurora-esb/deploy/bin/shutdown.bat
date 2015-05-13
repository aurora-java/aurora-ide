@ echo off
setlocal enabledelayedexpansion
::set classpath=.;%JAVA_HOME%/lib/tools.jar;E:\workspace\additional\web\WEB-INF\lib\*
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
@cd %CURRENT_DIR%
@for %%c in (..\WEB-INF\lib\*.jar) do set classpath=!classpath!;%%c
::@echo %classpath%
@set classpath=%classpath%;
java  aurora.application.admin.StopCommand 18080
echo "³É¹¦¹Ø±Õ"