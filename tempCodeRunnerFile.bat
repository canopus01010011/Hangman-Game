@echo off
set JAVA_FX_LIB=C:\javafx-sdk-21.0.6\lib
set SRC_DIR=src
set OUT_DIR=out
set RES_DIR=resources

cd C:\Users\souhe\OneDrive\Desktop\my projects\Java+JavaFX\hangmanGame
echo Compiling...
javac -d %OUT_DIR% --module-path "%JAVA_FX_LIB%" --add-modules javafx.controls,javafx.fxml %SRC_DIR%\application\*.java

if %ERRORLEVEL% neq 0 (
    echo Compilation failed.
    pause
    exit /b
)

echo Copying resources...
xcopy /E /Y %RES_DIR%\* %OUT_DIR%\

echo Running...
java --module-path "%JAVA_FX_LIB%" --add-modules javafx.controls,javafx.fxml -cp %OUT_DIR% application.Main
pause
