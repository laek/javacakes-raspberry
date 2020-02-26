# The Javacakes RaspBellry Pi Smart Doorbell

Smart doorbell built in Java, using Raspberry Pi hardware. When the doorbell is activated a picture is taken and sent to the owners email address, along with a link to a web page giving the option to ignore the doorbell or start a video call. The video call is hosted using Google Duo, and allows the owner to speak with whoever is at the door.

## Tech Used
- Hardware
    - Raspberry Pi 4
    - Raspberry Pi camera v2
    - BreadBoard
    - LED
    - Button
    - Buzzer
    - USB Microphone
    - Wireless Speaker
- Software
    - Java
    - Motion
    - Pi4J
    - Google Duo

## Setup
- Setup Raspberry Pi
- Download Google Duo on Pi
- In Pi's terminal update display settings by typing `export DISPLAY-:0.0` and find IP address by typing `hostname -I`
- Run `.jar` file
- Access web page at `PI-IP-ADDRESS:4567`
