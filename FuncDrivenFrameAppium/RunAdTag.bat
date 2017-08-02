cd %~dp0

set CLASSPATH=..\lib\*;.

cd bin

java test.RunTests
pause