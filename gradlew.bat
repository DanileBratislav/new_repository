@echo off
setlocal
set GRADLE_VERSION=8.10.2
if not defined GRADLE_USER_HOME set "GRADLE_USER_HOME=%USERPROFILE%\.gradle"
set "DIST_DIR=%GRADLE_USER_HOME%\wrapper\dists\gradle-%GRADLE_VERSION%-bin\copilot"
set "GRADLE_HOME=%DIST_DIR%\gradle-%GRADLE_VERSION%"
set "GRADLE_BIN=%GRADLE_HOME%\bin\gradle.bat"

if not exist "%GRADLE_BIN%" (
  if not exist "%DIST_DIR%" mkdir "%DIST_DIR%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $d='%DIST_DIR%'; $v='%GRADLE_VERSION%'; $z=Join-Path $d ('gradle-'+$v+'-bin.zip'); if (-not (Test-Path $z)) { Invoke-WebRequest ('https://services.gradle.org/distributions/gradle-'+$v+'-bin.zip') -OutFile $z }; Expand-Archive -LiteralPath $z -DestinationPath $d -Force"
  if errorlevel 1 exit /b 1
)

call "%GRADLE_BIN%" %*
exit /b %ERRORLEVEL%
