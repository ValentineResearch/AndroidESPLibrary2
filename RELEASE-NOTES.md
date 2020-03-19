# **ESP Library2 v1.0 – March 19, 2020**
* Added support for ESP Specification 3.005 (includes V1 Gen2)
* Complete rewrite of the ESP Library. 
    * To interact with a V1, create an instance of the ESPClient and register a ESPClientListener to receive ESP data.
    * To scan for VRI Bluetooth device use the BluetoothScanner.
        * Register a BTScanListener to received scanned devices.
