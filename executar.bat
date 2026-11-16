@echo off
chcp 65001 > nul
echo ========================================================
echo   Compilando e Executando - Sistema de Estacionamento
echo ========================================================

if not exist bin (
    mkdir bin
)

echo [1/2] Compilando arquivos Java...
javac -encoding UTF-8 -d bin src\br\com\estacionamento\interfaces\*.java src\br\com\estacionamento\exception\*.java src\br\com\estacionamento\model\*.java src\br\com\estacionamento\view\*.java src\br\com\estacionamento\*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] Falha na compilação do projeto.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Iniciando interface gráfica...
java -cp bin br.com.estacionamento.Principal
