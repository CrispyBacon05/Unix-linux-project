#!/bin/bash
#stackoverflow said this hides the cursor so it look cleaner
tput civis
#catch ctrl-c so my terminal doesn't stay broken after I quit
trap "tput cnorm; clear; exit" INT TERM

echo "compiling java code......"
javac WslMonitor.java

#$? holds the result of the last command: 0 means no errors hopefully
if [ $? -eq 0 ]; then
# actually run the program
java WslMonitor
else
echo "compilation failed. check ur syntac"

#put the cursor back so I can see what I'm typing
tput cnorm 
fi
