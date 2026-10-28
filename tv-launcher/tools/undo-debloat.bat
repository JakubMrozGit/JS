@echo off
setlocal
rem Wlacza z powrotem pakiety wylaczone przez debloat.bat.

set /p IP=Podaj IP dekodera (np. 192.168.1.25): 
adb connect %IP%:5555

for %%P in (
  com.google.android.tv.bugreportsender
  com.google.android.feedback
  com.android.camera2
  com.android.gallery3d
  com.android.htmlviewer
  com.android.printspooler
  com.android.bluetoothmidiservice
  com.skyworthdigital.producttest
  com.ctc
  com.google.android.play.games
  com.google.android.videos
  com.google.android.youtube.tvmusic
  com.google.android.marvin.talkback
  com.google.android.syncadapters.calendar
  com.yodo1.crossyroad
  com.google.android.backdrop
) do (
  echo Wlaczam %%P
  adb shell pm enable %%P
)

echo.
echo Gotowe.
pause
