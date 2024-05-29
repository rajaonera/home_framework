@echo off
setlocal enabledelayedexpansion

REM Chemin vers le projet test (à adapter)
set "PROJET_DIR=C:\ITU\Semester 6\naina\Sprint\home_framework\tests"

REM Nom du war
set "WAR_NAME=test-webapp.war"

REM Chemin Tomcat webapps
set "CATALINA_HOME=C:\Program Files\Apache Software Foundation\Tomcat 9.0"
set "TOMCAT_WEBAPPS=%CATALINA_HOME%\webapps"

REM Nettoyer ancien WAR et dossier déployé
if exist "%TOMCAT_WEBAPPS%\%WAR_NAME%" (
    echo Suppression de l'ancien WAR...
    del /q "%TOMCAT_WEBAPPS%\%WAR_NAME%"
)
if exist "%TOMCAT_WEBAPPS%\test-webapp" (
    echo Suppression de l'ancien dossier déployé...
    rmdir /s /q "%TOMCAT_WEBAPPS%\test-webapp"
)

REM Aller dans dossier projet
cd /d "%PROJET_DIR%"

REM Création du WAR (depuis le contenu du dossier, pas depuis un dossier parent)
echo Création du WAR...
jar -cvf "%WAR_NAME%" *

REM Copier le WAR dans Tomcat
echo Copie du WAR vers Tomcat...
copy /y "%WAR_NAME%" "%TOMCAT_WEBAPPS%\"

echo Déploiement terminé.
echo Redémarre Tomcat pour prendre en compte le nouveau WAR si nécessaire.

pause
endlocal
