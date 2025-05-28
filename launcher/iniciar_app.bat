@echo off

REM ✅ Ejecutar backend minimizado
start "" /min cmd /c "cd /d C:\Users\lacru\Documents\CryptoTracker\backend && mvn spring-boot:run"

REM Esperar 5 segundos
timeout /t 5 > nul

REM ✅ Ejecutar frontend minimizado
start "" /min cmd /c "cd /d C:\Users\lacru\Documents\CryptoTracker\frontend && mvn javafx:run"
