# tv-launcher

Prosty, lekki launcher na Android TV (bez zewnętrznych bibliotek, jeden ekran).
Ekran główny pokazuje kafelki zainstalowanych aplikacji z listy `FEATURED`
w `MainActivity.java` (Canal+, Netflix, YouTube, Disney+, Max, TVN Player...),
plus "Wszystkie aplikacje" i "Ustawienia". Nawigacja pilotem (D-pad + OK).

## Budowanie
1. Otwórz folder `tv-launcher` w Android Studio i poczekaj na synchronizację Gradle.
2. Build → Build APK(s). Plik: `app/build/outputs/apk/debug/app-debug.apk`.

Z linii poleceń (wymaga Android SDK i `ANDROID_HOME`):
`gradle assembleDebug`

## Instalacja na dekoderze
```
adb connect <IP_dekodera>:5555
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell monkey -p com.jakub.tvlauncher -c android.intent.category.LAUNCHER 1
```
Potem naciśnij Home i wybierz "Launcher" jako domyślny ("Zawsze").

## Cofnięcie
```
adb uninstall com.jakub.tvlauncher
```
Stary launcher nie jest ruszany. Jeśli system dalej wraca do starego,
daj znać, a podamy pakiet do wyłączenia.

## Zmiana listy aplikacji
Edytuj tablicę `FEATURED` w `MainActivity.java` (nazwy pakietów z `adb shell pm list packages`).
