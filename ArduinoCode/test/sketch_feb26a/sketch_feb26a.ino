#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
String command; //String to store app command state.
const char* ssid = "ESP8266";
ESP8266WebServer server(80);
void setup() { 
    pinMode(13, OUTPUT);
    digitalWrite(13,HIGH);
    Serial.begin(115200);
    // Connecting WiFi
    WiFi.mode(WIFI_AP);
    WiFi.softAP(ssid);
    IPAddress myIP = WiFi.softAPIP();
    Serial.print("AP IP address: ");
    Serial.println(myIP);
    // Starting WEB-server
    server.on ( "/", HTTP_handleRoot );
    server.onNotFound ( HTTP_handleRoot );
    server.begin();
}
void loop() {
    server.handleClient();
    command = server.arg("data");
    if(command!=""){
        if(command == "1"){
        digitalWrite(13, LOW);
        Serial.println("Led ON");
   } else{
        digitalWrite(13, HIGH);
        Serial.println("Led OFF");
   }
}
delay(5);
}
void HTTP_handleRoot(void) {
    if( server.hasArg("data") ){
        Serial.println(server.arg("data"));
    }
    server.send ( 200, "text/html", "" );
    delay(1);
}
