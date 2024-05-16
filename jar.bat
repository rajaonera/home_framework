@echo off
setlocal enabledelayedexpansion

set "dossier_principal=."
set "dossier_temp=temp"

:: Création du dossier temporaire
if not exist "%dossier_temp%" mkdir "%dossier_temp%"

:: Copie des fichiers .java vers le dossier temporaire
for /r "%dossier_principal%" %%f in (*.java) do (
    set "chemin_complet=%%~f"
    set "nom_fichier=%%~nxf"
    copy /Y "%%~f" "%dossier_temp%\%%~nxf" > nul
)

:: Demande du nom de la librairie
set /p projet=Librairie Name:
set "src=%dossier_temp%\*.java"
set "mainPkg=mg"
set "archive=%projet%.jar"

:: Compilation
javac -g -d "%dossier_temp%" %src%
if errorlevel 1 (
    echo Échec de la compilation
    exit /b 1
)

:: Création du .jar
cd %dossier_temp%
echo Main-Class: mg.Main> manifest.txt
jar cfm "%archive%" ..\manifest.txt %mainPkg%
del manifest.txt
cd ..

:: Copie du .jar dans le dossier principal
copy /Y "%dossier_temp%\%archive%" "%dossier_principal%"

:: Copie dans dossier de test
set "destination=..\Test\lib\"
if exist "%destination%%archive%" (
    del /Q "%destination%%archive%"
)
copy /Y "%dossier_temp%\%archive%" "%destination%"

:: Nettoyage
rmdir /S /Q "%dossier_temp%"

endlocal
