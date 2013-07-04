${shellHeader}
${shellComment}
@echo off
@setlocal
@setlocal enableextensions enabledelayedexpansion
for /f %%i in ("%0") do set USER_INSTALL=%%~dpi
set ${appHome}=%USER_INSTALL%\\..

java -classpath %${appHome}%\\boot\\${bootJarFileName} -Dclassworlds.conf=%${appHome}%\\etc\\classworlds.conf -Dapp.home=%${appHome}% org.codehaus.plexus.classworlds.launcher.Launcher %*
