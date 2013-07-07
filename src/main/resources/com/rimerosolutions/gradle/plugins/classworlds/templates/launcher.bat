@REM
@REM Copyright 2013 Rimero Solutions, Inc.
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM     http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

${shellHeader}
${shellComment}
@echo off
@setlocal
@setlocal enableextensions enabledelayedexpansion
for /f %%i in ("%0") do set USER_INSTALL=%%~dpi
set ${appHome}=%USER_INSTALL%\\..

java -classpath %${appHome}%\\boot\\${bootJarFileName} -Dclassworlds.conf=%${appHome}%\\etc\\classworlds.conf -Dapp.home=%${appHome}% org.codehaus.plexus.classworlds.launcher.Launcher %*
