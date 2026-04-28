@echo off
chcp 65001 > nul
title 教务管理系统 - 前端
echo ========================================
echo   JavaFX 教务管理系统 - 启动中...
echo ========================================
echo.

cd /d "%~dp0"

REM 检查后端是否运行
echo 检查后端服务器...
curl -s --connect-timeout 2 http://localhost:22222/auth/login -H "Content-Type: application/json" -d "{}" >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] 后端服务器未运行!
    echo 请先启动后端服务!
    echo.
)

REM 检查是否安装了Maven
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] 未找到Maven,直接运行JAR...
    java -jar target\java-fx-1.0-SNAPSHOT.jar
) else (
    echo 正在启动前端(Maven模式)...
    echo.
    call mvnw.cmd javafx:run
)

pause
