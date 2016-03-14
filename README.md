<h3>Java client for XINTEST HT-2000 CO2 monitor</h3>

Contains console, GUI and MQTT clients<br/>
<br/>
To run it just download zip file for latest release (https://github.com/eschava/HT2000-java/releases/latest) and unzip it. <br/>
Now you can run any .bat file  and check result<br/>
<br/>
Parameters of MQTT client are inside mqtt.bat file

<h5> USB error 3: Unable to claim interface: Access denied (insufficient permissions) </h5>
Please try solution from <a href="http://stackoverflow.com/questions/28884817/usb4java-library-error-while-claiming-an-interface">stackoverflow</a><br/>
If it doesn't work you can try next workaround:<br/>
add -DUSB.ClaimInterface=false parameter to the .sh(.bat) file right after "java" (with spaces of course)<br/>
