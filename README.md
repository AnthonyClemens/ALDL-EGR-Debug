# ALDL-EGR-Debug
A Java application used to go through an ALDL log, for EGR Debugging on GM OBD-1 ECU's. WinALDL is used to create the logfiles compatible with this program found at https://winaldl.joby.se/. 
You must remove the header from the logfile so that it starts with the first valid second of data logging. This code was tested on a 1227170 ecu out of an '88 Fiero Formula. 
To run, move the .jar file to the directory that you have your logfiles stored in and run `java -jar ALDLEGRDebug.jar` and you will be prompted to either select a file to analyze or quit. 
Example logfiles are shown, two with data that is readable, and one that still has the header included.
