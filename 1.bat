@echo off
chcp 65001 > nul
title 教务管理系统

echo ========================================
echo   教务管理系统 - 启动程序
echo ========================================
echo.

REM 获取当前目录
set BASE_DIR=%~dp0

REM 检查后端是否运行
echo [1/2] 检查后端服务器...
curl -s --connect-timeout 3 http://localhost:22222/auth/login -H "Content-Type: application/json" -d "{}" >nul 2>&1
if %errorlevel% neq 0 (
    echo        后端未运行，正在启动...
    cd /d "%BASE_DIR%java-server"
    start "后端服务" cmd /c "java -DJWT_SECRET_KEY=mySecretKeyForJWTAuthenticationThatIsLongEnoughToBeSecure1234567890 -jar target\java-server-1.0.1-SNAPSHOT.jar"
    echo        等待后端启动...
    timeout /t 15 /nobreak > nul
) else (
    echo        后端已运行
)

REM 启动前端
echo [2/2] 启动前端...
cd /d "%BASE_DIR%java-fx"
start "前端-教务管理系统" cmd /c "call mvnw.cmd javafx:run"

echo.
echo ========================================
echo   系统已启动！
echo   后端: http://localhost:22222
echo   前端: 请查看弹出的窗口
echo ========================================
pause
