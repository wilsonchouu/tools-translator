Set vbs = CreateObject("Wscript.Shell")
vbs.run "cmd /c java -Dfile.encoding=utf-8 -jar translator.jar",vbhide