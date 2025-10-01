# **ESP Library2 v1.0 - March 19, 2020**
* Added support for ESP Specification 3.005 (includes V1 Gen2)
* Complete rewrite of the ESP Library. 
    * To interact with a V1, create an instance of the ESPClient and register a ESPClientListener to receive ESP data.
    * To scan for VRI Bluetooth device use the BluetoothScanner.
        *Register a BTScanListener to received scanned device.

# **ESP Library2 v2.0.1 - January 26, 2021**
* Added support for reading the RSSI of the connected remote BLE device.
* Fixed a bug in InfDisplayData that falsely indicated active alerts when in use with a V1 Gen2 thats operating with Custom Frequencies enabled. The method isAlertPresent() has been marked deprecated.
* The ESP packet ResponseSerialNumber has been refactored.
* BluetoothScanner was modify to address potential NPEs.
* The gradle version for HelloV1 has been changed to '4.0.1'

# **ESP Library2 v2.0.2 - October 29, 2021**
* Added support for remoting adjusting the V1 Gen2's volume settings. **(Only supported on versions V4.1027 and above)**
* Fixed a race-condition in V1connectionLEWrapper's disconnect(boolean) method that could potentialy result in a connection loss callback instead of a disconnected callback.
* Fixed a bug that allowed for a NullPointerException to be thrown while attempting to get the BluetoothGattCharacteristics from the V1connection LE BluetoothGattService.

# **ESP Library2 v2.0.3 - September 20, 2023**
* Added support for setting Fast Laser Detection in the User Bytes. **(Only supported on versions V4.1031 and above)**
* Added support for setting Ka Always Priority in the User Bytes. **(Only supported on versions V4.1031 and above)**

# **ESP Library2 v2.0.4 - March 4, 2024**
* Added support for setting Ka sensitivity in the User Bytes. **(Only supported on versions V4.1032 and above)**
* Added support for detecting junked out alerts in respAlertData. **(Only supported on versions V4.1031 and above)**
* Added support for leaving the Bluetooth indicator on when turning off the main display on the V1 Gen2. **(Only supported on versions V4.1031 and above)**

# **ESP Library2 v2.0.5 - September, 2024**
* Added support for turning the Startup Sequence on or off. **(Only supported on versions V4.1035 and above)**
* Added support for turning the Resting Display on or off. **(Only supported on versions V4.1035 and above)**
* Added support for turning BSM Plus on or off. **(Only supported on versions V4.1035 and above)**

# **ESP Library2 v2.0.6 - December, 2024**
* Added support for the Tech Display. This includes adding a user settings object for the Tech Display and providing the option to specify a target device on some existing functions.
* Added support for turning Auto Mute on or off. **(Only supported on versions V4.1036 and above)**
* Added support for displaying the current volume setting on the V1. **(Only supported on versions V4.1036 and above)**


# **ESP Library2 v2.0.7 - September, 2025**
* Added support for Photo radar settings in the user bytes **(Only supported on versions V4.1037 and above)**
* Added support for Photo radar type identification in the alert data **(Only supported on versions V4.1037 and above)**
* Added support for adjusting X and K band sensitivity **(Only supported on versions V4.1037 and above)**
* Added support for reading the saved and temporary volume together in the reqAllVolume packet. Temporary volume changes can be made be clearing the Save Volume bit in Aux 0 of the reqWriteVolume packet **(Only supported on versions V4.1037 and above)**
* Added the display active flag to the infDisplayData packet to indicate when the V1 display is active, or not "resting" **(Only supported on versions V4.1037 and above)**
* Added support for the resting display feature on the Tech Display **(Only supported on versions T1.0001 and above)**
* Added support for the extended frequency timeout feature on the Tech Display **(Only supported on versions T1.0001 and above)**

# **ESP Library2 v2.0.8 - September, 2025**
* Fixed an issue causing alerts on V1 Gen1 versions to be reported as photo**

# **ESP Library2 v2.0.9 - October, 2025**
* Updated specification to 3.014 to add the Keep Curent on on BT Disconnect bit to reqWriteVolume**
* Added getBytes to AlertData**
* Added SEVEN_SEG_VALUE_P for parsing the bogey counter**
