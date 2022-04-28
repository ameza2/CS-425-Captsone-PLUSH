//#include <LiquidCrystal_I2C.h>

#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WiFiMulti.h>
#include <ESP8266mDNS.h>
#include <ESP8266WebServer.h>
#include <WiFiUDP.h>

char plush_id[255] = "789123456";

ESP8266WiFiMulti wifiMulti;     // Create an instance of the ESP8266WiFiMulti class, called 'wifiMulti'

//https://arduino-esp8266.readthedocs.io/en/latest/esp8266wifi/udp-examples.html
WiFiUDP udp; // Create a UDP address for recieving broadcasts
unsigned int udpPort = 4210;
char incomingPacket[256];

int musicVol = -1;
int hugSen = -1;
int currSong = -1;
int alert = 0;
enum CMD {VOL, HUG, TGM, SEN};
//using enum CMD;
ESP8266WebServer server(80);    // Create a webserver object that listens for HTTP request on port 80

//LiquidCrystal_I2C lcd(0x27, 16, 2); // (default address, rows, columns)


void handleRoot();              // function prototypes for HTTP handlers
void handleLogin();
void handleNotFound();

void valToByte(int* dbp, int val);

String prevData;

void setup(void) {
  pinMode(24, OUTPUT);
  pinMode(25, OUTPUT);
  pinMode(26, OUTPUT);
  pinMode(27, OUTPUT);
  pinMode(28, OUTPUT);
  pinMode(29, OUTPUT);
  pinMode(30, OUTPUT);
  pinMode(31, OUTPUT);
  pinMode(32, OUTPUT);
  pinMode(33, OUTPUT);
  Serial.begin(115200);         // Start the Serial communication to send messages to the computer
  delay(10);
  Serial.println('\n');
  Serial.println("Version Code: 0011"); // Debugging, just to see if reset worked

  randomSeed(analogRead(0)); // Set up random seed (pin 0 needs to be disconnected)

  wifiMulti.addAP("LAPTOP-3BOTE01B", "IHopeZoeWinsXD");   // add Wi-Fi networks you want to connect to
  //wifiMulti.addAP("ssid_from_AP_2", "your_password_for_AP_2");
  //wifiMulti.addAP("ssid_from_AP_3", "your_password_for_AP_3");

  Serial.println("Connecting ...");
  int i = 0;
  while (wifiMulti.run() != WL_CONNECTED) { // Wait for the Wi-Fi to connect: scan for Wi-Fi networks, and connect to the strongest of the networks above
    delay(250);
    Serial.print('.');
  }
  Serial.println('\n');
  Serial.print("Connected to ");
  Serial.println(WiFi.SSID());               // Tell us what network we're connected to
  Serial.print("IP address:\t");
  Serial.println(WiFi.localIP());            // Send the IP address of the ESP8266 to the computer

  if (MDNS.begin("esp8266")) {              // Start the mDNS responder for esp8266.local
    Serial.println("mDNS responder started");
  } else {
    Serial.println("Error setting up MDNS responder!");
  }

  server.on("/", HTTP_GET, handleRoot);        // Call the 'handleRoot' function when a client requests URI "/"
  server.onNotFound(handleNotFound);           // When a client requests an unknown URI (i.e. something other than "/"), call function "handleNotFound"

  server.begin();                            // Actually start the server
  Serial.println("HTTP server started");

  udp.begin(udpPort);

  //lcd.begin();
  //lcd.backlight();
  //lcd.setCursor(0,0);
  //lcd.print(WiFi.localIP()); // print IP on LCD
}

void loop(void) {
  server.handleClient();
  if ( server.hasArg("data") ) {
    String data = server.arg("data");
    if (prevData != data) { // only print when something changes
      //lcd.setCursor(0,0);
      //lcd.print("                ");
      //lcd.setCursor(0,0);
      Serial.println(data);
      //lcd.print(data);
    }
    server.send(200, "text/plain", server.arg("data"));
    prevData = data;
  }

  int packetSize = udp.parsePacket();
  if (packetSize)
  {

    Serial.printf("Received %d bytes from %s, port %d\n", packetSize, udp.remoteIP().toString().c_str(), udp.remotePort());
    int len = udp.read(incomingPacket, 255);
    if (len > 0)
    {
      incomingPacket[len] = '\0';
    }
    Serial.printf("UDP packet contents: %s\n", incomingPacket);

    char cmd[5]; // Get the command needed
    memcpy(cmd, &incomingPacket[0], 4);
    cmd[4] = '\0';

    char action[255]; // Get the action
    memcpy(action, &incomingPacket[5], 255);


    //=====================================================================================
    // Command CONN: App wants to connect to unit, send IP of this unit if the ID's match.
    //=====================================================================================
    if (strcmp(cmd, "CONN") == 0) {
      if (strcmp(action, plush_id) == 0) {
        udp.beginPacket(udp.remoteIP(), udp.remotePort());
        udp.write(WiFi.localIP().toString().c_str());
        udp.endPacket();
      }
    }

    //=====================================================================================
    // Command UPDT: App asks for updates on the unit's button set volume and hug sensitivity.
    //=====================================================================================
    if (strcmp(cmd, "UPDT") == 0) {
      Serial.printf("Update Requested");

      // PARSING
      char newHugSen[255];
      char newMusicVol[255];
      char newMusicSong[255];
      newHugSen[0] = '\0';
      newMusicVol[0] = '\0';
      newMusicSong[0] = '\0';
      int t = 0;

      for (int i = 0; i < 255; i++) {
        if (action[i] == '/') {
          t++;
        }
        if (action[i] == '\0') {
          break;
        }
        if (isdigit(action[i])) {
          switch (t) {
            case 0: // Probably can be written nicer
              char newChar0[1];
              newChar0[0] = action[i];
              strcat(newHugSen, newChar0);
              break;
            case 1:
              char newChar1[1];
              newChar1[0] = action[i];
              strcat(newMusicVol, newChar1);
              break;
            case 2:
              char newChar2[1];
              newChar2[0] = action[i];
              if (newChar2[0] == '1') {
                alert = 0;
              }
              break;
            case 3:
              char newChar3[1];
              newChar3[0] = action[i];
              strcat(newMusicSong, newChar3);
            default:
              break;
          }
        }
      }

      if (hugSen == -1 || musicVol == -1) {
        hugSen = atoi(newHugSen);
        musicVol = atoi(newMusicVol);

        if (newMusicSong[0] != '\0') {
          setMusic(atoi(newMusicSong));
        }
      }


      char packetToSend[256];
      sprintf(packetToSend, "HS: %d / MV: %d / AL: %d", hugSen, musicVol, alert);

      udp.beginPacket(udp.remoteIP(), udp.remotePort());
      udp.write(packetToSend);
      udp.endPacket();

      // FOR TESTING PURPOSES ONLY: The app will randomly alert.

      long rand = random(1000);
      if (rand < 10) {
        alert = 1;
      }
      if (rand > 990) {
        alert = 2;
      }

    }

    //=====================================================================================
    // Command HSEN: App orders unit to adjust the hug sensitivity.
    //=====================================================================================
    if (strcmp(cmd, "HSEN") == 0) {
      hugSen = atoi(action);
      Serial.printf("New sensitivity: %d\n", hugSen);
      udp.beginPacket(udp.remoteIP(), udp.remotePort());
      udp.write(incomingPacket);
      udp.endPacket();
    }

    //=====================================================================================
    // Command MVOL: App orders unit to change the volume of the speakers.
    //=====================================================================================
    if (strcmp(cmd, "MVOL") == 0) {
      musicVol = atoi(action);
      Serial.printf("New volume: %d\n", musicVol);
      udp.beginPacket(udp.remoteIP(), udp.remotePort());
      udp.write(incomingPacket);
      udp.endPacket();
    }

    //=====================================================================================
    // Command HUGP: App orders bear to hug/stop hugging patient.
    //=====================================================================================
    if (strcmp(cmd, "HUGP") == 0) {
      int toHug = atoi(action);

      if (toHug == 1) {
        startHug();
      }
      else {
        stopHug();
      }

      udp.beginPacket(udp.remoteIP(), udp.remotePort());
      udp.write(incomingPacket);
      udp.endPacket();
    }

    //=====================================================================================
    // Command PMUS: App orders music to play/stop playing from speakers.
    //=====================================================================================
    if (strcmp(cmd, "PMUS") == 0) {
      int toPlay = atoi(action);

      if (toPlay == 1) {
        startMusic();
      }
      else {
        stopMusic();
      }

      udp.beginPacket(udp.remoteIP(), udp.remotePort());
      udp.write(incomingPacket);
      udp.endPacket();
    }
    //=====================================================================================
    // Command SMUS: App orders unit to change the music to be played.
    //=====================================================================================
    if (strcmp(cmd, "SMUS") == 0) {
      int musToSet = atoi(action);

      setMusic(musToSet);

      udp.beginPacket(udp.remoteIP(), udp.remotePort());
      udp.write(incomingPacket);
      udp.endPacket();
    }

    //=====================================================================================
    // Command ACKA: App acknolwedges that the alert has been called, and will turn off the light.
    //=====================================================================================
    if (strcmp(cmd, "ACKA") == 0) {

      acknowledgeAlert();

      udp.beginPacket(udp.remoteIP(), udp.remotePort());
      udp.write(incomingPacket);
      udp.endPacket();
    }
  }
}

void handleRoot() {                          // When URI / is requested, send a web page with a button to toggle the LED
  if ( server.hasArg("data") ) {
    Serial.println(server.arg("data"));
    server.send(200, "text/plain", server.arg("data"));
  }
}

void handleNotFound() {
  server.send(404, "text/plain", "404: Not found"); // Send HTTP status 404 (Not Found) when there's no handler for the URI in the request
}

//---------------------------------------------------------
// This is where the hardware code comes in
//---------------------------------------------------------

void startHug() {
  Serial.printf("Started Hug!\n");
  sendMessageToMain(HUG, 0, true);
}

void stopHug() {
  Serial.printf("Stopped Hug!\n");
  sendMessageToMain(HUG, 0, false);
}

void startMusic() {
  Serial.printf("Started Music!\n");
  sendMessageToMain(TGM, 0, true);
}

void stopMusic() {
  Serial.printf("Stopped Music!\n");
  sendMessageToMain(TGM, 0, false);
}

void setMusic(int m) {
  Serial.printf("Set Music: %d\n", m);
}

void acknowledgeAlert() {
  Serial.printf("Alert acknlowedged!\n");
}

void valToByte(int* dbp, int val) {
  if (val > 63) return;
  dbp[5] = LOW;
  dbp[4] = LOW;
  dbp[3] = LOW;
  dbp[2] = LOW;
  dbp[1] = LOW;
  dbp[0] = LOW;
  if (val > 31) {
    dbp[5] = HIGH;
    val -= 32;
  }
  if (val > 15) {
    dbp[4] = HIGH;
    val -= 16;
  }
  if (val > 7) {
    dbp[3] = HIGH;
    val -= 8;
  }
  if (val > 3) {
    dbp[2] = HIGH;
    val -= 4;
  }
  if (val > 1) {
    dbp[1] = HIGH;
    val -= 2;
  }
  if (val == 1) {
    dbp[0] = HIGH;
  }
}
void sendMessageToMain(CMD command, int value, bool optionalFlag) {
  int pinToInterrupt = 0;
  int digitalBytePins[] = {24, 25, 26, 27, 28, 29, 30, 31, 32, 33};
  /*
    0|0|0|0|000000
    ^ ^ ^ ^ ^
    | | | | |__Byte array to send data
    | | | |____ flag for hug sensitivity
    | | |______ flag for toggle music command
    | |________ flag for volume command
    |__________ flag for hug command
  */
  switch (command) {
    case HUG:
      digitalWrite(digitalBytePins[0], HIGH);
      digitalWrite(digitalBytePins[1], LOW);
      digitalWrite(digitalBytePins[2], LOW);
      digitalWrite(digitalBytePins[3], LOW);
      digitalWrite(digitalBytePins[4], LOW);
      digitalWrite(digitalBytePins[5], LOW);
      digitalWrite(digitalBytePins[6], LOW);
      digitalWrite(digitalBytePins[7], LOW);
      digitalWrite(digitalBytePins[8], LOW);
      digitalWrite(digitalBytePins[9], LOW);
      if (optionalFlag) {
        digitalWrite(digitalBytePins[9], HIGH);
      }
      break;
    case VOL:
      digitalWrite(digitalBytePins[0], LOW);
      digitalWrite(digitalBytePins[1], HIGH);
      digitalWrite(digitalBytePins[2], LOW);
      digitalWrite(digitalBytePins[3], LOW);
      valToByte(digitalBytePins, value);
      break;
    case TGM:
      digitalWrite(digitalBytePins[0], LOW);
      digitalWrite(digitalBytePins[1], LOW);
      digitalWrite(digitalBytePins[2], HIGH);
      digitalWrite(digitalBytePins[3], LOW);
      break;
    case SEN:
      digitalWrite(digitalBytePins[0], LOW);
      digitalWrite(digitalBytePins[1], LOW);
      digitalWrite(digitalBytePins[2], LOW);
      digitalWrite(digitalBytePins[3], HIGH);
      valToByte(digitalBytePins, value);
      break;
  }
  digitalWrite(pinToInterrupt, HIGH);
}
