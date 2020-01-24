timeout 3
CALL gradlew build
timeout 2
XCOPY "build\libs\modid-1.0.jar" "C:\Projects\Plames Project\Minecraft Server 1.12.2\mods\modid-1.0.jar" /f /i /y /s 
XCOPY "build\libs\modid-1.0.jar" "C:\Users\stepa\Desktop\MultiMC-\instances\1.12.2\.minecraft\mods\modid-1.0.jar" /f /i /y /s 
timeout 1
CD "C:\Projects\Plames Project\Minecraft Server 1.12.2"
CALL "start.bat"