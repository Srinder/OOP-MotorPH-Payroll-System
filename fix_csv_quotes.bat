@echo off
REM Fix employee_logins.csv by removing malformed quoted entries (lines 37-38)
REM This file should be run from the project directory

setlocal enabledelayedexpansion

set "csvFile=data\employee_logins.csv"

REM Read the file and keep only first 36 lines
for /f "tokens=1,* delims=:" %%A in ('findstr /n "^" "%csvFile%"') do (
    if %%A LEQ 36 (
        echo.%%B
    )
) > "%csvFile%.tmp"

REM Replace original with fixed version
move /Y "%csvFile%.tmp" "%csvFile%"

echo Fixed employee_logins.csv - removed malformed entries (IDs 10036-10037)
pause
