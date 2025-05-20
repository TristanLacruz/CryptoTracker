@echo off
start cmd /k "cd backend && mvn spring-boot:run"
timeout /t 3
start cmd /k "cd frontend && mvn javafx:run"
