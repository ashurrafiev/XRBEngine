for %%C in (GLFractal GLGlass GLLife GLLights GLObjViewer GLPoints) do (
(
echo Manifest-Version: 1.0
echo Main-Class: com.xrbpowered.gl.examples.%%C
echo Class-Path: 
echo  xrbengine.jar 
echo  lib/lwjgl.jar 
echo  lib/lwjgl_util.jar 
echo  lib/javax.json-1.0.jar 
) > manifest.txt
jar cvmf manifest.txt %%C.jar -C bin com/xrbpowered/gl/examples
)
del manifest.txt
