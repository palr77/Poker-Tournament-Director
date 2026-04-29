@rem Gradle startup script for Windows
@echo off

set APP_BASE_NAME=%~n0
set APP_HOME=%~dp0
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

if defined JAVA_HOME goto findJavaFromJavaHome

set JAVACMD=java.exe
%JAVACMD% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
exit /b 1

:findJavaFromJavaHome
set JAVACMD=%JAVA_HOME%\bin\java.exe
if exist "%JAVACMD%" goto execute

echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
exit /b 1

:execute
"%JAVACMD%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% ^
  -Dorg.gradle.appname=%APP_BASE_NAME% ^
  -classpath "%CLASSPATH%" ^
  org.gradle.wrapper.GradleWrapperMain %*
