@echo off

start "" /min cmd /c "cd /d C:\Users\lacru\Documents\CryptoTracker\backend && mvn spring-boot:run"

timeout /t 5 > nul

start "" /min cmd /c "cd /d C:\Users\lacru\Documents\CryptoTracker\frontend && mvn javafx:run"
