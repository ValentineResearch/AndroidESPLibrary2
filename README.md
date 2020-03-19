# Android ESP Library
The Extended Serial Protocol (ESP) is a  communications protocol defined by Valentine Research. It enables two-way communication and data sharing between the Valentine One (V1) radar locator and other ESP-enabled devices. In addition to handling the setup and teardown of Bluetooth connections, this library will do the work of handling the ESP requirements and parsing the ESP packet data into a more usable format.  
## ESP Android Basics
The main interface to the Android ESP Library is through the ESPClient class. The ESPClient will handle all Bluetooth system calls and all ESP packet construction and parsing. The library will use its own threads to send requests to the V1connection and to read the ESP data from the V1connection. Data is transferred from the library to the host app using callbacks. Continuous data, such as alert and display data, requires the app to register an ESPClientListener when constructing an ESPClient. The registered client listener be called whenever display data is received. The registered client listener will be called whenever a complete alert table is recieved and not for each alert data packet. One time data, such as a version request, does not require a separate registration method. Instead, the request method takes an additional  ESPRequestedDataListener parameter that will be invoked when the requested data has been received or an error occurs.
## Sample Integration
The Hello V1 app offers a sample integration to demonstrate how easy it is to start communicating with the V1. The following functionality is demonstrated:
* Determine if the Android device supports Bluetooth Low Energy (BLE).
* Connect and disconnect from a V1connection or V1connection LE.
* Register for display, alert data, general ESP data recevied from a V1connection.
* Read the firmware version and current custom sweeps from the V1. This demonstrates the techniques used to send a request to the V1 and to receive the response.
