@echo on
set base_dir=%~dp0
set conf_dir=%base_dir%conf\
set JAVA_HOME=%base_dir%jre\
set java_bin=%base_dir%jre\bin\java.exe
set path=%JAVA_HOME%bin

set main_class=main.Main

echo %path%

%java_bin% -cp "conf\;lib\*" main.Main
pause