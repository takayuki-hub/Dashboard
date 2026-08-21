@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0
@rem

@echo off
setlocal

set DIRNAME=%~dp0
set APP_HOME=%DIRNAME%

for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

if not defined JAVA_HOME (
    echo ERROR: JAVA_HOME is not set. Please set the JAVA_HOME variable in your environment to match the location of your Java installation.
    exit /b 1
)

set JAVA_EXE=%JAVA_HOME%\bin\java.exe

if not exist "%JAVA_EXE%" (
    echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%. Please set the JAVA_HOME variable in your environment to match the location of your Java installation.
    exit /b 1
)

set CLASSPATH=

"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" -jar "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" %*

endlocal
