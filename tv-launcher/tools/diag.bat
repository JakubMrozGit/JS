@echo off
setlocal
rem Diagnostyka dekodera przez ADB. Uruchom z folderu platform-tools albo
rem skopiuj ten plik do tego folderu. Wynik trafia do diag.txt.

set /p IP=Podaj IP dekodera (np. 192.168.1.25): 
set OUT=diag.txt

adb connect %IP%:5555
echo Diagnostyka dekodera %IP% > %OUT%

call :run "Model" "adb shell getprop ro.product.model"
call :run "Wersja Androida" "adb shell getprop ro.build.version.release"
call :run "Data i godzina" "adb shell date"
call :run "Ping 8.8.8.8" "adb shell ping -c 3 8.8.8.8"
call :run "Ping play.google.com" "adb shell ping -c 3 play.google.com"
call :run "Ping miniaturki Play" "adb shell ping -c 3 play-lh.googleusercontent.com"
call :run "Private DNS" "adb shell settings get global private_dns_mode"
call :run "Wylaczone pakiety" "adb shell pm list packages -d"
call :run "Kto obsluguje HOME" "adb shell cmd package query-activities -a android.intent.action.MAIN -c android.intent.category.HOME"
call :run "Aplikacje LEANBACK" "adb shell cmd package query-activities -a android.intent.action.MAIN -c android.intent.category.LEANBACK_LAUNCHER"
call :run "Pakiety canal" "adb shell pm list packages canal"
call :run "Aktywnosci com.canal.box" "adb shell dumpsys package com.canal.box | findstr /i Activity"

echo.
echo Gotowe. Wklej zawartosc pliku %OUT% w rozmowie.
pause
exit /b

:run
echo. >> %OUT%
echo ===== %~1 ===== >> %OUT%
%~2 >> %OUT% 2>&1
exit /b
