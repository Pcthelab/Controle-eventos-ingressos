@echo off
chcp 65001 > nul
echo ========================================================
echo   Controle de Eventos e Venda de Ingressos
echo ========================================================

if not exist bin mkdir bin

echo [1/2] Compilando arquivos Java...
javac -encoding UTF-8 -d bin src\br\com\eventos\interfaces\*.java src\br\com\eventos\exception\*.java src\br\com\eventos\model\*.java src\br\com\eventos\view\*.java src\br\com\eventos\*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] Falha na compilacao do projeto.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Iniciando o programa...
java -cp bin br.com.eventos.Principal